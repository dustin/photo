// Copyright (c) 1999 Dustin Sallings <dustin@spy.net>
// arch-tag: D17013F0-5D6C-11D9-B102-000A957659CC

package net.spy.photo.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;

import java.util.Random;

import net.spy.photo.ImageServerScaler;
import net.spy.photo.PhotoImage;
import net.spy.photo.PhotoDimensions;
import net.spy.photo.PhotoDimScaler;

/**
 * Get an image scaler that scales via an external program.
 */
public class ExternalImageServerScaler extends ImageServerScaler {

	/**
	 * Get it.
	 */
	public ExternalImageServerScaler() {
		super();
	}

	private String dumpIS(InputStream s) {
		String out="";
		try {
			byte b[]=new byte[s.available()];

			s.read(b);
			out=new String(b);
		} catch(Exception e) {
			getLogger().warn("Error dumping InputStream", e);
		}
		out=out.trim();
		if(out.length() < 1) {
			out=null;
		}
		return(out);
	}

	/**
	 * Scale an image with an external command.
	 */
	public PhotoImage scaleImage(PhotoImage in, PhotoDimensions dim)
		throws Exception {

		Random r = new Random();
		String part = "/tmp/image." + Math.abs(r.nextInt());
		String thumbfilename = part + ".tn." + in.getFormat().getExtension();
		String tmpfilename=part + "." + in.getFormat().getExtension();
		byte b[]=null;

		// OK, got our filenames, now let's calculate the new size:
		PhotoDimensions imageSize=new PhotoDimensionsImpl(in.getWidth(),
			in.getHeight());
		PhotoDimensions newSize=PhotoDimScaler.scaleTo(imageSize, dim);

		FileInputStream fin=null;

		try {
			// Need these for the process.
			InputStream stderr=null;
			InputStream stdout=null;
			String tmp=null;

			// Make sure we have a defined convert command.
			tmp=getConf().get("scaler.convert.cmd");
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
			getLogger().info("Running " + command);
			Runtime run = Runtime.getRuntime();
			Process p = run.exec(command);
			stderr=p.getErrorStream();
			stdout=p.getInputStream();
			p.waitFor();

			// Process status.
			if(p.exitValue()!=0) {
				getLogger().warn("Exit value of " + command
					+ " was " + p.exitValue());
			}
			tmp=dumpIS(stderr);
			if(tmp!=null) {
				getLogger().warn("Stderr was as follows:\n" + tmp);
				getLogger().warn("------");
			}
			tmp=dumpIS(stdout);
			if(tmp!=null) {
				getLogger().warn("Stdout was as follows:\n" + tmp);
				getLogger().warn("------");
			}

			File file=new File(thumbfilename);
			b=new byte[(int)file.length()];

			fin = new FileInputStream(file);

			fin.read(b);

		} catch(Exception e) {
			getLogger().warn("Error scaling image", e);
			throw e;
		} finally {
			try {
				if(fin != null) {
					fin.close();
				}
				File f = new File(tmpfilename);
				f.delete();
				f = new File(thumbfilename);
				f.delete();
			} catch(IOException e2) {
				// No need to do anything, that's just cleanup.
			}
		}
		return(new PhotoImage(b));
	}
}
