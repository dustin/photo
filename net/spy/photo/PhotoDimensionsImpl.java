//
// $Id: PhotoDimensionsImpl.java,v 1.3 2001/12/29 06:30:07 dustin Exp $

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
	 * String me.
	 */
	public String toString() {
		return("PhotoDimensionsImpl@" + width + "x" + height);
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
