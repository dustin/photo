/*
 * Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
 *
 * $Id: PhotoBackup.java,v 1.7 2002/06/23 07:34:27 dustin Exp $
 */

package net.spy.photo.util;

import java.sql.*;
import java.util.*;
import java.io.*;
import java.util.zip.*;

import net.spy.*;
import net.spy.photo.*;

/**
 * Utility to backup images.
 */
public class PhotoBackup extends Object {
	public PhotoBackup() {
		super();
	}

	/**
	 * Set the directory to which to write the backups.
	 */
	public void backupTo(String dir) throws Exception {
		SpyDB db=new SpyDB(new PhotoConfig());

		backupAlbum(db, dir);
	}

	private void backupAlbum(SpyDB db, String dir) throws Exception {

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

		// Statistics.
		BackupStats bs=new BackupStats(ids.size());
		System.out.println("Beginning backups on " + ids.size() + " objects.");

		// Flip through the IDs and back 'em up.
		for(Enumeration e=ids.elements(); e.hasMoreElements(); ) {
			Integer i=(Integer)e.nextElement();

			// Count one.
			bs.click();

			// Get the filename.
			String filename=basedir+i;
			File outfile=new File(basedir+i);
			if(outfile.exists()) {
				System.out.println("Not backing up " + i +", already exists.");
			} else {

				// Startwatch
				bs.start();

				// Get the file
				FileOutputStream ostream = new FileOutputStream(outfile);
				GZIPOutputStream gzo=new GZIPOutputStream(ostream);

				System.out.println("Going to write out " + i);

				// Get the object
				AlbumBackupEntry abe=new AlbumBackupEntry(i.intValue());
				abe.writeTo(gzo);

				// Write it out
				gzo.close();

				// Stopwatch
				bs.stop();
				System.out.println("Wrote in " + bs.getLastTime()
					+ " - " + bs.getStats());
			}
		}
	}

	/**
	 * Perform a backup.  argv[0] is the destination directory where the
	 * backups will be written.
	 */
	public static void main(String args[]) throws Exception {
		PhotoBackup pb=new PhotoBackup();
		pb.backupTo(args[0]);
	}

	private class BackupStats extends Object {
		private int done=0;
		private int left=0;
		private long startTime=0;
		private long totalTime=0;

		private long lastTime=0;
		private long lastProcessTime=0;

		public BackupStats(int size) {
			super();

			this.startTime=System.currentTimeMillis();
			this.left=size;
		}

		public void click() {
			left--;
		}

		public void start() {
			lastTime=System.currentTimeMillis();
		}

		public void stop() {
			long thistime=System.currentTimeMillis();
			lastProcessTime=thistime-lastTime;
			done++;
			totalTime+=lastProcessTime;
		}

		public String getLastTime() {
			long lt=lastProcessTime/1000;
			return("" + lt + "s");
		}

		public String getStats() {
			double avgProcessTime=((double)totalTime/(double)done)/1000.0;
			double estimate=avgProcessTime*(double)left;

			java.text.NumberFormat nf=java.text.NumberFormat.getInstance();
			nf.setMaximumFractionDigits(2);

			return("Avg=" + nf.format(avgProcessTime)
				+ "s, Estimate=" + nf.format(estimate) + "s"
				+ " ("
				+ new java.util.Date(
					System.currentTimeMillis()+(1000*(long)estimate)
					)
				+ ")");
		}
	}
}
