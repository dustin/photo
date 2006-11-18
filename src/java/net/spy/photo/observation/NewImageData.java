// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>
// arch-tag: B9A09978-DD8A-4998-9F11-0E546CCCC325

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
