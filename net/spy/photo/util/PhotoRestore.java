// Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoRestore.java,v 1.1 2000/11/28 09:52:11 dustin Exp $

package net.spy.photo.util;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.*;
import org.xml.sax.*;
import java.util.*;
import java.io.*;
import java.util.zip.*;

public class PhotoRestore extends Object {

	int which=-1;

	public PhotoRestore(String which) {
		super();
		this.which=Integer.parseInt(which);
	}

	public BackupEntry restore() throws Exception {
		// Our result
		BackupEntry ret=null;

		// Get the stream
		FileInputStream fis=new FileInputStream("/tmp/bak/album/" + which);
		GZIPInputStream gis=new GZIPInputStream(fis);
		InputSource is=new InputSource(gis);

		// Get the parser
		DOMParser dp=new DOMParser();

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

		return(ret);
	}

	public static void main(String args[]) throws Exception {
		PhotoRestore pr=new PhotoRestore(args[0]);
		BackupEntry be = pr.restore();
	}
}
