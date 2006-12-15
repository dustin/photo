// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>

package net.spy.photo.observation;

import net.spy.photo.PhotoImage;
import net.spy.photo.PhotoImageData;

public class NewImageData {

	private PhotoImage photoImage=null;
	private PhotoImageData photoImageData=null;

	public NewImageData(PhotoImageData pid, PhotoImage pi) {
		super();
		photoImage=pi;
		photoImageData=pid;
	}

	public PhotoImage getPhotoImage() {
		return photoImage;
	}
	public PhotoImageData getPhotoImageData() {
		return photoImageData;
	}
}
