/*
 * Copyright (c) 1999 Dustin Sallings <dustin@spy.net>
 *
 * $Id: PhotoSecurity.java,v 1.14 2002/01/10 10:44:41 dustin Exp $
 */

package net.spy.photo;

import java.security.*;
import java.util.*;
import java.sql.*;

import net.spy.*;
import net.spy.db.*;
import net.spy.cache.*;
import net.spy.util.*;

/**
 * Security dispatch type stuff happens here.
 */
public class PhotoSecurity extends PhotoHelper {

	public PhotoSecurity() throws Exception {
		super();
	}

	/**
	 * Verify a password against what is stored in the database.
	 *
	 * @deprecated Why would I want to do this?
	 */
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
		Base64 base64=new Base64();
		String out = base64.encode(md.digest());
		out = out.replace('+', '/');
		out=out.trim();
		// Get rid of = signs.
		while(out.endsWith("=")) {
			out=out.substring(0,out.length()-1);
		}
		return(out.trim());
	}

	// All the common stuff for loading a user happens here.
	private PhotoUser getUser(Connection conn, PreparedStatement st)
		throws Exception {

			ResultSet rs=st.executeQuery();
			if(!rs.next()) {
				throw new Exception("No results while loading user.");
			}

			PhotoUser u = getUser(rs);

			rs.close();
			st.close();
			st=conn.prepareStatement(
				"select cat, canview, canadd from wwwacl where userid=?");
			st.setInt(1, u.getId());
			rs=st.executeQuery();
			// Add the ACL entries.
			while(rs.next()) {
				int cat=rs.getInt("cat");
				if(rs.getBoolean("canview")) {
					u.addViewACLEntry(cat);
				}
				if(rs.getBoolean("canadd")) {
					u.addAddACLEntry(cat);
				}
			}
			rs.close();
			st.close();

			return(u);
	}

	/**
	 * Get a user by username
	 */
	public PhotoUser getUser(String username) {
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
				PhotoUser u=getUser(photo, st);

				// User cache is valid for 30 minutes
				cache.store(key, u, 30*60*1000);
				ret=u;

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

	/**
	 * Get a user by integer ID
	 */
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
				PhotoUser u=getUser(photo, st);

				// User cache is valid for 30 minutes
				cache.store(key, u, 30*60*1000);
				ret=u;
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

	/**
	 * Check to see if the given uid has access to the given image ID.
	 */
	public static void checkAccess(int uid, int image_id) throws Exception {
		boolean ok=false;

		SpyCacheDB db=new SpyCacheDB(new PhotoConfig());
		// Verify specific access to viewability.
		PreparedStatement pst=db.prepareStatement(
			"select 1 from album a, wwwacl w\n"
				+ " where id=?\n"
				+ " and a.cat=w.cat\n"
				+ " and canview=true\n"
				+  " and (w.userid=? or w.userid=?)\n", 900);
		pst.setInt(1, image_id);
		pst.setInt(2, uid);
		pst.setInt(3, PhotoUtil.getDefaultId());
		ResultSet rs=pst.executeQuery();

		// If there's a result, access is granted
		if(rs.next()) {
			ok=true;
		}

		rs.close();
		pst.close();
		db.close();

		if(!ok) {
			throw new Exception("Access to image " + image_id
				+ " is not allowed by user " + uid);
		}
	}

	// Load the user info from a result set
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
