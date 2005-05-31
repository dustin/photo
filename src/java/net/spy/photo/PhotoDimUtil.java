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
	 * Get the scale factor that will be used to scale the first dimensions to
	 * fit as tightly as possible within the constraints of the second
	 * dimensions.
	 * 
	 * @param from the dimensions to be scaled
	 * @param to the constraints
	 * @return the scaling factor that will scale the dims to the constraints
	 */
	public static float getScaleFactor(PhotoDimensions from,
		PhotoDimensions to) {

		float fromw=(float)from.getWidth();
		float fromh=(float)from.getHeight();
		float tow=(float)to.getWidth();
		float toh=(float)to.getHeight();

		float scaleFactor=tow/fromw;
		if(fromh * scaleFactor > toh) {
			scaleFactor=toh/fromh;
		}

		// Assertions
		if( (fromw * scaleFactor) > tow || (fromh * scaleFactor) > toh) {
			throw new RuntimeException(
				"Results can't be outside of the input box");
		}
		// End assertions

		return(scaleFactor);
	}

	/** 
	 * Scale a set of PhotoDimensions by a specific factor.
	 * 
	 * @param from the source PhotoDimensions
	 * @param factor the factor by which to scale
	 * @return the scaled PhotoDimensions
	 */
	public static PhotoDimensions scaleBy(PhotoDimensions from, float factor) {
		PhotoDimensions rv=new PhotoDimensionsImpl(
			(int)(from.getWidth() * factor),
			(int)(from.getHeight() * factor));
		return(rv);
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

		PhotoDimensions rv=from;

		// This prevents us from scaling down.  We only scale if the
		// constraints are smaller than the from dimensions
		if(to.getWidth() < from.getWidth()
			|| to.getHeight() < from.getHeight()) {

			rv=scaleBy(from, getScaleFactor(from, to));
		}

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
