// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoSaver.java,v 1.7 2002/07/10 04:00:17 dustin Exp $

package net.spy.photo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import java.text.SimpleDateFormat;

import net.spy.SpyDB;

/**
 * This class is responsible for saving images that have been uploaded.
 */
public class PhotoSaver extends Object {

	private String keywords=null;
	private String info=null;
	private int cat=-1;
	private String taken=null;
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
		this.taken=taken;
	}

	public void setTaken(java.util.Date taken) {
		SimpleDateFormat sdf=new SimpleDateFormat("MM/dd/yyyy");
		this.taken=sdf.format(taken);
	}

	public String getTaken() {
		return(taken);
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
			SpyDB db=new SpyDB(new PhotoConfig());
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
			db=new SpyDB(new PhotoConfig());
			conn=db.getConn();
			conn.setAutoCommit(false);

			query = "insert into album(id, keywords, descr, cat, taken, size, "
				+ " addedby, ts, width, height)\n"
				+ "   values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

			PreparedStatement pst=conn.prepareStatement(query);

			int i=1;
			pst.setInt(i++, getId());
			pst.setString(i++, getKeywords());
			pst.setString(i++, getInfo());
			pst.setInt(i++, getCat());
			pst.setString(i++, getTaken());
			pst.setInt(i++, getPhotoImage().size());
			pst.setInt(i++, user.getId());
			pst.setTimestamp(i++, getTimestamp());
			pst.setInt(i++, getPhotoImage().getWidth());
			pst.setInt(i++, getPhotoImage().getHeight());

			// Run the query
			pst.executeUpdate();

			// Cache the image data
			PhotoImageHelper photoHelper=new PhotoImageHelper(getId());
			photoHelper.storeImage(getPhotoImage());

			// Image is stored, commit.
			conn.commit();

		} catch(Exception e) {
			try {
				conn.rollback();
			} catch(Exception e2) {
				e2.printStackTrace();
			}
			throw new PhotoException("Error saving image.", e);
		} finally {
			if(conn!=null) {
				try {
					conn.setAutoCommit(true);
					db.close();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		} // Finally block
	}

}
