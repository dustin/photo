/*
 * Copyright (c) 1999 Dustin Sallings <dustin@spy.net>
 *
 * $Id: PhotoSecurity.java,v 1.27 2002/07/10 03:38:08 dustin Exp $
 */

package net.spy.photo;

import java.security.MessageDigest;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;

import net.spy.SpyDB;

import net.spy.cache.SpyCache;

import net.spy.photo.sp.ListUsers;

import net.spy.util.Base64;

/**
 * Security dispatch type stuff happens here.
 */
public class PhotoSecurity extends PhotoHelper {

	/**
	 * Get a PhotoSecurity object.
	 */
	public PhotoSecurity() {
		super();
	}

	/**
	 * Get a digest for a given string.
	 */
	public String getDigest(String input) throws Exception {
		// We need the trim so we can ignore whitespace.
		byte dataB[]=input.trim().getBytes();
		PhotoConfig conf=getConfig();
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
	private PhotoUser getUser(SpyDB conn, PreparedStatement st)
		throws Exception {

			ResultSet rs=st.executeQuery();
			if(!rs.next()) {
				throw new Exception("No results while loading user.");
			}

			PhotoUser u = getUser(rs);

			rs.close();
			st.close();
			st=conn.prepareStatement(
				"select distinct cat, canview, canadd\n"
					+ " from wwwacl where userid=?");
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

	// Get a user by username.
	private PhotoUser getUserByUsername(String username) {
		PhotoUser ret=null;

		// Lowercase the username before the lookup (since it's lowercased
		// on insert)
		username=username.toLowerCase();

		// Grab the cache
		SpyCache cache=new SpyCache();
		String key="sec_u_" + username;

		// Get the data from cache
		ret = (PhotoUser)cache.get(key);

		// If it's not cached, grab it from the DB.
		if(ret==null) {
			try {
				SpyDB db=new SpyDB(getConfig());
				PreparedStatement st=db.prepareStatement(
					"select * from wwwusers where username=?"
					);
				st.setString(1, username);
				PhotoUser u=getUser(db, st);

				// User cache is valid for 30 minutes
				cache.store(key, u, 30*60*1000);
				ret=u;
				db.close();
			} catch(Exception e) {
				log("Error lookup up user " + username);
				e.printStackTrace();
			}
		}

		return(ret);
	}
	
	// Get a user by recorded Email address.
	private PhotoUser getUserByEmail(String email) {
		PhotoUser ret=null;

		// Grab the cache
		SpyCache cache=new SpyCache();
		String key="sec_e_" + email;

		// Get the data from cache
		ret = (PhotoUser)cache.get(key);

		// If it's not cached, grab it from the DB.
		if(ret==null) {
			try {
				SpyDB db=new SpyDB(getConfig());
				PreparedStatement st=db.prepareStatement(
					"select * from wwwusers where email=?"
					);
				st.setString(1, email);
				PhotoUser u=getUser(db, st);

				// User cache is valid for 30 minutes
				cache.store(key, u, 30*60*1000);
				ret=u;
				db.close();
			} catch(Exception e) {
				log("Error lookup up user " + email);
				e.printStackTrace();
			}
		}

		return(ret);
	}

	/**
	 * Get a user.  If the argument contains an @, it looks up by the
	 * E-mail address instead of the username.
	 */
	public PhotoUser getUser(String spec) {
		PhotoUser ret=null;

		if(spec.indexOf("@") > 0) {
			ret=getUserByEmail(spec);
		} else {
			ret=getUserByUsername(spec);
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
			try {
				SpyDB db=new SpyDB(getConfig());
				PreparedStatement st=db.prepareStatement(
					"select * from wwwusers where id=?"
					);
				st.setInt(1, id);
				PhotoUser u=getUser(db, st);

				// User cache is valid for 30 minutes
				cache.store(key, u, 30*60*1000);
				ret=u;
				db.close();
			} catch(Exception e) {
				log("Error lookup up user " + id);
				e.printStackTrace();
			}
		}

		return(ret);
	}

	/**
	 * The preferred way to check a user's access to an image.
	 */
	public static void checkAccess(PhotoUser user, int image_id) throws
		Exception {

		boolean ok=false;

		try {
			PhotoImageData pid=PhotoImageData.getData(image_id);
			ok=user.canView(pid.getCatId());
			
			if(!ok) {
				PhotoUser u=PhotoUtil.getDefaultUser();
				ok=u.canView(pid.getCatId());
			}
		} catch(Exception e) {
			// Will return false
			e.printStackTrace();
		}

		if(!ok) {
			throw new Exception("Access to image " + image_id
				+ " is not allowed by user " + user);
		}
	}

	/**
	 * Check to see if the given uid has access to the given image ID.
	 *
	 * <b>Note</b>:  This is not generally the right way to do this.
	 */
	public static void checkAccess(int uid, int image_id) throws Exception {
		PhotoSecurity sec=new PhotoSecurity();
		PhotoUser u=sec.getUser(uid);

		checkAccess(u, image_id);
	}

	// Load the user info from a result set
	private static PhotoUser getUser(ResultSet rs) throws Exception {
		PhotoUser u = new PhotoUser();
		u.setId(rs.getInt("id"));
		u.setUsername(rs.getString("username"));
		u.setPassword(rs.getString("password"));
		u.setEmail(rs.getString("email"));
		u.setRealname(rs.getString("realname"));
		u.canAdd(rs.getBoolean("canadd"));
		return(u);
	}

	/**
	 * List all users in alphabetical order by username.
	 *
	 * @return an List of PhotoUser objects
	 */
	public static List getAllUsers() throws Exception {
		ListUsers db=new ListUsers(new PhotoConfig());
		ResultSet rs=db.executeQuery();
		ArrayList al=new ArrayList();

		while(rs.next()) {
			al.add(getUser(rs));
		}

		return(al);
	}
}
