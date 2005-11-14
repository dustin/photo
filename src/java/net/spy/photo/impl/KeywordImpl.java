// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: E9E478DD-5D6C-11D9-B8CA-000A957659CC

package net.spy.photo.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.spy.db.AbstractSavable;
import net.spy.db.SaveContext;
import net.spy.db.SaveException;
import net.spy.db.SpyDB;
import net.spy.photo.Keyword;
import net.spy.photo.PhotoConfig;
import net.spy.photo.sp.InsertKeyword;

/**
 * This class represents a keyword in the database.
 */
public class KeywordImpl extends AbstractSavable implements Keyword, Comparable<KeywordImpl> {

	private String keyword=null;
	private int keywordId=-1;

	/**
	 * Get a new instance of Keyword.
	 */
	public KeywordImpl(String word) throws Exception {
		super();
		getNewId();
		this.keyword=word;
	}

	public KeywordImpl(String w, int i) throws Exception {
		super();
		keyword=w;
		keywordId=i;
		setModified(false);
		setNew(false);
	}

	private void getNewId() throws Exception {
		SpyDB db=new SpyDB(PhotoConfig.getInstance());
		ResultSet rs=db.executeQuery("select nextval('keywords_word_id_seq')");
		if(!rs.next()) {
			throw new Exception("No id found in sequence.");
		}
		keywordId=rs.getInt(1);
		if(rs.next()) {
			throw new Exception("Too many results returned from sequence.");
		}
		rs.close();
		db.close();
	}

	/**
	 * String me.
	 */
	public String toString() {
		StringBuffer sb=new StringBuffer(64);

		sb.append("{KeywordImpl id=");
		sb.append(keywordId);
		sb.append(", word=");
		sb.append(keyword);
		sb.append("}");

		return(sb.toString());
	}

	/**
	 * True if the given object is a PhotoImageData object representing the
	 * same image.
	 */
	public boolean equals(Object o) {
		boolean rv=false;

		if(o instanceof KeywordImpl) {
			KeywordImpl k=(KeywordImpl)o;

			if(keywordId == k.keywordId) {
				rv=true;
			}
		}

		return(rv);
	}

	/** 
	 * Get the hash code for this object.
	 * 
	 * @return the id
	 */
	public int hashCode() {
		return(keywordId);
	}

	/** 
	 * Compare these Keyword objects in their respective keyword natural
	 * orders.
	 */
	public int compareTo(KeywordImpl other) {
		return(keyword.compareTo(other.keyword));
	}

	/**
	 * Get the keyword.
	 */
	public String getKeyword() {
		return(keyword);
	}

	/**
	 * Get the keyword ID.
	 */
	public int getId() {
		return(keywordId);
	}

	// Savable
	public void save(Connection conn, SaveContext context)
		throws SaveException, SQLException {

		InsertKeyword db=new InsertKeyword(conn);
		db.setId(keywordId);
		db.setWord(keyword);
		int aff=db.executeUpdate();
		if(aff != 1) {
			throw new SaveException("Expected to update 1 row, updated " + aff);
		}
		db.close();
		setSaved();
	}

}