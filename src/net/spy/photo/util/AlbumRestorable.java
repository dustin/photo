// Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
// arch-tag: 617B4070-5D6E-11D9-94D5-000A957659CC

package net.spy.photo.util;

import java.io.Serializable;

/**
 * Superclass of all backup entries.
 */
public class AlbumRestorable extends Restorable implements Serializable {

	/**
	 * Get an AlbumRestorable.
	 */
	public AlbumRestorable() {
		super("/photo_album_object");

		addPartHandler("keywords");
		addPartHandler("descr");
		addPartHandler("cat");
		addPartHandler("taken");
		addPartHandler("size");
		addPartHandler("addedby");
		addPartHandler("ts");
		addPartHandler("id");
		addPartHandler("width");
		addPartHandler("height");
		addPartHandler("image_data");
		addPartHandler("image_data/image_row");
	}
}
