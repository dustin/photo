// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: E9E478DD-5D6C-11D9-B8CA-000A957659CC

package net.spy.photo;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.spy.db.SpyDB;
import net.spy.db.AbstractSavable;
import net.spy.db.Saver;
import net.spy.db.SaveContext;
import net.spy.db.SaveException;
import net.spy.cache.SpyCache;

import net.spy.photo.sp.GetKeywords;
import net.spy.photo.sp.InsertKeyword;

/**
 * This class represents a keyword in the database.
 */
public class Keyword extends AbstractSavable implements Comparable<Keyword> {

	private static final String CACHE_KEY="keywords";
	private static final int CACHE_TIME=3600000;

	private String keyword=null;
	private int keywordId=-1;

	/**
	 * Get a new instance of Keyword.
	 */
	private Keyword(String word) throws Exception {
		super();
		getNewId();
		this.keyword=word;
	}

	private Keyword(ResultSet rs) throws Exception {
		super();
		initFromResultSet(rs);
		setModified(false);
		setNew(false);
	}

	private void initFromResultSet(ResultSet rs) throws Exception {
		keyword=rs.getString("word");
		keywordId=rs.getInt("word_id");
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
	 * Get a keyword by ID.
	 */
	public static Keyword getKeyword(int id) throws Exception {
		Cache c=getCache();
		Keyword rv=c.byId.get(new Integer(id));
		if(rv == null) {
			throw new Exception("No such keyword id:  " + id);
		}
		return(rv);
	}

	/** 
	 * Get a keyword by name.
	 * 
	 * @param kw the keyword
	 * @return the Keyword instance, or null if there's no such keyword
	 */
	public static Keyword getKeyword(String kw) throws Exception {
		return(getKeyword(kw, false));
	}

	/** 
	 * Get a keyword by word.  Optionally, a new keyword instance may be
	 * created if one does not exist.
	 */
	public static Keyword getKeyword(String kw, boolean create)
		throws Exception {
		if(kw == null) {
			throw new NullPointerException("Null keyword not allowed");
		}
		kw=kw.trim().toLowerCase();
		Cache c=getCache();
		Keyword rv=c.byWord.get(kw);
		if(rv == null && create) {
			// If this didn't match an existing keyword, make a new one
			rv=new Keyword(kw);
			// Save it
			Saver s=new Saver(PhotoConfig.getInstance());
			s.save(rv);
			// Cache it
			c.byWord.put(rv.getKeyword(), rv);
			c.byId.put(new Integer(rv.getId()), rv);
		}
		return(rv);
	}

	private static Cache getCache() throws Exception {
		SpyCache sc=SpyCache.getInstance();
		Cache rv=(Cache)sc.get(CACHE_KEY);
		if(rv == null) {
			rv=initFromDB();
			sc.store(CACHE_KEY, rv, CACHE_TIME);
		}
		return(rv);
	}

	private static Cache initFromDB() throws Exception {
		Cache rv=new Cache();
		GetKeywords db=new GetKeywords(PhotoConfig.getInstance());
		ResultSet rs=db.executeQuery();
		while(rs.next()) {
			Keyword k=new Keyword(rs);
			rv.byId.put(new Integer(k.getId()), k);
			rv.byWord.put(k.getKeyword(), k);
		}
		rs.close();
		db.close();
		return(rv);
	}

	/**
	 * String me.
	 */
	public String toString() {
		StringBuffer sb=new StringBuffer(64);

		sb.append("{Keyword id=");
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

		if(o instanceof Keyword) {
			Keyword k=(Keyword)o;

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
	public int compareTo(Keyword other) {
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

	private static class Cache {
		public Map<Integer, Keyword> byId=null;
		public Map<String, Keyword> byWord=null;

		public Cache() {
			super();
			byId=Collections.synchronizedMap(new HashMap());
			byWord=Collections.synchronizedMap(new HashMap());
		}
	}

}
