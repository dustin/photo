// Copyright (c) 1999 Dustin Sallings <dustin@spy.net>

package net.spy.photo.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Random;

import net.spy.photo.ImageServerScaler;
import net.spy.photo.PhotoDimensions;
import net.spy.photo.PhotoImage;
import net.spy.photo.PhotoUtil;
import net.spy.util.CloseUtil;

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
	@Override
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
		PhotoDimensions newSize=PhotoUtil.scaleTo(imageSize, dim);

		FileInputStream fin=null;
		FileOutputStream f=null;
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
			f = new FileOutputStream(tmpfilename);
			f.write(in.getData());
			f.flush();
			CloseUtil.close(f);
			f=null;

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
			CloseUtil.close(fin);
			File theFile = new File(tmpfilename);
			theFile.delete();
			theFile = new File(thumbfilename);
			theFile.delete();
		}
		return(new PhotoImage(b));
	}
}
