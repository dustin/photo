// Copyright (c) 1999 Dustin Sallings <dustin@spy.net>
// $Id: ImageServerImpl.java,v 1.10 2001/10/12 21:45:19 dustin Exp $

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

public class ImageServerImpl extends UnicastRemoteObject
	implements ImageServer {

	protected RHash rhash=null;
	protected SpyConfig conf = null;

	public ImageServerImpl(File config) throws RemoteException {
		super();
		conf=new SpyConfig(config);
	}

	public PhotoImage getImage(int image_id, boolean thumbnail)
		throws RemoteException {
		PhotoImage image_data=null;
		if(rhash==null) {
			getRhash();
		}
		try {
			if(thumbnail) {
				image_data=fetchThumbnail(image_id);
			} else {
				image_data=fetchImage(image_id);
			}
		} catch(Exception e) {
			log("Error fetching image:  " + e);
			e.printStackTrace();
			throw new RemoteException("Error fetching image", e);
		}
		// Calculate the width
		image_data.width();
		return(image_data);
	}

	public void storeImage(int image_id, PhotoImage image)
		throws RemoteException {
		// Make sure we've calculated the width and height
		image.width();
		if(rhash==null) {
			getRhash();
			if(rhash==null) {
				log("Can't get an RHash connection");
				throw new RemoteException("Can't get an RHash connection");
			}
		}
		try {
			rhash.put("photo_" + image_id, image);
		} catch(Exception e) {
			log("Error caching image:  " + e);
			e.printStackTrace();
			throw new RemoteException("Error storing image", e);
		}
	}

	protected String dumpIS(InputStream s) {
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

	protected byte[] makeThumbnail(PhotoImage in, int image_id)
		throws Exception {
		Random r = new Random();
		String part = "/tmp/image." + r.nextInt() + "." + image_id;
		String thumbfilename = part + ".tn.jpg";
		String tmpfilename=part + ".jpg";
		byte b[]=null;

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

			String command=tmp + " " + tmpfilename + " " + thumbfilename;
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
			log("Error making thumbnail:  " + e);
			e.printStackTrace();
			throw new Exception("Error making thumbnail:  " + e);
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
		return(b);
	}

	// Fetch a thumbnail of an image 
	protected PhotoImage fetchThumbnail(int image_id) throws Exception {
		String key="photo_tn_" + image_id;
		PhotoImage pi=null;

		if(rhash!=null) {
			pi=(PhotoImage)rhash.get(key);
		}

		if(pi==null) {
			// Make a thumbnail out of the fullsize image
			byte data[]=makeThumbnail(fetchImage(image_id), image_id);
			pi=new PhotoImage(data);
			if(rhash!=null) {
				rhash.put(key, pi);
			} else {
				log("The rhash is null, unable to cache images.");
			}

			// See if we can record this...
			Connection conn=null;
			try {
				conn=getDBConn();
				PreparedStatement st=conn.prepareStatement(
					"update album set tn_width=?, tn_height=?\n"
						+ " where id=?\n"
					);
				st.setInt(1, pi.width());
				st.setInt(2, pi.height());
				st.setInt(3, image_id);
				st.executeUpdate();
			} catch(Exception e) {
				log("Error updating thumbnail info:  " + e);
				e.printStackTrace();
			} finally {
				freeDBConn(conn);
			}
		}

		return(pi);
	}

	protected void log(String what) {
		System.err.println(what);
	}

	// Fetch an image
	protected PhotoImage fetchImage(int image_id) throws Exception {
		String key=null;
		PhotoImage pi=null;

		key = "photo_" + image_id;

		if(rhash!=null) {
			pi=(PhotoImage)rhash.get(key);
		}

		if(pi==null) {
			Connection photo=null;
			String sdata="";

			try {
				photo=getDBConn();
				String query="select * from image_store where id = ?\n"
					+ " order by line";
				PreparedStatement st = photo.prepareStatement(query);
				st.setInt(1, image_id);
				ResultSet rs = st.executeQuery();

				while(rs.next()) {
					sdata+=rs.getString(3);
				}

			} catch(Exception e) {
				log("Problem getting image:  " + e);
				e.printStackTrace();
				throw new Exception("Problem getting image: " + e);
			} finally {
				freeDBConn(photo);
			}

			// If we got an exception, throw it
			Base64 base64 = new Base64();
			byte data[]=base64.decode(sdata);
			pi=new PhotoImage(data);
			rhash.put(key, pi);
		}

		return(pi);
	}

	protected Connection getDBConn() throws Exception {
		Connection photo;
		String source;

		Class.forName(conf.get("db.driver"));
		source=conf.get("db.url");
		photo = DriverManager.getConnection(source,
			conf.get("db.user"), conf.get("db.pass"));
		return(photo);
	}

	protected void freeDBConn(Connection conn) {
		try {
			conn.close();
		} catch(Exception e) {
			log("Error closing database:  " + e);
			e.printStackTrace();
		}
	}

	public boolean ping() throws RemoteException {
		return(true);
	}

	// Get a cache object server
	protected void getRhash() {
		try {
			rhash = new RHash(conf.get("rhash.url"));
		} catch(Exception e) {
			log("Error getting RHash");
			e.printStackTrace();
			rhash=null;
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
