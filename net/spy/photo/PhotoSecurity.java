/*
 * Copyright (c) 1999 Dustin Sallings <dustin@spy.net>
 *
 * $Id: PhotoSecurity.java,v 1.6 2000/07/07 06:05:37 dustin Exp $
 */

package net.spy.photo;

import java.security.*;
import java.util.*;
import java.sql.*;
import sun.misc.*;

import net.spy.*;

public class PhotoSecurity extends PhotoHelper {

	public PhotoSecurity() throws Exception {
		super();
	}

	// Verify a password against what is stored in the database.
	public boolean checkPW(String user, String pass) {
		boolean ret=false;
		Connection db=null;
		try {
			String tpw=getDigest(pass);
			String pw=null;

			PhotoUser pu=getUser(user);
			pw=pu.password;

			log("Testing for " + tpw + " = " + pw);
			ret=tpw.equals(pw);
		} catch(Exception e) {
			// Nothing.
		} finally {
			try {
				freeDBConn(db);
			} catch(Exception e2) {
				// Nothing.
			}
		}
		return(ret);
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
		PhotoCache cache=new PhotoCache();
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
					PhotoUser u = new PhotoUser();
					u.id=new Integer(rs.getInt("id"));
					u.username=rs.getString("username");
					u.password=rs.getString("password");
					u.email=rs.getString("email");
					u.realname=rs.getString("realname");
					u.canadd=rs.getBoolean("canadd");
					// User cache is valid for 30 minutes
					cache.store(key, u, 30*60*1000);
					ret=u;
				}
			} catch(Exception e) {
				log("Error lookup up user:  " + username);
			} finally {
				if(photo!=null) {
					freeDBConn(photo);
				}
			}
		}

		return(ret);
	}
}
