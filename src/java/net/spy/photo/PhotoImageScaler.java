// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 1E730BDC-5D6D-11D9-ABA3-000A957659CC

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

	private PhotoImage pi=null;

	/**
	 * Get an instance of PhotoImageScaler.
	 */
	public PhotoImageScaler(PhotoImage img) {
		super();
		this.pi=img;
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
		PhotoDimensions sdim=PhotoUtil.scaleTo(pi, dim);

		// Scale it
		Image img=image.getScaledInstance(sdim.getWidth(), sdim.getHeight(),
			Image.SCALE_DEFAULT);

		// Write it out
		ByteArrayOutputStream os=new ByteArrayOutputStream();
		JpegEncoder jpg=new JpegEncoder(img, quality, os);
		jpg.compress();

		PhotoImage ri=new PhotoImage(os.toByteArray());

		// flush the image.
		img.flush();

		return(ri);
	}

}
