// Copyright (c) 1999  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoImage.java,v 1.21 2003/08/01 06:58:23 dustin Exp $

package net.spy.photo;

import java.io.Serializable;

/**
 * Represents the image itself, yo.
 */
public class PhotoImage extends Object
	implements Serializable, PhotoDimensions {

	// Serialization info
	private static final long serialVersionUID = -581283675761191893l;

	// Image data
	private byte imageData[]=null;

	private int width=-1;
	private int height=-1;

	public static final int FORMAT_UNKNOWN=0;
	public static final int FORMAT_JPEG=1;
	public static final int FORMAT_PNG=2;
	public static final int FORMAT_GIF=3;

	private int format=FORMAT_UNKNOWN;

	/**
	 * Get an empty PhotoImage object.
	 */
	public PhotoImage() {
		super();
	}

	/**
	 * Get a PhotoImage object with binary data representing the image.
	 */
	public PhotoImage(byte data[]) throws PhotoException {
		super();
		setData(data);
	}

	/**
	 * Set the binary data that represents this image.
	 *
	 * @exception PhotoException if the data format is corrupt or invalid
	 */
	public void setData(byte data[]) throws PhotoException {
		imageData=data;
		calcDim();
	}

	/**
	 * Get the binary data that represents this image.
	 */
	public byte[] getData() {
		return(imageData);
	}

	/**
	 * Get the size (in bytes) of this image.
	 */
	public int size() {
		return(imageData.length);
	}

	/**
	 * Get the format of this image.
	 */
	public int getFormat() {
		return(format);
	}

	/**
	 * Get the name of the format of this image.
	 */
	public String getFormatString() {
		String rv="unknown";

		switch(format) {
			case FORMAT_JPEG:
				rv="jpeg";
				break;
			case FORMAT_PNG:
				rv="png";
				break;
			case FORMAT_GIF:
				rv="gif";
				break;
		}

		return(rv);
	}

	/**
	 * String me.
	 */
	public String toString() {
		StringBuffer sb=new StringBuffer(64);

		sb.append("PhotoImage (");
		sb.append(getFormatString());
		sb.append(") ");
		sb.append(getWidth());
		sb.append("x");
		sb.append(getHeight());

		return(sb.toString());
	}

	/**
	 * Deal with comparisons in different ways (currently, only
	 * PhotoDimensions and default compares work).
	 */
	public boolean equals(Object o) {
		boolean rv=false;

		if(o instanceof PhotoImage) {
			rv=super.equals(o);
		} else if(o instanceof PhotoDimensions) {
			PhotoDimensions odim=(PhotoDimensions)o;

			rv=( (odim.getWidth() == getWidth())
				&& (odim.getHeight() == getHeight()));
		}

		return(rv);
	}

	/** 
	 * Get the hash code of this object.
	 *
	 * This method is overridden to be consistent with overridding equals.
	 * 
	 * @return the system identity hash code
	 */
	public int hashCode() {
		return(System.identityHashCode(this));
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
	 * @see PhotoDimensions
	 */
	public int getWidth() {
		return(width);
	}

	/**
	 * @see PhotoDimensions
	 */
	public int getHeight() {
		return(height);
	}

	// This sucks, but byte comes back signed.
	private int getIntValue(int which) {
		int i=(int)(imageData[which]&0xff);
		return(i);
	}

	// Figure out the format of this binary stream.
	private void determineFormat() throws PhotoException {
        if(imageData==null || imageData.length==0) {
            throw new PhotoException("imageData is empty");
        }
		if(isJpeg()) {
			format=FORMAT_JPEG;
		} else if(isPng()) {
			format=FORMAT_PNG;
		} else if(isGif()) {
			format=FORMAT_GIF;
		} else {
			throw new PhotoException("Cannot determine format ("
                + "imageData is " + imageData.length + " bytes)");
		}
	}

	// Calculate the width and height of the image.
	private void calcDim() throws PhotoException {
		determineFormat();

		switch(format) {
			case FORMAT_JPEG:
				calcDimJpeg();
				break;
			case FORMAT_PNG:
				calcDimPng();
				break;
			case FORMAT_GIF:
				calcDimGif();
				break;
			default:
				throw new PhotoException("Format " + format + " not handled.");
		}
	}

	// GIF SUPPORT

	/**
	 * True if this is a GIF.
	 */
	private boolean isGif() {
		int i=0;
		if(imageData.length < 3) {
			throw new IllegalArgumentException(
				"Image data too small to be a GIF, it's only "
					+ imageData.length + " bytes.");
		}
		return(	   ((imageData[i++]&0xff)=='G')
				&& ((imageData[i++]&0xff)=='I')
				&& ((imageData[i++]&0xff)=='F'));
	}

	private void calcDimGif() throws PhotoException {
		int i=0;

		if(!isGif()) {
			throw new PhotoException("This isn't a GIF.");
		}

		// Skip the header.
		i+=6;

		width=(imageData[i++]&0xff)
			| ((imageData[i++]&0xff)<<8);

		height=(imageData[i++]&0xff)
			| ((imageData[i++]&0xff)<<8);
	}

	// END GIF SUPPORT

	// PNG SUPPORT

	/**
	 * True if this is a PNG.
	 */
	private boolean isPng() {
		int i=0;
		if(imageData.length < 8) {
			throw new IllegalArgumentException(
				"Image data too short to be a PNG, it's only "
					+ imageData.length + " bytes.");
		}
		return(	   ((imageData[i++]&0xff)==0x89)
				&& ((imageData[i++]&0xff)=='P')
				&& ((imageData[i++]&0xff)=='N')
				&& ((imageData[i++]&0xff)=='G')
				&& ((imageData[i++]&0xff)=='\r')
				&& ((imageData[i++]&0xff)=='\n')
				&& ((imageData[i++]&0xff)==0x1a)
				&& ((imageData[i++]&0xff)==0x0a));
	}

	private void calcDimPng() throws PhotoException {
		int i=0;
		int length=0;

		if(!isPng()) {
			throw new PhotoException("This isn't a PNG.");
		}

		// Skip the header.
		i+=8;

		// Get the length of the header
		length=((imageData[i++]&0xff) << 24)
				| ((imageData[i++]&0xff) << 16)
				| ((imageData[i++]&0xff) << 8)
				| ((imageData[i++]&0xff));

		String ctype=""
			+ (char)imageData[i++] + (char)imageData[i++]
			+ (char)imageData[i++] + (char)imageData[i++];

		// The first chunk should be an IHDR, check it anyway.
		if(ctype.equals("IHDR")) {

			width=((imageData[i++]&0xff) << 24)
				| ((imageData[i++]&0xff) << 16)
				| ((imageData[i++]&0xff) << 8)
				| ((imageData[i++]&0xff));

			height=((imageData[i++]&0xff) << 24)
				| ((imageData[i++]&0xff) << 16)
				| ((imageData[i++]&0xff) << 8)
				| ((imageData[i++]&0xff));
		} else {
			throw new PhotoException(
				"IHDR Should be the first chunk type in a PNG, not " + ctype);
		}

	}

	// END PNG SUPPORT

	// JPEG SUPPORT

	// Return true if the given data is a jpeg.
	private boolean isJpeg() {
		if(imageData.length < 24) {
			throw new IllegalArgumentException(
				"Too short for a jpeg header, your image data is only "
					+ imageData.length + " bytes long.");
		}
		return( (getIntValue(0)==0xff)
				&& ( getIntValue(1) == 0xd8 )
				&& ( getIntValue(2) == 0xff ));
	}

	// Calculate the dimensions of a jpeg.
	private void calcDimJpeg() throws PhotoException {
		int ch=-1, i=0;
		boolean done=false;

		if(!isJpeg()) {
			throw new PhotoException("This isn't a JPEG");
		}

		// Move forward two.
		i+=2;

		// OK, look at all the tags until we find our image tag.
		while(!done && ch != 0xDA && i<imageData.length) {
			// Look for the next tag
			while(ch!=0xFF) { ch=getIntValue(i++); }
			// Tags can be kinda log, get to the end of it.
			while(ch==0xFF) { ch=getIntValue(i++); }

			// Is this our man?
			if(ch>=0xC0 && ch<= 0xC3) {
				// Jump to the interesting part.
				i+=3;
				width=(getIntValue(i+2)<<8) | (getIntValue(i+3));
				height=(getIntValue(i+0)<<8) | (getIntValue(i+1));
				done=true;
			} else {
				// OK, this isn't something we care about.  Figure out the
				// length of this section, skip over it, and let's move on
				// to the next one.
				int length=(getIntValue(i)<<8)|getIntValue(i+1);
				i+=length;
			}
		} // while loop
	}

	// END JPEG SUPPORT

}