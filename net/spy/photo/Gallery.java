// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: Gallery.java,v 1.1 2002/06/30 07:51:31 dustin Exp $

package net.spy.photo;

import java.util.*;
import java.util.Date;
import java.sql.*;

/**
 * A named collection of images.
 */
public class Gallery extends Object {

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

	// Get the gallery for the current row in the given resultset
	private Gallery(ResultSet rs) throws SQLException {
		throw new SQLException("NOT IMPLEMENTED");
	}

	/**
	 * Get the given gallery by ID as it will be seen by the given user.
	 */
	public static Gallery getGallery(PhotoUser user, int id)
		throws PhotoException {
		throw new PhotoException("NOT IMPLEMENTED");
	}

	/**
	 * Save a new gallery.
	 */
	public void save() throws PhotoException {
		throw new PhotoException("NOT IMPLEMENTED");
	}

	/**
	 * Add a new image to the gallery.
	 */
	public void addImage(PhotoImageData pid) {
		images.addElement(pid);
	}

	/**
	 * Add a new image to the gallery.
	 */
	public void addImage(int imageId) throws Exception {
		PhotoImageData pid=PhotoImageData.getData(imageId);
		images.addElement(pid);
	}

	/**
	 * String me.
	 */
	public String toString() {
		return("{Gallery name=\"" + name + "\" images:  " + images + "}");
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
		PhotoUser user=sec.getUser("dustin");
		Gallery g=new Gallery(user, "Test Gallery");

		g.addImage(2600);
		g.addImage(2069);

		System.out.println(g);
	}

}
