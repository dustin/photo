// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: Gallery.java,v 1.4 2002/07/02 07:03:41 dustin Exp $

package net.spy.photo;

import java.util.*;
import java.util.Date;
import java.sql.*;

import net.spy.SpyDB;
import net.spy.db.SpyCacheDB;

import net.spy.photo.sp.LookupGallery;

/**
 * A named collection of images.
 */
public class Gallery extends Object implements java.io.Serializable {

	private int id=-1;
	private String name=null;
	private Vector images=null;
	private boolean isPublic=false;
	private PhotoUser owner=null;
	private Date timestamp=null;

	/**
	 * Get an instance of Gallery belonging to a given owner.
	 */
	public Gallery(PhotoUser owner, String name) {
		super();
		this.name=name;
		this.owner=owner;
		this.images=new Vector();
	}

	/**
	 * Get an instance of Gallery belonging to a given owner.
	 */
	public Gallery(PhotoUser owner) {
		super();
		this.owner=owner;
		this.images=new Vector();
	}

	// Get the gallery for the current row in the given resultset
	private Gallery(ResultSet rs) throws SQLException, PhotoException {
		super();
		this.id=rs.getInt("gallery_id");
		this.name=rs.getString("gallery_name");
		this.owner=Persistent.getSecurity().getUser(
			rs.getInt("wwwuser_id"));
		this.isPublic=rs.getBoolean("ispublic");
		this.timestamp=rs.getTimestamp("ts");

		if(owner==null) {
			throw new PhotoException("User " + rs.getInt("wwwuser_id")
				+ "not found");
		}
	}

	/**
	 * Get a list of all galleries visible by the user.
	 */
	public static Cursor getGalleries(PhotoUser user)
		throws PhotoException {

		Cursor rv=null;

		try {
			SpyCacheDB db=new SpyCacheDB(new PhotoConfig());
			PreparedStatement pst=db.prepareStatement(
				"select * from galleries\n"
				+ "  where wwwuser_id=? or ispublic = true\n"
				+ "  order by ts", 3600);
			pst.setInt(1, user.getId());
			ResultSet rs=pst.executeQuery();
			rv=new Cursor();
			while(rs.next()) {
				Gallery g=new Gallery(rs);
				g.loadMap(user);
				rv.addElement(g);
			}
			rs.close();
			pst.close();
			db.close();
		} catch(Exception e) {
			throw new PhotoException("Error getting gallery list", e);
		}

		return(rv);
	}

	private void loadMap(PhotoUser user) throws Exception {
		LookupGallery lg=new LookupGallery(new PhotoConfig());
		lg.set("gallery_id", id);
		lg.set("current_user", user.getId());
		lg.set("default_user", PhotoUtil.getDefaultId());

		ResultSet rs=lg.executeQuery();

		images=new Vector();
		while(rs.next()) {
			addImage(rs.getInt("album_id"));
		}

		rs.close();
		lg.close();
	}

	/**
	 * Get the given gallery by ID as it will be seen by the given user.
	 *
	 * @return the Gallery or null if there's no match.
	 */
	public static Gallery getGallery(PhotoUser user, int id)
		throws PhotoException {

		Gallery rv=null;

		try {
			SpyCacheDB db=new SpyCacheDB(new PhotoConfig());
			PreparedStatement pst=db.prepareStatement(
				"select gallery_id, gallery_name, wwwuser_id, ispublic, ts\n"
					+ " from galleries\n"
					+ "   where gallery_id=?\n"
					+ "    and (wwwuser_id=? or ispublic=true)", 3600);
			pst.setInt(1, id);
			pst.setInt(2, user.getId());
			ResultSet rs=pst.executeQuery();
			if(rs.next()) {
				rv=new Gallery(rs);
			}
			rs.close();
			db.close();

			// Load the map
			rv.loadMap(user);

		} catch(Exception e) {
			throw new PhotoException("Couldn't look up gallery", e);
		}

		return(rv);
	}

