// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 1E730BDC-5D6D-11D9-ABA3-000A957659CC

package net.spy.photo;

import java.awt.Image;
import java.awt.Toolkit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import java.rmi.Naming;

import net.spy.SpyObject;

import net.spy.photo.rmi.RemoteImageServer;

/**
 * Scales images.
 */
public class PhotoImageScaler extends SpyObject {

	private PhotoImage pi=null;

	/**
	 * Get an instance of PhotoImageScaler.
	 */
	public PhotoImageScaler(PhotoImage pi) {
		super();
		this.pi=pi;
	}

	/**
	 * Get a scaled image as a stream.
	 */
	public InputStream getJpegStream(PhotoDimensions dim, int quality)
		throws Exception {

		PhotoImage tmp=getScaledImage(dim, quality);
		return(new ByteArrayInputStream(tmp.getData()));
	}

	/**
	 * Get a scaled jpeg as a byte array input stream.
	 */
	public PhotoImage getScaledImage(PhotoDimensions dim, int quality)
		throws Exception {

		if(getLogger().isDebugEnabled()) {
			getLogger().debug("Scaling " + pi + " to " + dim);
		}

		// Get the original image.
		Image image=Toolkit.getDefaultToolkit().createImage(pi.getData());

		// Make the dimensions have the proper aspect ratio
		PhotoDimensions sdim=PhotoDimScaler.scaleTo(pi, dim);

		// Scale it
		Image img=image.getScaledInstance(sdim.getWidth(), sdim.getHeight(),
			Image.SCALE_DEFAULT);

		// Write it out
		ByteArrayOutputStream os=new ByteArrayOutputStream();
		JpegEncoder jpg=new JpegEncoder(img, quality, os);
		jpg.Compress();

		PhotoImage ri=new PhotoImage(os.toByteArray());

		// flush the image.
		img.flush();

		return(ri);
	}

	/**
	 * Testing and what not.
	 */
	public static void main(String args[]) throws Exception {

		int imageId=Integer.parseInt(args[0]);
		PhotoDimensions dim=new PhotoDimensionsImpl(args[1]);

		PhotoConfig conf=PhotoConfig.getInstance();

		RemoteImageServer server=null;
		System.out.println("Connecting to image server.");
		server = (RemoteImageServer)Naming.lookup(conf.get("imageserver"));

		System.out.println("Getting image.");
		PhotoImage pi=server.getImage(imageId, null);
		PhotoImageScaler pis=new PhotoImageScaler(pi);

		System.out.println("Getting scaled image.");
		PhotoImage tmp=pis.getScaledImage(dim, 70);

		System.out.println("Done, calculating with and height.");

		System.out.println("New image size:  "
			+ tmp.getWidth() + "x" + tmp.getHeight());

		FileOutputStream fos=new FileOutputStream(imageId + ".jpg");
		fos.write(tmp.getData());
		fos.close();

		System.exit(0);
	}

}
