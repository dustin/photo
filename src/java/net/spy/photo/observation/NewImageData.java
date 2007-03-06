// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>

package net.spy.photo.observation;

import net.spy.photo.PhotoImage;

public class NewImageData {

	private PhotoImage photoImageData=null;

	public NewImageData(PhotoImage pid) {
		super();
		photoImageData=pid;
	}

	public PhotoImage getPhotoImage() {
		return photoImageData;
	}
}
