/*
 * Copyright (c) 1999 Dustin Sallings <dustin@spy.net>
 *
 * $Id: PhotoSecurity.java,v 1.10 2001/04/29 08:18:11 dustin Exp $
 */

package net.spy.photo;

import java.security.*;
import java.util.*;
import java.sql.*;
import sun.misc.*;

import net.spy.*;
import net.spy.cache.*;

public class PhotoSecurity extends PhotoHelper {

	public PhotoSecurity() throws Exception {
		super();
	}

	// Verify a password against what is stored in the database.
	public boolean checkPW(String user, String pass) {
		PhotoUser pu=getUser(user);
		return(pu.checkPassword(pass));
	}

	// Get a digest for a string
	public String getDigest(String input) throws Exception {
		// We need the trim so we can ignore whitespace.
		byte dataB[]=input.trim().getBytes();
		MessageDigest md = MessageDigest.getInstance(conf.get("cryptohash"));
		md.update(dataB);
		BASE64Encoder base64=new BASE64Encoder();
		String out = base64.encodeBuffer(md.digest());
		out = out.replace('+', '/');
		out=out.trim();
		// Get rid of = signs.
		while(out.endsWith("=")) {
			out=out.substring(0,out.length()-1);
		}
		return(out.trim());
	}

	protected PhotoUser getUser(String username) {
		PhotoUser ret=null;

		// Grab the cache
		SpyCache cache=new SpyCache();
		String key="sec_u_" + username;

		// Get the data from cache
		ret = (PhotoUser)cache.get(key);

		// If it's not cached, grab it from the DB.
		if(ret==null) {
			Connection photo=null;
			try {
				photo=getDBConn();
				PreparedStatement st=photo.prepareStatement(
					"select * from wwwusers where username=?"
					);
				st.setString(1, username);
				ResultSet rs=st.executeQuery();
				while(rs.next()) {
					PhotoUser u = getUser(rs);
					// User cache is valid for 30 minutes
					cache.store(key, u, 30*60*1000);
					ret=u;
				}
			} catch(Exception e) {
				log("Error lookup up user " + username);
				e.printStackTrace();
			} finally {
				if(photo!=null) {
					freeDBConn(photo);
				}
			}
		}

		return(ret);
	}

	public PhotoUser getUser(int id) {
		PhotoUser ret=null;

		// Grab the cache
		SpyCache cache=new SpyCache();
		String key="sec_u_id_" + id;

		// Get the data from cache
		ret = (PhotoUser)cache.get(key);

		// If it's not cached, grab it from the DB.
		if(ret==null) {
			Connection photo=null;
			try {
				photo=getDBConn();
				PreparedStatement st=photo.prepareStatement(
					"select * from wwwusers where id=?"
					);
				st.setInt(1, id);
				ResultSet rs=st.executeQuery();
				while(rs.next()) {
					PhotoUser u = getUser(rs);
					// User cache is valid for 30 minutes
					cache.store(key, u, 30*60*1000);
					ret=u;
				}
			} catch(Exception e) {
				log("Error lookup up user " + id);
				e.printStackTrace();
			} finally {
				if(photo!=null) {
					freeDBConn(photo);
				}
			}
		}

		return(ret);
	}

	private PhotoUser getUser(ResultSet rs) throws Exception {
		PhotoUser u = new PhotoUser();
		u.setId(rs.getInt("id"));
		u.setUsername(rs.getString("username"));
		u.setPassword(rs.getString("password"));
		u.setEmail(rs.getString("email"));
		u.setRealname(rs.getString("realname"));
		u.canAdd(rs.getBoolean("canadd"));
		return(u);
	}
}
