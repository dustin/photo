package net.spy.photo;

import static net.spy.photo.PhotoUtil.getIntValue;
import static net.spy.photo.PhotoUtil.getIntValue2;

import java.security.MessageDigest;

import net.spy.SpyObject;
import net.spy.stat.ComputingStat;
import net.spy.stat.Stats;
import net.spy.util.SpyUtil;

/**
 * Parse photos and find out stuff about them.
 */
public class PhotoParser extends SpyObject {

	// This is the size of a single-pixel GIF.
	public static final int SMALLEST_IMAGE=42;

	private static PhotoParser instance=null;

	private ComputingStat parseStat=null;

	public static synchronized PhotoParser getInstance() {
		if(instance == null) {
			instance=new PhotoParser();
			instance.parseStat=Stats.getComputingStat("photoparser.parsed");
		}
		return instance;
	}

	/**
	 * Parse the given image to find out stuff about it.
	 */
	public Result parseImage(byte[] data) throws PhotoException {
		long start=System.currentTimeMillis();
		Format fmt=determineFormat(data);
		Result rv=new Result(fmt);
		try {
			rv.md5=computeMd5(data);
		} catch (Exception e) {
			throw new PhotoException("Error computing md5", e);
		}
		calcDim(data, rv);
		parseStat.add(System.currentTimeMillis()-start);
		return rv;
	}


	/**
	 * Compute the md5 of this image.
	 */
	public static String computeMd5(byte[] imageData) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(imageData);
		return SpyUtil.byteAToHexString(md.digest());
	}

	// Calculate the width and height of the image.
	private void calcDim(byte[] imageData, Result res)
		throws PhotoException {
		assert res.getFormat() != Format.UNKNOWN
			: "Can't calculate for an unknown format";
		assert imageData != null : "Can't calculate within null data";
		switch(res.getFormat()) {
			case JPEG:
				calcDimJpeg(imageData, res);
				break;
			case PNG:
				calcDimPng(imageData, res);
				break;
			case GIF:
				calcDimGif(imageData, res);
				break;
			default:
				throw new PhotoException("Format "
						+ res.getFormat() + " not handled.");
		}
	}

	// GIF SUPPORT


	private void calcDimGif(byte[] imageData, Result res) throws PhotoException {
		int i=0;

		// Skip the header.
		i+=6;

		res.width=(imageData[i++]&0xff)
			| ((imageData[i++]&0xff)<<8);

		res.height=(imageData[i++]&0xff)
			| ((imageData[i++]&0xff)<<8);
	}

	// END GIF SUPPORT

	// PNG SUPPORT

	private void calcDimPng(byte[] imageData, Result res) throws PhotoException {
		int i=0;
		int length=0;

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

			res.width=((imageData[i++]&0xff) << 24)
				| ((imageData[i++]&0xff) << 16)
				| ((imageData[i++]&0xff) << 8)
				| ((imageData[i++]&0xff));

			res.height=((imageData[i++]&0xff) << 24)
				| ((imageData[i++]&0xff) << 16)
				| ((imageData[i++]&0xff) << 8)
				| ((imageData[i++]&0xff));
		} else {
			throw new PhotoException(
				"IHDR Should be the first chunk type in a PNG, not " + ctype
					+ " (with a length of " + length + ")");
		}

	}

	// END PNG SUPPORT

	// JPEG SUPPORT

	// Calculate the dimensions of a jpeg.
	private void calcDimJpeg(byte[] imageData, Result res) throws PhotoException {
		int ch=-1, i=0;
		boolean done=false;

		// Move forward two.
		i+=2;

		// OK, look at all the tags until we find our image tag.
		while(!done && ch != 0xDA && i<imageData.length) {
			// Look for the next tag
			while(ch!=0xFF) { ch=getIntValue(imageData, i++); }
			// Tags can be kinda log, get to the end of it.
			while(ch==0xFF) { ch=getIntValue(imageData, i++); }

			// Is this our man?
			if(ch>=0xC0 && ch<= 0xC3) {
				// Jump to the interesting part.
				i+=3;
				res.width=getIntValue2(imageData, i+2);
				res.height=getIntValue2(imageData, i);
				done=true;
			} else {
				// OK, this isn't something we care about.  Figure out the
				// length of this section, skip over it, and let's move on
				// to the next one.
				int length=getIntValue2(imageData, i);
				i+=length;
			}
		} // while loop
	}

	// Figure out the format of this binary stream.
	private Format determineFormat(byte[] imageData)
		throws PhotoException {
		Format rv=null;
        if(imageData==null || imageData.length==0) {
            throw new PhotoException("imageData is empty");
        }
        if(imageData.length < SMALLEST_IMAGE) {
        		throw new PhotoException("imageData is too small to be an image");
        }
		if(isJpeg(imageData)) {
			rv=Format.JPEG;
		} else if(isPng(imageData)) {
			rv=Format.PNG;
		} else if(isGif(imageData)) {
			rv=Format.GIF;
		} else {
			throw new PhotoException("Cannot determine format ("
                + "imageData is " + imageData.length + " bytes)");
		}
		return rv;
	}

	/**
	 * True if this is a GIF.
	 */
	private boolean isGif(byte[] imageData) {
		assert imageData != null : "Image data was null";
		int i=0;
		return(	   ((imageData[i++]&0xff)=='G')
				&& ((imageData[i++]&0xff)=='I')
				&& ((imageData[i++]&0xff)=='F'));
	}


	/**
	 * True if this is a PNG.
	 */
	private boolean isPng(byte[] imageData) {
		int i=0;
		return(	   ((imageData[i++]&0xff)==0x89)
				&& ((imageData[i++]&0xff)=='P')
				&& ((imageData[i++]&0xff)=='N')
				&& ((imageData[i++]&0xff)=='G')
				&& ((imageData[i++]&0xff)=='\r')
				&& ((imageData[i++]&0xff)=='\n')
				&& ((imageData[i++]&0xff)==0x1a)
				&& ((imageData[i++]&0xff)==0x0a));
	}

	// Return true if the given data is a jpeg.
	private boolean isJpeg(byte[] imageData) {
		return( (getIntValue(imageData, 0)==0xff)
				&& ( getIntValue(imageData, 1) == 0xd8 )
				&& ( getIntValue(imageData, 2) == 0xff ));
	}

	public static class Result implements PhotoDimensions {
		int width=0;
		int height=0;
		String md5=null;
		private Format format=null;

		Result(Format f) {
			super();
			format=f;
		}

		public Format getFormat() {
			return format;
		}

		public int getHeight() {
			return height;
		}

		public int getWidth() {
			return width;
		}

		public String getMd5() {
			return md5;
		}

	}
}
