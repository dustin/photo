// Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoRestore.java,v 1.7 2002/07/09 21:33:20 dustin Exp $

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
	 * Restore an image or a series of images.
	 */
	public static void main(String args[]) throws Exception {
		PhotoRestore pr=new PhotoRestore();

		for(int i=0; i<args.length; i++) {
			// Get the stream

			FileInputStream fis=new FileInputStream(args[i]);
			GZIPInputStream gis=new GZIPInputStream(fis);

			BackupEntry be = pr.restore(gis);
			System.out.println("Restored " + be);
			gis.close();
		}
	}
}
