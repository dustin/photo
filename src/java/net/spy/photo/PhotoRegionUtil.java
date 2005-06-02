// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>
// arch-tag: A4CBE4CC-7EE0-4792-A16F-54503C8C2DA2

package net.spy.photo;

import net.spy.photo.impl.PhotoRegionImpl;

/**
 * Utilities for working with PhotoRegions.
 */
public final class PhotoRegionUtil extends Object {

	/**
	 * Get an instance of PhotoRegionUtil.
	 */
	private PhotoRegionUtil() {
		super();
	}

	/** 
	 * Scale a region by a factor.
	 * 
	 * @param rin the region
	 * @param factor the factor
	 * @return the region applied to a specific scaling factor
	 */
	public static PhotoRegion scaleRegion(PhotoRegion rin, float factor) {
		PhotoRegion rv=new PhotoRegionImpl(
			(int)(rin.getX() * factor),
			(int)(rin.getY() * factor),
			(int)(rin.getWidth() * factor),
			(int)(rin.getHeight() * factor));
		return(rv);
	}

}
