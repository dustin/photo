/*
 * Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
 *
 * $Id: AlbumRestorable.java,v 1.1 2002/06/28 23:13:09 dustin Exp $
 */

package net.spy.photo.util;

import java.util.*;
import java.io.*;

import org.xml.sax.*;

import net.spy.*;
import net.spy.photo.*;

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
