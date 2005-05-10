// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: FAC36821-5D6C-11D9-AF00-000A957659CC

package net.spy.photo;

import net.spy.photo.impl.PhotoDimensionsImpl;

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
		float x=(float)from.getWidth();
		float y=(float)from.getHeight();
		float aspect=x/y;
		int newx=from.getWidth();
		int newy=from.getHeight();

		if(to.getWidth() <= newx || to.getHeight() <= newy) {

			newx=to.getWidth();
			newy=(int)((float)newx/aspect);

			// If it exceeds the boundaries, do it the other way.
			if(newx > to.getWidth() || newy > to.getHeight()) {
				newy=to.getHeight();
				newx=(int)((float)newy*aspect);
			}
		}

		PhotoDimensions rv=new PhotoDimensionsImpl(newx, newy);

		// Assertions
		if(rv.getWidth() > to.getWidth()
			|| rv.getHeight() > to.getHeight()) {
			throw new RuntimeException(
				"Results can't be outside of the input box");
		}

		if(rv.getWidth() > from.getWidth()
			|| rv.getHeight() > from.getHeight()) {
			throw new RuntimeException(
				"Results can't be outside of the input size");
		}
		// End assertions

		return(rv);
	}

}
