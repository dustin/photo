// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoDimScaler.java,v 1.4 2002/07/10 03:38:08 dustin Exp $

package net.spy.photo;

/**
 * Calculate a dimension for a set of dimensions to fit within a given box.
 * This will scale down, not up.
 */
public class PhotoDimScaler extends Object {

	private PhotoDimensions dim=null;

	/**
	 * Get an instance of PhotoDimScaler with a reference dimension set.
	 */
	public PhotoDimScaler(PhotoDimensions dim) {
		super();
		this.dim=dim;
	}

	/**
	 * Scale a dimension to another dimension.
	 */
	public PhotoDimensions scaleTo(PhotoDimensions d) {
		float x=(float)dim.getWidth();
		float y=(float)dim.getHeight();
		float aspect=x/y;
		int newx=dim.getWidth();
		int newy=dim.getHeight();

		if(d.getWidth() <= newx || d.getHeight() <= newy) {

			newx=d.getWidth();
			newy=(int)((float)newx/aspect);

			// If it exceeds the boundaries, do it the other way.
			if(newx > d.getWidth() || newy > d.getHeight()) {
				newy=d.getHeight();
				newx=(int)((float)newy*aspect);
			}
		}

		PhotoDimensions rv=new PhotoDimensionsImpl(newx, newy);

		// Assertions
		if(rv.getWidth() > d.getWidth() || rv.getHeight() > d.getHeight()) {
			throw new Error("Results can't be outside of the input box");
		}

		if(rv.getWidth() > dim.getWidth() || rv.getHeight() > dim.getHeight()) {
			throw new Error("Results can't be outside of the input size");
		}
		// End assertions

		return(rv);
	}

	/**
	 * Testing and what not.
	 */
	public static void main(String args[]) throws Exception {
		PhotoDimensionsImpl pdibase=new PhotoDimensionsImpl(
			Integer.parseInt(args[0]), Integer.parseInt(args[1]));
		PhotoDimensionsImpl pdiconv=new PhotoDimensionsImpl(
			Integer.parseInt(args[2]), Integer.parseInt(args[3]));

		PhotoDimScaler pds=new PhotoDimScaler(pdibase);

		PhotoDimensions pd=pds.scaleTo(pdiconv);

		System.out.println(pd);
	}

}


