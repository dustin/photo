// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>

package net.spy.photo;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import net.spy.SpyObject;

/**
 * Scales images.
 */
public class PhotoImageScaler extends SpyObject {

	private PhotoImage pid=null;
	private byte[] data=null;

	/**
	 * Get an instance of PhotoImageScaler.
	 */
	public PhotoImageScaler(PhotoImage p, byte[] img) {
		super();
		pid=p;
		data=img;
	}

	/**
	 * Get a scaled image as a stream.
	 */
	public InputStream getJpegStream(PhotoDimensions dim, int quality)
		throws Exception {

		return(new ByteArrayInputStream(getScaledImage(dim, quality)));
	}

	/**
	 * Get a scaled jpeg as a byte array input stream.
	 */
	public byte[] getScaledImage(PhotoDimensions dim, int quality)
		throws Exception {

		if(getLogger().isDebugEnabled()) {
			getLogger().debug("Scaling some image to " + dim);
		}

		// Get the original image.
		Image image=Toolkit.getDefaultToolkit().createImage(data);

		// Make the dimensions have the proper aspect ratio
		PhotoDimensions sdim=PhotoUtil.scaleTo(pid.getDimensions(), dim);

		// Scale it
		Image img=image.getScaledInstance(sdim.getWidth(), sdim.getHeight(),
			Image.SCALE_DEFAULT);

		// Write it out
		ByteArrayOutputStream os=new ByteArrayOutputStream();
		JpegEncoder jpg=new JpegEncoder(img, quality, os);
		jpg.compress();

		// flush the images.
		img.flush();
		image.flush();

		return(os.toByteArray());
	}

}
