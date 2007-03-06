// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>

package net.spy.photo.impl;

import net.spy.photo.ImageServerScaler;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoDimensions;
import net.spy.photo.PhotoImage;
import net.spy.photo.PhotoImageScaler;

/**
 * Scale the image in 100% Java.
 */
public class JavaImageServerScaler extends ImageServerScaler {

	/**
	 * Get an instance of JavaImageServerScaler.
	 */
	public JavaImageServerScaler() {
		super();
	}

	@Override
	public byte[] scaleImage(PhotoImage pid, byte[] in,
			PhotoDimensions dim) throws Exception {
		PhotoImageScaler pis=new PhotoImageScaler(pid, in);
		return(pis.getScaledImage(dim,
				PhotoConfig.getInstance().getInt("jpeg_quality", 70)));
	}

}

