// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoSaver.java,v 1.9 2003/07/26 08:38:27 dustin Exp $

package net.spy.photo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import java.text.SimpleDateFormat;
import java.text.ParseException;

import net.spy.SpyObject;
import net.spy.db.SpyDB;

import net.spy.photo.sp.InsertImage;

/**
 * This class is responsible for saving images that have been uploaded.
 */
public class PhotoSaver extends SpyObject {

	private String keywords=null;
	private String info=null;
	private int cat=-1;
	private String taken=null;
	private java.util.Date takenDate=null;
	private PhotoUser user=null;
	private PhotoImage photoImage=null;
	private Timestamp timestamp=null;
	private int id=-1;

	/**
	 * Get an instance of PhotoSaver.
	 */
	public PhotoSaver() {
		super();
		timestamp=new Timestamp(System.currentTimeMillis());
	}

	/**
	 * String me.
	 */
	public String toString() {
		return("{PhotoSaver id=" + id + "}");
	}

	public void setKeywords(String keywords) {
		this.keywords=keywords;
	}

	public String getKeywords() {
		return(keywords);
	}

	public void setInfo(String info) {
		this.info=info;
	}

	public String getInfo() {
		return(info);
	}

	public void setCat(int cat) {
		this.cat=cat;
	}

	public int getCat() {
		return(cat);
	}

	public void setTaken(String taken) {
		SimpleDateFormat sdf=new SimpleDateFormat("MM/dd/yyyy");
		try {
			this.takenDate=sdf.parse(taken);
		} catch(ParseException e) {
			getLogger().warn(taken + " can't be parsed as a date", e);
		}
		this.taken=taken;
	}

	public void setTaken(java.util.Date taken) {
		SimpleDateFormat sdf=new SimpleDateFormat("MM/dd/yyyy");
		this.taken=sdf.format(taken);
		takenDate=taken;
	}

	public String getTaken() {
		return(taken);
	}

	public java.util.Date getTakenAsDate() {
		return(takenDate);
	}

	public void setPhotoImage(PhotoImage photoImage) {
		this.photoImage=photoImage;
	}

	public PhotoImage getPhotoImage() {
		return(photoImage);
	}

	public void setId(int id) {
		this.id=id;
	}

	public int getId() {
		return(id);
	}

	public void setUser(PhotoUser user) {
		this.user=user;
	}

	public PhotoUser getUser() {
		return(user);
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp=timestamp;
	}

	public Timestamp getTimestamp() {
		return(timestamp);
	}

	/**
	 * Get a new ID for a photo.
	 */
	public static int getNewImageId() throws PhotoException {
		int rv=0;
		try {
			SpyDB db=new SpyDB(PhotoConfig.getInstance());
			ResultSet rs=db.executeQuery("select nextval('album_id_seq')");
			if(!rs.next()) {
				throw new PhotoException("No result for new album ID");
			}
			rv=rs.getInt(1);
			if(rs.next()) {
				throw new PhotoException("Too many results for new album ID");
			}
			rs.close();
			db.close();
		} catch(PhotoException e) {
			throw e;
		} catch(Exception e) {
			throw new PhotoException("Error getting image ID", e);
		}
		return(rv);
	}

	/**
	 * Tell the PhotoSaver it will not be needed soon.
	 */
	public void passivate() {
	}

	/**
	 * Tell the PhotoSaver to active itself for saving.
	 */
	public void activate() {
	}

	/**
	 * Activate and perform this save.
	 */
	public void saveImage() throws PhotoException {
		activate();

		if(id==-1) {
			throw new PhotoException("ID not set.");
		}

		// Check access.
		if(!user.canAdd(cat)) {
			throw new PhotoException("User " + user
				+ " has no access to category " + cat);
		}

		SpyDB db=null;
		Connection conn=null;
		try {
			String query=null;
			db=new SpyDB(PhotoConfig.getInstance());
			conn=db.getConn();
			conn.setAutoCommit(false);

			InsertImage ii=new InsertImage(conn);

			ii.setImageId(getId());
			ii.setKeywords(getKeywords());
			ii.setDescription(getInfo());
			ii.setCatId(getCat());
			ii.setTaken(new java.sql.Date(getTakenAsDate().getTime()));
			ii.setSize(getPhotoImage().size());
			ii.setAddedBy(user.getId());
			ii.setTimestamp(getTimestamp());
			ii.setWidth(getPhotoImage().getWidth());
			ii.setHeight(getPhotoImage().getHeight());

			// Run the query
			ii.executeUpdate();
			ii.close();

			// Cache the image data
			PhotoImageHelper photoHelper=new PhotoImageHelper(getId());
			photoHelper.storeImage(getPhotoImage());

			// Image is stored, commit.
			conn.commit();

			// Clear the image data cache
			PhotoImageDataImpl.clearCache();

		} catch(Exception e) {
			try {
				if(conn!=null) {
					conn.rollback();
				}
			} catch(Exception e2) {
				getLogger().warn("Problem rolling back transaction", e2);
			}
			throw new PhotoException("Error saving image.", e);
		} finally {
			if(conn!=null) {
				try {
					conn.setAutoCommit(true);
					if(db != null) {
						db.close();
					}
				} catch(Exception e) {
					getLogger().warn("Problem restoring autocommit");
				}
			}
		} // Finally block
	}

}
