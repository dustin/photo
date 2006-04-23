// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: E1977522-5D6C-11D9-A68F-000A957659CC

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

	/**
	 * Scale it in 100% Java.
	 */
	public PhotoImage scaleImage(PhotoImage in, PhotoDimensions dim)
		throws Exception {

		PhotoImageScaler pis=new PhotoImageScaler(in);
		return(pis.getScaledImage(dim,
				PhotoConfig.getInstance().getInt("jpeg_quality", 85)));
	}

}

