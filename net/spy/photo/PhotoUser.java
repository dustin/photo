// Copyright (c) 1999  Dustin Sallings
//
// $Id: PhotoUser.java,v 1.5 2000/12/27 06:05:25 dustin Exp $

// This class stores an entry from the wwwusers table.

package net.spy.photo;

import java.sql.*;

import net.spy.*;

public class PhotoUser extends Object {
	private Integer id=null;
	private String username=null;
	private String password=null;
	private String email=null;
	private String realname=null;
	private boolean canadd=false;

	public PhotoUser() {
		super();
	}

	public String toString() {
		return("Photo User - " + username);
	}

	public boolean canAdd() {
		return(canadd);
	}

	public void canAdd(boolean canadd) {
		this.canadd=canadd;
	}

	public int getId() {
		return(id.intValue());
	}

	public void setId(int id) {
		this.id=new Integer(id);
	}

	public void setUsername(String username) {
		this.username=username;
	}

	public void setRealname(String realname) {
		this.realname=realname;
	}

	public void setEmail(String email) {
		this.email=email;
	}

	public void save() throws Exception {
		// Get a DB handle
		SpyDB db=new SpyDB(new PhotoConfig());
		PreparedStatement st=null;
		// Determine whether this is a new user or not.
		if(id!=null) {
			st=db.prepareStatement(
				"update wwwusers set username=?, realname=?, email=?, "
					+ "password=?, canadd=?\n"
					+ "\twhere id=?"
				);
			st.setString(1, username);
			st.setString(2, realname);
			st.setString(3, email);
			st.setString(4, password);
			st.setBoolean(5, canadd);
			st.setInt(6, getId());
			st.executeUpdate();
		} else {
			st=db.prepareStatement(
				"insert into wwwusers(username, realname, email, "
					+ "password, canadd) values(?, ?, ?, ?, ?)"
				);
			st.setString(1, username);
			st.setString(2, realname);
			st.setString(3, email);
			st.setString(4, password);
			st.setBoolean(5, canadd);
			st.executeUpdate();

			ResultSet rs=st.executeQuery("select currval('wwwusers_id_seq')");
			rs.next();
			id=new Integer(rs.getInt(1));
		}
		// Tell it we're done.
		db.close();
	}

	public void setPassword(String pass) throws Exception {
		// Make sure the password is hashed
		if(pass.length()<13) {
			PhotoSecurity security=new PhotoSecurity();
			pass=security.getDigest(pass);
		}
		this.password=pass;
	}

	public boolean checkPassword(String pass) {
		boolean ret=false;
		try {
			PhotoSecurity security=new PhotoSecurity();
			String tpw=security.getDigest(pass);
			ret=tpw.equals(password);
		} catch(Exception e) {
			// Ignore, leave it false.
		}
		return(ret);
	}

	public String toXML() {
		StringBuffer sb=new StringBuffer();

		sb.append("<photo_user>\n");

		sb.append("\t<id>\n");
		sb.append("\t\t" + id + "\n");
		sb.append("\t</id>\n");

		sb.append("\t<username>\n");
		sb.append("\t\t" + username + "\n");
		sb.append("\t</username>\n");

		sb.append("\t<email>\n");
		sb.append("\t\t" + email + "\n");
		sb.append("\t</email>\n");

		sb.append("\t<realname>\n");
		sb.append("\t\t" + realname + "\n");
		sb.append("\t</realname>\n");

		if(canadd) {
			sb.append("\t<canadd/>\n");
		}

		sb.append("</photo_user>\n");

		return(sb.toString());
	}
}
