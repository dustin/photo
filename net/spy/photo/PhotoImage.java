// Copyright (c) 1999  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoImage.java,v 1.17 2002/07/10 03:38:08 dustin Exp $

package net.spy.photo;

import java.io.Serializable;

/**
 * Represents the image itself, yo.
 */
public class PhotoImage extends Object
	implements Serializable, PhotoDimensions {

	// Image data
	private byte image_data[]=null;

	private int _width=-1;
	private int _height=-1;

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
		image_data=data;
		calcDim();
	}

	/**
	 * Get the binary data that represents this image.
	 */
	public byte[] getData() {
		return(image_data);
	}

	/**
	 * Get the size (in bytes) of this image.
	 */
	public int size() {
		return(image_data.length);
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
		StringBuffer sb=new StringBuffer();

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
		return(_width);
	}

	/**
	 * @see PhotoDimensions
	 */
	public int getHeight() {
		return(_height);
	}

	// This sucks, but byte comes back signed.
	private int getIntValue(int which) {
		int i=(int)(image_data[which]&0xff);
		return(i);
	}

	// Figure out the format of this binary stream.
	private void determineFormat() throws PhotoException {
        if(image_data==null || image_data.length==0) {
            throw new PhotoException("image_data is empty");
        }
		if(isJpeg()) {
			format=FORMAT_JPEG;
		} else if(isPng()) {
			format=FORMAT_PNG;
		} else if(isGif()) {
			format=FORMAT_GIF;
		} else {
			throw new PhotoException("Cannot determine format ("
                + "imageData is " + image_data.length + " bytes)");
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
		if(image_data.length < 3) {
			throw new IllegalArgumentException(
				"Image data too small to be a GIF, it's only "
					+ image_data.length + " bytes.");
		}
		return(	   ((image_data[i++]&0xff)=='G')
				&& ((image_data[i++]&0xff)=='I')
				&& ((image_data[i++]&0xff)=='F'));
	}

	private void calcDimGif() throws PhotoException {
		int i=0;

		if(!isGif()) {
			throw new PhotoException("This isn't a GIF.");
		}

		// Skip the header.
		i+=6;

		_width=(image_data[i++]&0xff)
			| ((image_data[i++]&0xff)<<8);

		_height=(image_data[i++]&0xff)
			| ((image_data[i++]&0xff)<<8);
	}

	// END GIF SUPPORT

	// PNG SUPPORT

	/**
	 * True if this is a PNG.
	 */
	private boolean isPng() {
		int i=0;
		if(image_data.length < 8) {
			throw new IllegalArgumentException(
				"Image data too short to be a PNG, it's only "
					+ image_data.length + " bytes.");
		}
		return(	   ((image_data[i++]&0xff)==0x89)
				&& ((image_data[i++]&0xff)=='P')
				&& ((image_data[i++]&0xff)=='N')
				&& ((image_data[i++]&0xff)=='G')
				&& ((image_data[i++]&0xff)=='\r')
				&& ((image_data[i++]&0xff)=='\n')
				&& ((image_data[i++]&0xff)==0x1a)
				&& ((image_data[i++]&0xff)==0x0a));
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
		length=((image_data[i++]&0xff) << 24)
				| ((image_data[i++]&0xff) << 16)
				| ((image_data[i++]&0xff) << 8)
				| ((image_data[i++]&0xff));

		String ctype=""
			+ (char)image_data[i++] + (char)image_data[i++]
			+ (char)image_data[i++] + (char)image_data[i++];

		// The first chunk should be an IHDR, check it anyway.
		if(ctype.equals("IHDR")) {

			_width=((image_data[i++]&0xff) << 24)
				| ((image_data[i++]&0xff) << 16)
				| ((image_data[i++]&0xff) << 8)
				| ((image_data[i++]&0xff));

			_height=((image_data[i++]&0xff) << 24)
				| ((image_data[i++]&0xff) << 16)
				| ((image_data[i++]&0xff) << 8)
				| ((image_data[i++]&0xff));
		} else {
			throw new PhotoException(
				"IHDR Should be the first chunk type in a PNG, not " + ctype);
		}

	}

	// END PNG SUPPORT

	// JPEG SUPPORT

	// Return true if the given data is a jpeg.
	private boolean isJpeg() {
		if(image_data.length < 24) {
			throw new IllegalArgumentException(
				"Too short for a jpeg header, your image data is only "
					+ image_data.length + " bytes long.");
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
		while(!done && ch != 0xDA && i<image_data.length) {
			// Look for the next tag
			while(ch!=0xFF) { ch=getIntValue(i++); }
			// Tags can be kinda log, get to the end of it.
			while(ch==0xFF) { ch=getIntValue(i++); }

			// Is this our man?
			if(ch>=0xC0 && ch<= 0xC3) {
				// Jump to the interesting part.
				i+=3;
				_width=(getIntValue(i+2)<<8) | (getIntValue(i+3));
				_height=(getIntValue(i+0)<<8) | (getIntValue(i+1));
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
