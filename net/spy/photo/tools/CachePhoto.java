/*
 * Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
 *
 * $Id: CachePhoto.java,v 1.9 2002/07/10 04:00:17 dustin Exp $
 */

package net.spy.photo.tools;

import java.io.File;
import java.io.FileOutputStream;

import java.rmi.Naming;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Iterator;

import net.spy.SpyConfig;
import net.spy.SpyDB;

import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoImage;

import net.spy.photo.rmi.RemoteImageServer;

import net.spy.util.Base64;

/**
 * Cache all of the images and verify they look the same in the DB as they
 * do when they come from the image server.
 */
public class CachePhoto extends Object {

	private SpyConfig conf=null;
	private RemoteImageServer server=null;
	private String saveDir=null;
	private ArrayList errors=null;

	/**
	 * Get a new CachePhoto.
	 */
	public CachePhoto() throws Exception {
		super();

		conf=new PhotoConfig();
		server = (RemoteImageServer)Naming.lookup(conf.get("imageserver"));
		errors=new ArrayList();
	}

	/**
	 * Get an image from DB.
	 */
	public String getImageFromDB(int id) throws Exception {
		SpyDB db=new SpyDB(conf);

		PreparedStatement pst=db.prepareStatement(
			"select data from image_store where id = ?\n"
			+ " order by line");
		pst.setInt(1, id);
		ResultSet rs=pst.executeQuery();

		StringBuffer sb=new StringBuffer();
		while(rs.next()) {
			sb.append(rs.getString("data"));
		}

		rs.close();
		db.close();

		return(sb.toString());
	}

	private void compare(byte dbd[], byte isd[], int id) throws Exception {
		// Check the length
		if(dbd.length!=isd.length) {
			throw new ImageDataException(
				"Different size data from DB and image server:\n\t"
				+ dbd.length + " (db) vs. " + isd.length + " (is)", id);
		}

		// Compare each byte.
		for(int i=0; i<dbd.length; i++) {
			if(dbd[i]!=isd[i]) {
				throw new ImageDataException("Difference on byte " + i, id);
			}
		}

	}

	/**
	 * Set the directory to which we will save all differing files.  If
	 * this isn't set, files won't be saved.
	 */
	public void setSaveDir(String to) {
		this.saveDir=to;
	}

	// Save image data (if we're supposed to)
	private void saveImage(int id, String dbds, byte dbdata[], byte isdata[])
		throws Exception {

		if(saveDir!=null) {
			File dbfile=new File(saveDir + "/" + id + "_db.jpg");
			File dbsfile=new File(saveDir + "/" + id + "_db.txt");
			File isfile=new File(saveDir + "/" + id + "_is.jpg");

			FileOutputStream fos=new FileOutputStream(dbfile);
			fos.write(dbdata);
			fos.close();

			fos=new FileOutputStream(dbsfile);
			fos.write(dbds.getBytes());
			fos.close();

			fos=new FileOutputStream(isfile);
			fos.write(isdata);
			fos.close();

		}
	}

	/**
	 * Do your thing.
	 */
	private void go() throws Exception {

		SpyDB db=new SpyDB(conf);
		Base64 base64 = new Base64();
		ResultSet rs=db.executeQuery("select count(*) from album");
		rs.next();
		int num=rs.getInt(1);
		rs.close();

		Stats stats=new Stats(num);

		rs = db.executeQuery("select id from album order by ts desc");
		// rs = db.executeQuery("select id from album order by ts");
		while(rs.next()) {
			int id = rs.getInt(1);
			PhotoImage data=null;
			String dbdataStr=null;
			byte dbdata[]=null;
			stats.click();
			stats.start();
			try {
				// false == full image
				data=server.getImage(id, false);
				dbdataStr=getImageFromDB(id);
				dbdata=base64.decode(dbdataStr);
				compare(dbdata, data.getData(), id);
				// true == thumbnail
				data=server.getImage(id, true);
			} catch(ImageDataException ide) {
				System.err.println("Data difference on " + id + ":  " + ide);
				saveImage(id, dbdataStr, dbdata, data.getData());
				errors.add(ide);
			} catch(Exception e) {
				System.err.println("Error on image " + id);
				e.printStackTrace();
				errors.add(e);
			}
			stats.stop();
			System.out.println("Cached " + id 
				+ " in " + stats.getLastTime() + " - " + stats.getStats());
		}
		rs.close();
		db.close();

		System.err.println("Errors:");
		for(Iterator i=errors.iterator(); i.hasNext(); ) {
			System.err.println("\t" + i.next());
		}
	}

	/**
	 * Run.  You may optionally supply an argument to save the differences
	 * when files are different.
	 */
	public static void main(String args[]) throws Exception {
		CachePhoto cp=new CachePhoto();
		if(args.length>0) {
			cp.setSaveDir(args[0]);
		}
		cp.go();
	}

	// Inner exception for reporting image data differences
	private class ImageDataException extends Exception {
		private int id=0;
		public ImageDataException(String s, int id) {
			super(s);
			this.id=id;
		}
		public int getId() {
			return(id);
		}
	}

	// Inner class for stats
	private class Stats extends Object {
		private int done=0;
		private int left=0;
		private long startTime=0;
		private long totalTime=0;
		private long lastTime=0;
		private long lastProcessTime=0;

		public Stats(int size) {
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
			String rv=null;
			try {
				double avgProcessTime=((double)totalTime/(double)done)/1000.0;
				double estimate=avgProcessTime*(double)left;

				java.text.NumberFormat nf=java.text.NumberFormat.getInstance();
				nf.setMaximumFractionDigits(2);

				rv="Avg=" + nf.format(avgProcessTime)
					+ "s, Estimate=" + nf.format(estimate) + "s"
					+ " ("
					+ new java.util.Date(
						System.currentTimeMillis()+(1000*(long)estimate)
						)
					+ ")";
			} catch(Exception e) {
				rv="Error getting stats:  " + e;
			}
			return(rv);
		}
	}
}
