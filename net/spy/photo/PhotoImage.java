// Copyright (c) 1999  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoImage.java,v 1.2 2000/06/30 04:11:19 dustin Exp $

package net.spy.photo;

import java.lang.*;
import java.util.*;
import java.io.Serializable;

public class PhotoImage extends Object implements Serializable {
	// Meta stuff
	protected int format_version=1;

	// Image data
	protected byte image_data[]=null;

	// empty constructor
	public PhotoImage() {
		super();
	}

	// Constructor mit data
	public PhotoImage(byte data[]) {
		super();
		image_data = data;
	}

	public void setData(byte data[]) {
		image_data=data;
	}

	public byte[] getData() {
		return(image_data);
	}

	public int size() {
		return(image_data.length);
	}
}
