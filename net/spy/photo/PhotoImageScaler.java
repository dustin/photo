// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoImageScaler.java,v 1.5 2002/07/10 03:38:08 dustin Exp $

package net.spy.photo;

import java.awt.Image;
import java.awt.Toolkit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import java.rmi.Naming;

import net.spy.photo.rmi.RemoteImageServer;

/**
 * Scales images.
 */
public class PhotoImageScaler extends Object {

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

		// This is stupid, but I'll fix it at some point.
		if(pi.getFormat()!=PhotoImage.FORMAT_JPEG) {
			throw new Exception("PhotoImageScaler does not handle "
				+ pi.getFormatString() + " yet");
		}

		// Get the original image.
		Image image=Toolkit.getDefaultToolkit().createImage(pi.getData());

		// Make the dimensions have the proper aspect ratio
		PhotoDimScaler pds=new PhotoDimScaler(pi);
		PhotoDimensions sdim=pds.scaleTo(dim);

		// Scale it
		Image img=image.getScaledInstance(sdim.getWidth(), sdim.getHeight(),
			Image.SCALE_DEFAULT);

		// Write it out
		ByteArrayOutputStream os=new ByteArrayOutputStream();
		JpegEncoder jpg=new JpegEncoder(img, quality, os);
		jpg.Compress();

		PhotoImage ri=new PhotoImage(os.toByteArray());

		return(ri);
	}

	/**
	 * Testing and what not.
	 */
	public static void main(String args[]) throws Exception {

		int image_id=Integer.parseInt(args[0]);
		PhotoDimensions dim=new PhotoDimensionsImpl(args[1]);

		PhotoConfig conf=new PhotoConfig();

		RemoteImageServer server=null;
		System.out.println("Connecting to image server.");
		server = (RemoteImageServer)Naming.lookup(conf.get("imageserver"));

		System.out.println("Getting image.");
		PhotoImage pi=server.getImage(image_id, null);
		PhotoImageScaler pis=new PhotoImageScaler(pi);

		System.out.println("Getting scaled image.");
		PhotoImage tmp=pis.getScaledImage(dim, 70);

		System.out.println("Done, calculating with and height.");

		System.out.println("New image size:  "
			+ tmp.getWidth() + "x" + tmp.getHeight());

		FileOutputStream fos=new FileOutputStream(image_id + ".jpg");
		fos.write(tmp.getData());
		fos.close();

		System.exit(0);
	}

}
