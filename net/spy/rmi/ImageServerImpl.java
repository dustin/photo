// Copyright (c) 1999 Dustin Sallings <dustin@spy.net>
// $Id: ImageServerImpl.java,v 1.12 2002/02/20 11:32:12 dustin Exp $

package net.spy.rmi;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.RMISecurityManager;
import java.rmi.server.UnicastRemoteObject;

import java.util.*;
import java.lang.*;
import java.io.*;
import java.sql.*;

import net.spy.*;
import net.spy.photo.*;
import net.spy.util.*;

/**
 * Implementation of the image server.
 */
public class ImageServerImpl extends UnicastRemoteObject
	implements ImageServer {

	private RHash rhash=null;
	private SpyConfig conf = null;
	private boolean debug=false;

	/**
	 * Get an ImageServerImpl using the given config.
	 */
	public ImageServerImpl(File config) throws RemoteException {
		super();
		conf=new SpyConfig(config);
	}

	public PhotoImage getImage(int image_id, PhotoDimensions dim)
		throws RemoteException {
		PhotoImage image_data=null;
		debug("Requested image " + image_id + " at scale " + dim);
		try {

			if(dim==null) {
				image_data=fetchImage(image_id);
			} else {
				image_data=fetchScaledImage(image_id, dim);
			}
		} catch(Exception e) {
			log("Error fetching image:  " + e);
			e.printStackTrace();
			throw new RemoteException("Error fetching image", e);
		}
		// Calculate the width
		image_data.getWidth();
		return(image_data);
	}

	private PhotoImage fetchScaledImage(int image_id, PhotoDimensions dim)
		throws Exception {

		PhotoImage pi=null;
		String key = "photo_" + image_id + "_"
			+ dim.getWidth() + "x" + dim.getHeight();

		// Try cache first
		getRhash();
		pi=(PhotoImage)rhash.get(key);
		if(pi==null) {
			// Not in cache, get it
			pi=fetchImage(image_id);
			// Scale it
			pi=scaleImage(pi, image_id, dim);
			// Store it
			rhash.put(key, pi);
			log("Stored " + image_id + " with key " + key);
		} else {
			debug("Found " + key + "(" + key.hashCode() + ") in cache.");
		}

		return(pi);
	}

	public PhotoImage getImage(int image_id, boolean thumbnail)
		throws RemoteException {
		PhotoDimensions dim=null;
		PhotoConfig sconf=new PhotoConfig();
		if(thumbnail) {
			dim=new PhotoDimensionsImpl(sconf.get("thumbnail_size", "220x146"));
		}
		return(getImage(image_id, dim));
	}

	public void storeImage(int image_id, PhotoImage image)
		throws RemoteException {
		// Make sure we've calculated the width and height
		image.getWidth();
		getRhash();
		try {
			rhash.put("photo_" + image_id, image);
		} catch(Exception e) {
			log("Error caching image:  " + e);
			e.printStackTrace();
			throw new RemoteException("Error storing image", e);
		}
	}

	private String dumpIS(InputStream s) {
		String out="";
		try {
			byte b[]=new byte[s.available()];

			s.read(b);
			out=new String(b);
		} catch(Exception e) {
			log("Error dumping InputStream:  " + e);
			e.printStackTrace();
		}
		out=out.trim();
		if(out.length() < 1) {
			out=null;
		}
		return(out);
	}

	private PhotoImage scaleImage(
		PhotoImage in, int id, PhotoDimensions dim) throws Exception {
		Random r = new Random();
		String part = "/tmp/image." + id + "." + Math.abs(r.nextInt());
		String thumbfilename = part + ".tn.jpg";
		String tmpfilename=part + ".jpg";
		byte b[]=null;

		// OK, got our filenames, now let's calculate the new size:
		PhotoDimensions imageSize=new PhotoDimensionsImpl(in.getWidth(),
			in.getHeight());
		PhotoDimScaler pds=new PhotoDimScaler(imageSize);
		PhotoDimensions newSize=pds.scaleTo(dim);

		try {
			// Need these for the process.
			InputStream stderr=null;
			InputStream stdout=null;
			String tmp=null;

			// Make sure we have a defined convert command.
			tmp=conf.get("convert.cmd");
			if(tmp==null) {
				throw new Exception("No convert command has been defined!");
			}

			// Write the image data to a temporary file.
			FileOutputStream f = new FileOutputStream(tmpfilename);
			f.write(in.getData());
			f.flush();
			f.close();

			String command=tmp + " -geometry "
				+ newSize.getWidth() + "x" + newSize.getHeight()
				+ " " + tmpfilename + " " + thumbfilename;
			log("Running " + command);
			Runtime run = Runtime.getRuntime();
			Process p = run.exec(command);
			stderr=p.getErrorStream();
			stdout=p.getInputStream();
			p.waitFor();

			// Process status.
			if(p.exitValue()!=0) {
				log("Exit value was " + p.exitValue());
			}
			tmp=dumpIS(stderr);
			if(tmp!=null) {
				log("Stderr was as follows:\n" + tmp);
				log("------");
			}
			tmp=dumpIS(stdout);
			if(tmp!=null) {
				log("Stdout was as follows:\n" + tmp);
				log("------");
			}

			File file=new File(thumbfilename);
			b=new byte[(int)file.length()];

			FileInputStream fin = new FileInputStream(file);

			fin.read(b);

		} catch(Exception e) {
			log("Error scaling image:  " + e);
			throw e;
		} finally {
			try {
				File f = new File(tmpfilename);
				f.delete();
				f = new File(thumbfilename);
				f.delete();
			} catch(Exception e2) {
				// No need to do anything, that's just cleanup.
			}
		}
		return(new PhotoImage(b));
	}

	private void log(String what) {
		System.err.println(what);
	}

	private void debug(String what) {
		if(debug) {
			System.err.println(what);
		}
	}

	// Fetch an image
	private PhotoImage fetchImage(int image_id) throws Exception {
		String key=null;
		PhotoImage pi=null;

		key = "photo_" + image_id;

		getRhash();
		pi=(PhotoImage)rhash.get(key);

		if(pi==null) {
			Connection photo=null;
			StringBuffer sdata=new StringBuffer();

			try {
				SpyDB db=new SpyDB(new PhotoConfig());
				String query="select * from image_store where id = ?\n"
					+ " order by line";
				PreparedStatement st = db.prepareStatement(query);
				st.setInt(1, image_id);
				ResultSet rs = st.executeQuery();

				while(rs.next()) {
					sdata.append(rs.getString("data"));
				}
				rs.close();
				db.close();

			} catch(Exception e) {
				log("Problem getting image:  " + e);
				e.printStackTrace();
				throw new Exception("Problem getting image: " + e);
			}

			// If we got an exception, throw it
			Base64 base64 = new Base64();
			byte data[]=base64.decode(sdata.toString());
			pi=new PhotoImage(data);
			rhash.put(key, pi);
		} else {
			debug("Found " + key + " in cache.");
		}

		return(pi);
	}

	public boolean ping() throws RemoteException {
		return(true);
	}

	// Get a cache object server
	private void getRhash() throws RemoteException {
		try {
			if(rhash==null) {
				rhash = new RHash(conf.get("rhash.url"));
			}
		} catch(Exception e) {
			log("Error getting RHash");
			e.printStackTrace();
			rhash=null;
			throw new RemoteException("Error getting RHash", e);
		}
	}

	public static void main(String args[]) throws Exception {
		if(args.length < 1) {
			System.err.println("imageserver.conf path not given.");
			throw new Exception("imageserver.conf path not given.");
		}
		ImageServerImpl i=new ImageServerImpl(new File(args[0]));
		Naming.rebind("ImageServer", i);
		System.err.println("ImageServer bound in registry");
	}
}
