/*
 * Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
 *
 * $Id: PhotoBackup.java,v 1.2 2000/11/17 10:13:06 dustin Exp $
 */

package net.spy.photo.util;

import java.sql.*;
import java.util.*;
import java.io.*;
import java.util.zip.*;

import net.spy.*;
import net.spy.photo.*;

public class PhotoBackup extends Object {
	public PhotoBackup() {
		super();
	}

	public void backupTo(String dir) throws Exception {
		SpyDB db=new SpyDB(new PhotoConfig());

		backupAlbum(db, dir);
	}

	protected void backupAlbum(SpyDB db, String dir) throws Exception {

		// First, make our directory.
		String basedir=dir + "/album/";
		File baseDirFile=new File(basedir);
		baseDirFile.mkdirs();

		// Next, grab all the IDs we need
		Vector ids=new Vector();
		ResultSet rs=db.executeQuery("select id from album");
		while(rs.next()) {
			ids.addElement(new Integer(rs.getInt("id")));
		}

		// Flip through the IDs and back 'em up.
		for(Enumeration e=ids.elements(); e.hasMoreElements(); ) {
			Integer i=(Integer)e.nextElement();

			// Get the filename.
			String filename=basedir+i;
			// Get the file
			FileOutputStream ostream = new FileOutputStream(filename);
			GZIPOutputStream gzo=new GZIPOutputStream(ostream);

			System.out.println("Going to write out " + i);

			// Get the object
			AlbumBackupEntry abe=new AlbumBackupEntry(i.intValue());
			abe.writeTo(gzo);

			// Write it out
			gzo.close();
		}
	}

	public static void main(String args[]) throws Exception {
		PhotoBackup pb=new PhotoBackup();
		pb.backupTo(args[0]);
	}
}
