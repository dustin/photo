// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>
// arch-tag: D2D20F73-5D6C-11D9-BD65-000A957659CC

package net.spy.photo;

/**
 * Format object.
 */
public class Format extends Object {

	public static final int FORMAT_ID_UNKNOWN=0;
	public static final int FORMAT_ID_JPEG=1;
	public static final int FORMAT_ID_PNG=2;
	public static final int FORMAT_ID_GIF=3;

	public static final Format FORMAT_UNKNOWN=new Format(FORMAT_ID_UNKNOWN);
	public static final Format FORMAT_JPEG=new Format(FORMAT_ID_JPEG);
	public static final Format FORMAT_PNG=new Format(FORMAT_ID_PNG);
	public static final Format FORMAT_GIF=new Format(FORMAT_ID_GIF);

	private int id=0;

	// Format definitions from above
	private static String formatMime[]={
		"image/unknown",
		"image/jpeg",
		"image/png",
		"image/gif"
	};
	private static String formatExt[]={ "unk", "jpg", "png", "gif" };

	/**
	 * Get an instance of Format.
	 */
	private Format(int i) {
		super();
		this.id=i;
	}

	/** 
	 * Get a format by ID.
	 */
	public static Format getFormat(int i) {
		Format rv=null;
		switch(i) {
			case FORMAT_ID_UNKNOWN:
				rv=FORMAT_UNKNOWN;
				break;
			case FORMAT_ID_JPEG:
				rv=FORMAT_JPEG;
				break;
			case FORMAT_ID_PNG:
				rv=FORMAT_PNG;
				break;
			case FORMAT_ID_GIF:
				rv=FORMAT_GIF;
				break;
			default:
				throw new IllegalArgumentException("Invalid format id: " + i);
		}
		return(rv);
	}

	public String toString() {
		StringBuffer sb=new StringBuffer(40);
		sb.append("{Format ");
		sb.append(formatMime[id]);
		sb.append("}");
		return(sb.toString());
	}

	/**
	 * Get the name of the format of this image.
	 */
	public String getMime() {
		return(formatMime[id]);
	}

	/** 
	 * Get the extension to be used for this format.
	 */
	public String getExtension() {
		return(formatExt[id]);
	}

	/** 
	 * Get the ID of this format.
	 */
	public int getId() {
		return(id);
	}

}
