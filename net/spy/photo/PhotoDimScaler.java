// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoDimScaler.java,v 1.1 2001/12/28 01:54:13 dustin Exp $

package net.spy.photo;

/**
 * Calculate a dimension for a set of dimensions to fit within a given box.
 * This will scale down, not up.
 */
public class PhotoDimScaler extends Object {

	private PhotoDimensions dim=null;

	/**
	 * Get an instance of PhotoDimScaler.
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
		int newx=0;
		int newy=0;

		newx=d.getWidth();
		newy=(int)((float)newx/aspect);

		// If it exceeds the boundaries, do it the other way.
		if(newx > d.getWidth() || newy > d.getHeight()) {
			newy=d.getHeight();
			newx=(int)((float)newy*aspect);
		}

		return(new PhotoDimensionsImpl(newx, newy));
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
