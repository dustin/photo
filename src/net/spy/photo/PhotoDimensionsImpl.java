// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>
// arch-tag: 04E78E56-5D6D-11D9-981E-000A957659CC

package net.spy.photo;

import java.util.StringTokenizer;

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
		if(st.countTokens() < 2) {
			throw new IllegalArgumentException(dims
				+ " is not in the following format:  WIDTHxHEIGHT");
		}

		width=Integer.parseInt(st.nextToken());
		height=Integer.parseInt(st.nextToken());
	}

	/**
	 * True if the given object is a PhotoDimensions object representing
	 * the same width and height.
	 */
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
	 * @see PhotoDimensions
	 */
	public boolean smallerThan(PhotoDimensions pdim) {
		int thisone=getWidth() * getHeight();
		int thatone=pdim.getWidth()*pdim.getHeight();

		return(thisone < thatone);
	}

	/**
	 * Get the hash code.
	 */
	public int hashCode() {

		int rv=width << 16;
		rv|=height;

		return(rv);
	}

	/**
	 * String me.
	 */
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

	/**
	 * Test me.
	 */
	public static void main(String args[]) throws Exception {
		PhotoDimensionsImpl dimpl=new PhotoDimensionsImpl(args[0]);

		System.out.println(dimpl);
	}

}
