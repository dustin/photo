// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoDimScaler.java,v 1.4 2002/07/10 03:38:08 dustin Exp $

package net.spy.photo;

/**
 * Calculate a dimension for a set of dimensions to fit within a given box.
 * This will scale down, not up.
 */
public class PhotoDimScaler extends Object {

	/**
	 * Get an instance of PhotoDimScaler with a reference dimension set.
	 */
	private PhotoDimScaler() {
		super();
	}

	/**
	 * Scale a dimension to another dimension.
	 * @param from the dimensions of the source
	 * @param to the maximum dimensions to scale to
	 * @return the largest dimensions of from that fit within to
	 */
	public static PhotoDimensions scaleTo(
		PhotoDimensions from, PhotoDimensions to) {
		float x=(float)to.getWidth();
		float y=(float)to.getHeight();
		float aspect=x/y;
		int newx=to.getWidth();
		int newy=to.getHeight();

		if(from.getWidth() <= newx || from.getHeight() <= newy) {

			newx=from.getWidth();
			newy=(int)((float)newx/aspect);

			// If it exceeds the boundaries, do it the other way.
			if(newx > from.getWidth() || newy > from.getHeight()) {
				newy=from.getHeight();
				newx=(int)((float)newy*aspect);
			}
		}

		PhotoDimensions rv=new PhotoDimensionsImpl(newx, newy);

		// Assertions
		if(rv.getWidth() > from.getWidth()
			|| rv.getHeight() > from.getHeight()) {
			throw new RuntimeException(
				"Results can't be outside of the input box");
		}

		if(rv.getWidth() > to.getWidth() || rv.getHeight() > to.getHeight()) {
			throw new RuntimeException(
				"Results can't be outside of the input size");
		}
		// End assertions

		return(rv);
	}

}
