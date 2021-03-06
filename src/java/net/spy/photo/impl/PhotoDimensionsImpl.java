// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>

package net.spy.photo.impl;

import java.util.StringTokenizer;

import net.spy.photo.PhotoDimensions;

/**
 * A class that contains only a width and height.
 */
public class PhotoDimensionsImpl
	implements PhotoDimensions, java.io.Serializable {

	private int width=0;
	private int height=0;

	/**
	 * Get a dimensions set.
	 */
	public PhotoDimensionsImpl(int w, int h) {
		super();
		if(w < 0) {
			throw new IllegalArgumentException("width < 0:  " + w);
		}
		if(h < 0) {
			throw new IllegalArgumentException("height < 0:  " + h);
		}
		this.width=w;
		this.height=h;
	}

	/**
	 * Get a dimension set from a string describing the dimensions.
	 * i.e.: 640x480.
	 */
	public PhotoDimensionsImpl(String dims) {
		super();

		StringTokenizer st=new StringTokenizer(dims, "x");
		if(st.countTokens() != 2) {
			throw new IllegalArgumentException(dims
				+ " is not in the following format:  WIDTHxHEIGHT");
		}

		width=Integer.parseInt(st.nextToken());
		height=Integer.parseInt(st.nextToken());

		if(width < 0) {
			throw new IllegalArgumentException("width < 0:  " + width);
		}
		if(height < 0) {
			throw new IllegalArgumentException("height < 0:  " + height);
		}
	}

	/**
	 * True if the given object is a PhotoDimensions object representing
	 * the same width and height.
	 */
	@Override
	public boolean equals(Object o) {
		boolean rv=false;

		if(o instanceof PhotoDimensions) {
			PhotoDimensions odim=(PhotoDimensions)o;

			rv=( (odim.getWidth() == getWidth())
				&& (odim.getHeight() == getHeight()));
		}

		return(rv);
	}

	/**
	 * Get the hash code.
	 */
	@Override
	public int hashCode() {

		int rv=width << 16;
		rv|=height;

		return(rv);
	}

	/**
	 * String me.
	 */
	@Override
	public String toString() {
		// return("PhotoDimensionsImpl@" + width + "x" + height);
		// Try a strictly functional toString for now.
		return(width + "x" + height);
	}

	/**
	 * Get the width of the set.
	 */
	public int getWidth() {
		return(width);
	}
	/**
	 * Get the height of the set.
	 */
	public int getHeight() {
		return(height);
	}

}
