// Copyright (c) 2000  Dustin Sallings <dustin@spy.net>

package net.spy.photo.util;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.zip.GZIPOutputStream;

import net.spy.db.SpyDB;
import net.spy.photo.PhotoConfig;

/**
 * Utility to backup images.
 */
public class PhotoBackup extends Object {
	public PhotoBackup() {
		super();
	}

	/**
	 * Perform a backup to a given diretory.
	 *
	 * @param cat the name of the category to back up, if null, back up all
	 *			categories.
	 * @param dir the directory to which to write the backup
	 */
	public void backup(String cat, String dir) throws Exception {
		SpyDB db=new SpyDB(PhotoConfig.getInstance());

		if(dir==null) {
			throw new NullPointerException("Directory was null.");
		}

		backupAlbum(db, cat, dir);
	}

	private void backupAlbum(SpyDB db, String cat, String dir)
		throws Exception {

		// First, make our directory.
		String basedir=dir + "/album/";
		File baseDirFile=new File(basedir);
		baseDirFile.mkdirs();

		// Next, grab all the IDs we need
		ArrayList<Integer> ids=new ArrayList<Integer>();

		PreparedStatement pst=null;
		if(cat==null) {
			pst=db.prepareStatement("select id from album");
		} else {
			pst=db.prepareStatement("select album.id\n"
				+ "from album, cat\n"
				+ " where album.cat=cat.id and cat.name=?");
			pst.setString(1, cat);
		}

		ResultSet rs=pst.executeQuery();
		while(rs.next()) {
			ids.add(rs.getInt("id"));
		}

		// Statistics.
		BackupStats bs=new BackupStats(ids.size());
		if(cat==null) {
			System.out.println("Beginning backup of all categories, totalling "
				+ ids.size() + " objects.");
		} else {
			System.out.println("Beginning backup of category ``" + cat
				+ "'', " + ids.size() + " objects.");
		}

		// Flip through the IDs and back 'em up.
		for(Integer i : ids) {
			// Count one.
			bs.click();

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
	 * Perform a backup.
	 *
	 * Usage:  PhotoBackup /path/to/store/backups [CategoryName ...]
	 */
	public static void main(String args[]) throws Exception {
		PhotoBackup pb=new PhotoBackup();
		String cat=null;
		String dir=null;
		if(args.length==1) {
			dir=args[0];
			pb.backup(null, dir);
		} else {
			for(int i=1; i<args.length; i++) {
				dir=args[0];
				cat=args[i];
				pb.backup(cat, dir);
			}
		}
	}

	private static class BackupStats extends Object {
		private int done=0;
		private int left=0;
		private long totalTime=0;

		private long lastTime=0;
		private long lastProcessTime=0;

		public BackupStats(int size) {
			super();

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
			double estimate=avgProcessTime*left;

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
