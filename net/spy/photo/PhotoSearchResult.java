/*
 * Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
 *
 * $Id: PhotoSearchResult.java,v 1.24 2002/05/16 18:35:24 dustin Exp $
 */

package net.spy.photo;

import java.sql.*;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.Serializable;

import javax.servlet.*;
import javax.servlet.http.*;

import net.spy.*;
import net.spy.cache.*;

public class PhotoSearchResult extends PhotoHelper implements Serializable {
	private Hashtable mydata=null;
	private int id=-1;
	private int search_id=-1;
	private String html=null;
	private String xml=null;

	private PhotoDimensions maxSize=null;

	/**
	 * Get an uninitialized search result.
	 */
	public PhotoSearchResult() {
		super();
		mydata=new Hashtable();
	}

	/**
	 * Get an uninitialized search result pointing at a given id.
	 */
	public PhotoSearchResult(int id, int search_id) {
		super();
		this.id=id;
		this.search_id=search_id;
        initialize();
	}

	/**
	 * Set the maximum image size to be represented.
	 */
	public void setMaxSize(PhotoDimensions maxSize) {
		this.maxSize=maxSize;
		calculateScaled();
	}

	/**
	 * String representation of this object.
	 */
	public String toString() {
		String out="Photo search result for result ";
		if(id>0) {
			out+=id;
		} else {
			out+=search_id;
		}
		return(out);
	}

	/**
	 * Grab the XML chunk to be displayed.
	 */
	public String showXML(String self_uri) {
		if(xml==null) {
			// Initialize the xml thingy.
			xml="";
			// Make sure we have the data.
			addToHash(null);
			// Calculate the scaled size.
			calculateScaled();
			for(Enumeration e=mydata.keys(); e.hasMoreElements(); ) {
				String key=(String)e.nextElement();
				String data=(String)mydata.get(key);

				xml+="<" + key + ">" + data + "</" + key + ">\n";
			}
		}
		return(xml);
	}

	/**
	 * Place Strings describing yourself into this hash.
	 *
	 * @param h Hashtable to inject.  If the passed in hash is null, it
	 * will create a new one.
	 *
	 * @return the Hashtable with all the stuff in it
	 */
	public Hashtable addToHash(Hashtable h) {
		if(h==null) {
			h=new Hashtable();
		}

		// Make sure we have data
		initialize();

		for(Enumeration e=mydata.keys(); e.hasMoreElements(); ) {
			Object k=e.nextElement();
			h.put(k, mydata.get(k));
		}

		h.put("ID",            "" + search_id);

		return(h);
	}

	private void initialize() {
		// If we are uninitialized, but have an ID, initialize.
		if(id>=0 && mydata==null) {
			try {
				SpyCache pc=new SpyCache();
				mydata=(Hashtable)pc.get("s_result_" + id);
				if(mydata==null) {
					find(id);
					// Store it for fifteen minutes.
					pc.store("s_result_" + id, mydata, 600*1000);
				}
			} catch(Exception e) {
				log("Error getting data for result " + id);
			}
		}
		mydata.put("ID", "" + search_id);
	}

	/**
	 * Populate myself via a database lookup.
	 *
	 * @param id Image ID
	 * @param uid User ID
	 *
	 * @throws Exception on failure
	 */
	public void find(int id) throws Exception {
		this.id=id;

		PhotoImageData pid=PhotoImageData.getData(id);
		storeResult(pid);
	}

	private void storeResult(PhotoImageData pid) throws Exception {

		// If we don't already have a result hash, build one.
		if(mydata==null) {
			mydata=new Hashtable();
		}

		// Grab the components
		mydata.put("IMAGE",    "" + pid.getId());
		mydata.put("KEYWORDS", pid.getKeywords());
		mydata.put("DESCR",    pid.getDescr());
		mydata.put("SIZE",     "" + pid.getSize());
		mydata.put("TAKEN",    pid.getTaken());
		mydata.put("TS",       pid.getTimestamp());
		mydata.put("CAT",      pid.getCatName());
		mydata.put("CATNUM",   "" + pid.getCatId());
		mydata.put("ADDEDBY",  pid.getAddedBy().getUsername());

		// Get the width and the height
		mydata.put("WIDTH",    "" + pid.getWidth());
		mydata.put("HEIGHT",   "" + pid.getHeight());

		calculateThumbnailSize();
	}

