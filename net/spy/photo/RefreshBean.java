// Copyright (c) 2003  Dustin Sallings <dustin@spy.net>
//
// $Id: RefreshBean.java,v 1.1 2003/04/25 06:32:23 dustin Exp $

package net.spy.photo;

/**
 * Bean to hold refresh state.
 */
public class RefreshBean extends Object implements java.io.Serializable {

	private String location=null;
	private int delay=0;

	/**
	 * Get an instance of RefreshBean.
	 */
	public RefreshBean() {
		super();
	}

	/** 
	 * String me.
	 */
	public String toString() {
		StringBuffer sb=new StringBuffer(64);
		sb.append("RefreshBean:  ");
		sb.append(delay);
		sb.append("; ");
		sb.append(location);
		return(sb.toString());
	}

	/** 
	 * Get the location to which we will refresh.
	 */
	public String getLocation() {
		return(location);
	}

	/** 
	 * Set the location to which to refresh.
	 */
	public void setLocation(String location) {
		this.location=location;
	}

	/** 
	 * Get the number of seconds to delay before redirecting.
	 */
	public int getDelay() {
		return(delay);
	}

	/** 
	 * Set the number of seconds to delay before redirecting.
	 */
	public void setDelay(int delay) {
		this.delay=delay;
	}

}
