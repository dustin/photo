// Copyright (c) 1999  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoImage.java,v 1.8 2002/02/21 10:41:32 dustin Exp $

package net.spy.photo;

import java.lang.*;
import java.util.*;
import java.io.Serializable;

/**
 * Represents the image itself, yo.
 */
public class PhotoImage extends Object
	implements Serializable, PhotoDimensions {

	// Image data
	private byte image_data[]=null;

	private int _width=-1;
	private int _height=-1;

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

	/**
	 * String me.
	 */
	public String toString() {
		return("PhotoImage@" + getWidth() + "x" + getHeight());
	}

	/**
	 * Deal with comparisons in different ways (currently, only
	 * PhotoDimensions and default compares work).
	 */
	public boolean equals(Object o) {
		boolean rv=false;

		if(o instanceof PhotoImage) {
			rv=super.equals(o);
		} else if(o instanceof PhotoDimensions) {
			PhotoDimensions odim=(PhotoDimensions)o;

			rv=( (odim.getWidth() == getWidth())
				&& (odim.getHeight() == getHeight()));
		}

		return(rv);
	}

	/**
	 * @see PhotoDimensions
	 */
	public boolean smallerThan(PhotoDimensions pdim) {
		int thisone=getWidth() * getHeight();
		int thatone=pdim.getWidth()*pdim.getHeight();

		return(thisone < thatone);
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
	private int getIntValue(int which) {
		int i=(int)(image_data[which]&0xff);
		return(i);
	}

	private void calcDim() throws Exception {
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
