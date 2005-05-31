// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: FAC36821-5D6C-11D9-AF00-000A957659CC

package net.spy.photo;

import net.spy.photo.impl.PhotoDimensionsImpl;

/**
 * Utilities for working with photo dimensions.
 */
public class PhotoDimUtil extends Object {

	private PhotoDimUtil() {
		super();
	}

	/**
	 * Scale a dimension to another dimension.
	 * This will only scale down, not up.
	 *
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

	/** 
	 * Determine whether the first PhotoDimensions instance is smaller than the
	 * second in area.
	 * 
	 * @return true if the area of a is greater than the area of b.
	 */
	public static boolean smallerThan(PhotoDimensions a, PhotoDimensions b) {
		int areaa=a.getWidth() * a.getHeight();
		int areab=b.getWidth() * b.getHeight();
		return(areaa < areab);
	}

}
