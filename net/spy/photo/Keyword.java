// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: Keyword.java,v 1.1 2002/08/14 06:59:08 dustin Exp $

package net.spy.photo;

import java.sql.ResultSet;
import java.sql.PreparedStatement;

import java.util.Collection;

import net.spy.SpyDB;

/**
 * This class represents a keyword in the database.
 */
public class Keyword extends Object {

	private String keyword=null;
	private int keywordId=-1;

	/**
	 * Get a new instance of Keyword.
	 */
	public Keyword(String word) throws Exception {
		super();
		getNewId();
		this.keyword=word;
	}

	private Keyword(ResultSet rs) throws Exception {
		super();
		initFromResultSet(rs);
	}

	private void initFromResultSet(ResultSet rs) throws Exception {
		keyword=rs.getString("word");
		keywordId=rs.getInt("word_id");
	}

	private void getNewId() throws Exception {
		SpyDB db=new SpyDB(new PhotoConfig());
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
		StringBuffer sb=new StringBuffer();

		sb.append("{Keyword id=");
		sb.append(keywordId);
		sb.append(", word=");
		sb.append(keyword);
		sb.append("}");

		return(sb.toString());
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
	public int getKeywordId() {
		return(keywordId);
	}

}
