// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>
// arch-tag: 4CEA2C48-5D6D-11D9-90EF-000A957659CC

package net.spy.photo.impl;

import java.util.TreeSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Date;
import java.util.StringTokenizer;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.ParseException;

import net.spy.db.SpyDB;
import net.spy.db.AbstractSavable;
import net.spy.db.Saver;
import net.spy.db.SaveContext;
import net.spy.db.SaveException;

import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoImage;
import net.spy.photo.PhotoImageData;
import net.spy.photo.PhotoDimensions;
import net.spy.photo.PhotoUser;
import net.spy.photo.Format;
import net.spy.photo.Keyword;
import net.spy.photo.PhotoException;
import net.spy.photo.ImageServer;
import net.spy.photo.ImageServerFactory;
import net.spy.photo.PhotoImageDataFactory;
import net.spy.photo.PhotoUtil;
import net.spy.photo.sp.InsertImage;
import net.spy.photo.sp.InsertKeywordMap;
import net.spy.photo.sp.DeleteKeywordMap;
import net.spy.photo.sp.UpdateImage;

/**
 * Savable implementation of PhotoImageData.
 */
public class SavablePhotoImageData extends AbstractSavable
	implements PhotoImageData {

	private Collection keywords=null;
	private String descr=null;
	private int catId=-1;
	private int size=-1;
	private PhotoDimensions dimensions=null;
	private PhotoDimensions tnDims=null;
	private PhotoUser addedBy=null;
	private String catName=null;
	private Date taken=null;
	private Date timestamp=null;
	private int id=-1;
	private Format format=null;
	private PhotoImage imageData=null;

	/**
	 * Get a new savable photo.
	 *
	 * The following attributes will be initialized:
	 * <ul>
	 *  <li>dimensions</li>
	 *  <li>size</li>
	 *  <li>format</li>
	 *  <li>timestamp</li>
	 *  <li>id</li>
	 * </ul>
	 *
	 * @param data the data for the image
	 * @throws PhotoException If a new photo id cannot be obtained
	 */
	public SavablePhotoImageData(PhotoImage data) throws PhotoException {
		super();

		this.imageData=data;
		this.dimensions=data;
		this.size=data.size();
		this.format=data.getFormat();
		this.timestamp=new Date();

		keywords=new TreeSet();

		id=getNewImageId();
		setNew(true);
	}

	/** 
	 * Get an editable wrapper around the given PhotoImageData.
	 */
	public SavablePhotoImageData(PhotoImageData proto) {
		super();

		this.keywords=proto.getKeywords();
		this.descr=proto.getDescr();
		this.catId=proto.getCatId();
		this.size=proto.getSize();
		this.dimensions=proto.getDimensions();
		this.tnDims=proto.getTnDims();
		this.addedBy=proto.getAddedBy();
		this.catName=proto.getCatName();
		this.taken=proto.getTaken();
		this.timestamp=proto.getTimestamp();
		this.id=proto.getId();
		this.format=proto.getFormat();

		setNew(false);
		setModified(false);
	}

	private void saveNew(Connection conn) throws SaveException, SQLException {
		// Check access
		if(!addedBy.canAdd(catId)) {
			throw new SaveException("User " + addedBy + " cannot add to cat "
				+ catId);
		}
		InsertImage db=new InsertImage(conn);
		db.setImageId(id);
		db.setDescription(descr);
		db.setCatId(catId);
		db.setTaken(new java.sql.Date(taken.getTime()));
		db.setSize(size);
		db.setAddedBy(addedBy.getId());
		db.setTimestamp(new java.sql.Timestamp(timestamp.getTime()));
		db.setWidth(dimensions.getWidth());
		db.setHeight(dimensions.getHeight());
		db.setFormatId(format.getId());
		int aff=db.executeUpdate();
		if(aff != 1) {
			throw new SaveException("Expected to update 1 row, updated " + aff);
		}
		db.close();

		saveKeywords(conn);

		// Get the photo cached
		try {
			ImageServer server=ImageServerFactory.getImageServer();
			server.storeImage(id, imageData);
		} catch(Exception e) {
			throw new SaveException("Cannot cache photo", e);
		}
	}

	private void saveKeywords(Connection conn)
		throws SaveException, SQLException {

		InsertKeywordMap ikm=new InsertKeywordMap(conn);
		ikm.setPhotoId(id);

		for(Iterator i=keywords.iterator(); i.hasNext(); ) {
			Keyword k=(Keyword)i.next();
			ikm.setWordId(k.getId());
			int aff=ikm.executeUpdate();
			if(aff != 1) {
				throw new SaveException("Expected to update 1 row, updated "
					+ aff);
			}
		}

		ikm.close();
	}

	private void saveUpd(Connection conn) throws SaveException, SQLException {
		UpdateImage db=new UpdateImage(conn);
		db.setDescr(descr);
		db.setCat(catId);
		db.setTaken(new java.sql.Date(taken.getTime()));
		db.setId(id);
		int aff=db.executeUpdate();
		if(aff != 1) {
			throw new SaveException("Expected to update 1 row, updated " + aff);
		}
		db.close();

		// Delete the old keyword map
		DeleteKeywordMap dkm=new DeleteKeywordMap(conn);
		dkm.setPhotoId(id);
		dkm.executeUpdate();

		// Insert a new keyword map
		saveKeywords(conn);
	}

	public void save(Connection conn, SaveContext ctx)
		throws SaveException, SQLException {

		if(isNew()) {
			saveNew(conn);
		} else {
			saveUpd(conn);
		}
		// Clear the photo image cache
		PhotoImageDataFactory pidf=PhotoImageDataFactory.getInstance();
		pidf.recache();
	}

	/**
	 * Get a new ID for a photo.
	 */
	private static int getNewImageId() throws PhotoException {
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
	 * Set the keywords from a keyword string.
	 */
	public void setKeywords(String kw) throws Exception {
		keywords.clear();
		StringTokenizer st=new StringTokenizer(kw);
		while(st.hasMoreTokens()) {
			keywords.add(Keyword.getKeyword(st.nextToken(), true));
		}
		modify();
	}

	public void setKeywords(Collection to) {
		this.keywords=to;
		modify();
	}

	public Collection getKeywords() {
		return(keywords);
	}

	public void setDescr(String to) {
		this.descr=to;
		modify();
	}

	public String getDescr() {
		return(descr);
	}

	public void setCatId(int to) {
		this.catId=to;
		modify();
	}

	public int getCatId() {
		return(catId);
	}

	public void setSize(int to) {
		this.size=to;
		modify();
	}

	public int getSize() {
		return(size);
	}

	public void setDimensions(PhotoDimensions to) {
		this.dimensions=to;
		modify();
	}

	public PhotoDimensions getDimensions() {
		return(dimensions);
	}

	public void setTnDims(PhotoDimensions to) {
		this.tnDims=to;
		modify();
	}

	public PhotoDimensions getTnDims() {
		return(tnDims);
	}

	public void setAddedBy(PhotoUser to) {
		this.addedBy=to;
		modify();
	}

	public PhotoUser getAddedBy() {
		return(addedBy);
	}

	public void setCatName(String to) {
		this.catName=to;
		modify();
	}

	public String getCatName() {
		return(catName);
	}

	/** 
	 * Set the taken date from a string in one of the photo date formats.
	 */
	public void setTaken(String to) {
		Date d=PhotoUtil.parseDate(to);
		if(d == null) {
			throw new RuntimeException("Invalid date format:  " + to);
		}
		setTaken(d);
	}

	public void setTaken(Date to) {
		this.taken=to;
		modify();
	}

	public Date getTaken() {
		return(taken);
	}

	public void setTimestamp(Date to) {
		this.timestamp=to;
		modify();
	}

	public Date getTimestamp() {
		return(timestamp);
	}

	public void setId(int to) {
		this.id=to;
		modify();
	}

	public int getId() {
		return(id);
	}

	public void setFormat(Format to) {
		this.format=to;
		modify();
	}

	public Format getFormat() {
		return(format);
	}

}
