// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>
// arch-tag: D2D20F73-5D6C-11D9-BD65-000A957659CC

package net.spy.photo;

/**
 * Format object.
 */
public enum Format {

	UNKNOWN (0, "unk", "image/unknown"),
	JPEG    (1, "jpg", "image/jpeg"),
	PNG     (2, "png", "image/png"),
	GIF     (3, "gif", "image/gif");

	private final int id;
	private final String ext;
	private final String type;

	/**
	 * Get an instance of Format.
	 */
	private Format(int i, String e, String m) {
		this.id=i;
		this.ext=e;
		this.type=m;
	}

	/** 
	 * Get a format by ID.
	 */
	public static Format getFormat(int i) {
		Format rv=null;
		for(Format f : values()) {
			if(f.getId() == i) {
				rv=f;
			}
		}
		if(rv == null) {
			throw new IllegalArgumentException("Invalid format id:  " + i);
		}
		return(rv);
	}

	@Override
	public String toString() {
		return("{Format " + type + "}");
	}

	/**
	 * Get the name of the format of this image.
	 */
	public String getMime() {
		return(type);
	}

	/** 
	 * Get the extension to be used for this format.
	 */
	public String getExtension() {
		return(ext);
	}

	/** 
	 * Get the ID of this format.
	 */
	public int getId() {
		return(id);
	}

}
