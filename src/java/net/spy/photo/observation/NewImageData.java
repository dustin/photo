// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>

package net.spy.photo.observation;

import net.spy.photo.PhotoImageData;

public class NewImageData {

	private PhotoImageData photoImageData=null;

	public NewImageData(PhotoImageData pid) {
		super();
		photoImageData=pid;
	}

	public PhotoImageData getPhotoImageData() {
		return photoImageData;
	}
}
