// Copyright (c) 1999  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoImage.java,v 1.6 2001/12/28 01:54:13 dustin Exp $

package net.spy.photo;

import java.lang.*;
import java.util.*;
import java.io.Serializable;

/**
 * Represents the image itself, yo.
 */
public class PhotoImage extends Object
	implements Serializable, PhotoDimensions {

	// Meta stuff
	protected int format_version=2;

	// Image data
	protected byte image_data[]=null;

	int _width=-1;
	int _height=-1;

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

	public int getWidth() {
		if(_width<0) {
			try {
				calcDim();
			} catch(Exception e) {
				System.err.println("Error getting dimensions:  " + e);
			}
		}
		return(_width);
	}

	public int getHeight() {
		if(_height<0) {
			try {
				calcDim();
			} catch(Exception e) {
				System.err.println("Error getting dimensions:  " + e);
			}
		}
		return(_height);
	}

	// This sucks, but byte comes back signed.
	protected int getIntValue(int which) {
		int i=(int)image_data[which];
		if(i<0) {
			i+=256;
		}
		return(i);
	}

	protected void calcDim() throws Exception {
		int ch=-1, i=0;
		boolean done=false;

		// Short circuit if it's not a JPEG
		if( ( getIntValue(0) != 0xFF)
			|| ( getIntValue(1) != 0xD8 )
			|| ( getIntValue(2) != 0xFF )
			) {
			throw new Exception("That's not a JPEG");
		}

		// Move forward two.
		i+=2;

		// OK, look at all the tags until we find our image tag.
		while(!done && ch != 0xDA && i<image_data.length) {
			// Look for the next tag
			while(ch!=0xFF) { ch=getIntValue(i++); }
			// Tags can be kinda log, get to the end of it.
			while(ch==0xFF) { ch=getIntValue(i++); }

			// Is this our man?
			if(ch>=0xC0 && ch<= 0xC3) {
				// Jump to the interesting part.
				i+=3;
				_width=(getIntValue(i+2)<<8) | (getIntValue(i+3));
				_height=(getIntValue(i+0)<<8) | (getIntValue(i+1));
				done=true;
			} else {
				// OK, this isn't something we care about.  Figure out the
				// length of this section, skip over it, and let's move on
				// to the next one.
				int length=(getIntValue(i)<<8)|getIntValue(i+1);
				i+=length;
			}
		} // while loop
	}
}