	void calculateThumbnailSize() {
		int width=Integer.parseInt((String)mydata.get("WIDTH"));
		int height=Integer.parseInt((String)mydata.get("HEIGHT"));

		PhotoConfig conf=new PhotoConfig();
		PhotoDimensions pdim=new PhotoDimensionsImpl(width, height);
		PhotoDimensions tdim=new PhotoDimensionsImpl(
			conf.get("thumbnail_size"));
		PhotoDimScaler pds=new PhotoDimScaler(pdim);
		PhotoDimensions stn=pds.scaleTo(tdim);

		setTnWidth("" + stn.getWidth());
		setTnHeight("" + stn.getHeight());
	}

	/**
	 * Make sure the scaled width and height are calculated."
	 */
	private void calculateScaled() {
		int width=Integer.parseInt((String)mydata.get("WIDTH"));
		int height=Integer.parseInt((String)mydata.get("HEIGHT"));
		// Calculate the scaled image size
		PhotoDimScaler pds=new PhotoDimScaler(
			new PhotoDimensionsImpl(width, height));
		PhotoDimensions scaled=null;
		if(maxSize!=null) {
			scaled=pds.scaleTo(maxSize);
		} else {
			scaled=new PhotoDimensionsImpl(width, height);
			// System.err.println("!!! DID NOT SCALE !!!");
		}
		mydata.put("SCALED_WIDTH", "" + scaled.getWidth());
		mydata.put("SCALED_HEIGHT", "" + scaled.getHeight());
	}

	public String getScaledWidth() {
		return((String)mydata.get("SCALED_WIDTH"));
	}

	public String getScaledHeight() {
		return((String)mydata.get("SCALED_HEIGHT"));
	}

	public void setKeywords(String to) {
		mydata.put("KEYWORDS", to);
	}

	public String getKeywords() {
		return((String)mydata.get("KEYWORDS"));
	}

	public void setDescr(String to) {
		mydata.put("DESCR", to);
	}

	public String getDescr() {
		return((String)mydata.get("DESCR"));
	}

	public void setCat(String to) {
		mydata.put("CAT", to);
	}

	public String getCat() {
		return((String)mydata.get("CAT"));
	}

	public void setSize(String to) {
		mydata.put("SIZE", to);
	}

	public String getSize() {
		return((String)mydata.get("SIZE"));
	}

	public void setTaken(String to) {
		mydata.put("TAKEN", to);
	}

	public String getTaken() {
		return((String)mydata.get("TAKEN"));
	}

	public void setTs(String to) {
		mydata.put("TS", to);
	}

	public String getTs() {
		return((String)mydata.get("TS"));
	}

	public void setImage(String to) {
		mydata.put("IMAGE", to);
	}

	public String getImage() {
		return((String)mydata.get("IMAGE"));
	}

	public void setCatNum(String to) {
		mydata.put("CATNUM", to);
	}

	public void setAddedBy(String to) {
		mydata.put("ADDEDBY", to);
	}

	public String getAddedBy() {
		return((String)mydata.get("ADDEDBY"));
	}

	public void setWidth(String to) {
		mydata.put("WIDTH", to);
	}

	public String getWidth() {
		return((String)mydata.get("WIDTH"));
	}

	public void setHeight(String to) {
		mydata.put("HEIGHT", to);
	}

	public String getHeight() {
		return((String)mydata.get("HEIGHT"));
	}

	public void setTnWidth(String to) {
		mydata.put("TN_WIDTH", to);
	}

	public String getTnWidth() {
		return((String)mydata.get("TN_WIDTH"));
	}

	public void setTnHeight(String to) {
		mydata.put("TN_HEIGHT", to);
	}

	public String getTnHeight() {
		return((String)mydata.get("TN_HEIGHT"));
	}

	public String getTnSize() {
		return(getTnWidth() + "x" + getTnHeight());
	}

	public void setId(int id) {
		this.search_id=id;
	}

	public String getId() {
		return("" + id);
	}

	public int getCatNum() {
		initialize();
		int ret=Integer.parseInt((String)mydata.get("CATNUM"));
		return(ret);
	}

	/**
	 * Get the Image ID.
	 */
	public int getImageId() {
		return(id);
	}
}
