// Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoRestore.java,v 1.5 2002/06/23 07:34:27 dustin Exp $

package net.spy.photo.util;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.*;
import org.xml.sax.*;
import java.util.*;
import java.io.*;
import java.util.zip.*;

/**
 * Restore image backups.
 */
public class PhotoRestore extends Object {

	/**
	 * Get a restorer.
	 */
	public PhotoRestore() {
		super();
	}

	/**
	 * Create a BackupEntry from an input stream.
	 */
	public BackupEntry restore(InputStream input) throws Exception {
		// Our result
		BackupEntry ret=null;

		// Input source
		InputSource is=new InputSource(input);

		// Get the parser
		DOMParser dp=new DOMParser();
		dp.setFeature("http://xml.org/sax/features/validation", true);

		// Parse the stream
		dp.parse(is);

		// Get the document.
		Document d=dp.getDocument();
		
		// Figure out what we're parsing here.
		Node n=d.getFirstChild();
		if(n==null) {
			throw new Exception("No child in document!");
		}
		String type=n.getNodeName();
		if(type.equals("photo_album_object")) {
			ret=new AlbumBackupEntry(n);
		}

		ret.restore();

		return(ret);
	}

	/**
	 * Restore an image.
	 */
	public static void main(String args[]) throws Exception {
		PhotoRestore pr=new PhotoRestore();

		// Get the stream

		FileInputStream fis=new FileInputStream(args[0]);
		GZIPInputStream gis=new GZIPInputStream(fis);

		BackupEntry be = pr.restore(gis);
		gis.close();
	}
}
