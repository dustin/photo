// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>
// arch-tag: 664824DC-60D2-4B7E-B256-39F2A2C9F36B

package net.spy.photo.impl;

import java.util.Collection;
import java.util.Date;
import java.util.TreeSet;

import net.spy.SpyObject;
import net.spy.photo.AnnotatedRegion;
import net.spy.photo.Keyword;
import net.spy.photo.User;

/**
 * Abstract implementation of an annotated region.
 */
public abstract class AnnotatedRegionImpl extends SpyObject
	implements AnnotatedRegion {

	private int id=0;
	private int x=0;
	private int y=0;
	private int width=0;
	private int height=0;
	private String title=null;
	private Collection<Keyword> keywords=null;
	private User user=null;
	private Date timestamp=null;

	/**
	 * Get an instance of AnnotatedRegionImpl.
	 */
	public AnnotatedRegionImpl() {
		super();
		keywords=new TreeSet<Keyword>();
	}

	/** 
	 * Get the ID of this annotated region.
	 */
	public int getId() {
		return(id);
	}

	/** 
	 * Set the ID of this annotated region.
	 */
	protected void setId(int to) {
		this.id=to;
	}

	/** 
	 * Get the X coordinate of this region.
	 */
	public int getX() {
		return(x);
	}

	/** 
	 * Set the X coordinate of this region.
	 */
	protected void setX(int to) {
		this.x=to;
	}

	/** 
	 * Get the Y coordinate of this region.
	 */
	public int getY() {
		return(y);
	}

	/** 
	 * Set the Y coordinate of this region.
	 */
	protected void setY(int to) {
		this.y=to;
	}

	/** 
	 * Get the width of this region.
	 */
	public int getWidth() {
		return(width);
	}

	/** 
	 * Set the width of this region.
	 */
	protected void setWidth(int to) {
		this.width=to;
	}

	/** 
	 * Get the height of this region.
	 */
	public int getHeight() {
		return(height);
	}

	/** 
	 * Set the height of this region.
	 */
	protected void setHeight(int to) {
		this.height=to;
	}

	/** 
	 * Get the title of this region.
	 */
	public String getTitle() {
		return(title);
	}

	/** 
	 * Set the title of this region.
	 */
	protected void setTitle(String to) {
		this.title=to;
	}

	/** 
	 * Get the keywords associated with this region.
	 */
	public Collection<Keyword> getKeywords() {
		return(keywords);
	}

	/** 
	 * Add a keyword.
	 */
	protected void addKeyword(Keyword k) {
		keywords.add(k);
	}

	/** 
	 * Get the user who created this annotation.
	 */
	public User getUser() {
		return(user);
	}

	/** 
	 * Set the user who created this annotation.
	 */
	protected void setUser(User to) {
		this.user=to;
	}

	/** 
	 * Get the timestamp of this annotation.
	 */
	public Date getTimestamp() {
		return(timestamp);
	}

	/** 
	 * Set the timestamp of this annotation.
	 */
	protected void setTimestamp(Date to) {
		this.timestamp=to;
	}

}