	/**
	 * Save a new gallery.
	 */
	public void save() throws PhotoException {
		SpyDB db=new SpyDB(new PhotoConfig());
		Connection conn=null;

		if(name==null) {
			throw new PhotoException("Gallery name not provided.");
		}

		try {
			conn=db.getConn();
			conn.setAutoCommit(false);
			PreparedStatement pst=null;

			// Different prepared statement for new vs. update
			if(id==-1) {
				pst=conn.prepareStatement(
					"insert into galleries "
						+ "(gallery_name, wwwuser_id, ispublic)\n"
						+ " values(?,?,?)");
			} else {
				pst=conn.prepareStatement(
					"update galleries "
						+ "set gallery_name=?, wwwuser_id=?, ispublic=?\n"
						+ "  where gallery_id=?");
				pst.setInt(4, id);
			}
			// Set the common fields
			pst.setString(1, getName());
			pst.setInt(2, getOwner().getId());
			pst.setBoolean(3, isPublic());

			// Perform the update
			int affected=pst.executeUpdate();

			// Make sure we didn't mess up
			if(affected!=1) {
				throw new PhotoException(
					"Expected to affect one row, affected " + affected);
			}

			// Close the statement.
			pst.close();

			// If it's a new record, get the ID
			if(id==-1) {
				Statement st2=conn.createStatement();
				ResultSet rs=st2.executeQuery(
					"select currval('galleries_gallery_id_seq')");
				rs.next();
				id=rs.getInt(1);
				rs.close();
				st2.close();
			}

			// Save the mappings

			// Delete any old mappings that might exist
			pst=conn.prepareStatement(
				"delete from galleries_map where gallery_id=?");
			pst.setInt(1, id);
			pst.executeUpdate();
			pst.close();

			// Load the new gallery
			pst=conn.prepareStatement(
				"insert into galleries_map(gallery_id,album_id)\n"
					+ " values(?,?)");
			pst.setInt(1, id);

			for(Enumeration e=images.elements(); e.hasMoreElements(); ) {
				PhotoImageData pid=(PhotoImageData)e.nextElement();

				pst.setInt(2, pid.getId());
				affected=pst.executeUpdate();
				if(affected!=1) {
					throw new PhotoException(
						"Expected to affect one row, affected " + affected);
				}
			}
			pst.close();

			// Done, commit
			conn.commit();

		} catch(Exception e) {
			if(conn!=null) {
				try {
					conn.rollback();
				} catch(SQLException sqle) {
					sqle.printStackTrace();
				}
			}
			throw new PhotoException("Error saving new gallery", e);
		} finally {
			if(conn!=null) {
				try {
					conn.setAutoCommit(true);
				} catch(SQLException e) {
					e.printStackTrace();
				}
			}
			// Close the DB
			db.close();
		}
	}

	/**
	 * Add a new image to the gallery.
	 */
	public void addImage(PhotoImageData pid) {
		removeImage(pid);
		images.addElement(pid);
	}

	/**
	 * Add a new image to the gallery.
	 */
	public void addImage(int imageId) throws Exception {
		PhotoImageData pid=PhotoImageData.getData(imageId);
		addImage(pid);
	}

	/**
	 * String me.
	 */
	public String toString() {
		return("{Gallery name=\"" + name + "\", id=" + id
			+ ", images:  " + images + "}");
	}

	/**
	 * Remove an image from the given gallery.
	 */
	public void removeImage(PhotoImageData pid) {
		images.remove(pid);
	}

	/**
	 * Get this gallery's ID.
	 */
	public int getId() {
		return(id);
	}

	/**
	 * Get the name of this gallery.
	 */
	public String getName() {
		return(name);
	}

	/**
	 * Set the name of this Gallery.
	 */
	public void setName(String to) {
		this.name=to;
	}

	/**
	 * Get an enumeration of PhotoImageData objects describing the contents
	 * of this gallery.
	 */
	public Enumeration getImages() {
		return(images.elements());
	}

	/**
	 * If true, the gallery is public.
	 */
	public boolean isPublic() {
		return(isPublic);
	}

	/**
	 * If true, the gallery is public.
	 */
	public void setPublic(boolean isPublic) {
		this.isPublic=isPublic;
	}

	/**
	 * Get the timestamp this gallery was created.
	 */
	public Date getTimestamp() {
		return(timestamp);
	}

	/**
	 * Get the owner of this gallery.
	 */
	public PhotoUser getOwner() {
		return(owner);
	}

	/**
	 * Testing and what not.
	 */
	public static void main(String args[]) throws Exception {

		PhotoSecurity sec=new PhotoSecurity();
		if(args.length == 0) {
			PhotoUser user=sec.getUser("dustin");
			Gallery g=new Gallery(user, "Test Gallery");

			g.addImage(3985);
			g.addImage(3929);
			g.addImage(4009);

			g.save();

			System.out.println(g);
		} else if(args.length == 1) {
			PhotoUser user=sec.getUser(args[0]);
			for(Cursor c=getGalleries(user); c.hasMoreElements(); ) {
				Gallery g=(Gallery)c.nextElement();
				System.out.println(g);
			}
		} else {
			PhotoUser user=sec.getUser(args[0]);
			Gallery g=getGallery(user, Integer.parseInt(args[1]));
			System.out.println(g);
		}
	}

}
