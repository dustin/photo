// Copyright (c) 1999  Dustin Sallings
//
// $Id: PhotoUser.java,v 1.7 2001/07/19 10:07:09 dustin Exp $

// This class stores an entry from the wwwusers table.

package net.spy.photo;

import java.sql.*;
import java.util.*;
import java.io.Serializable;

import net.spy.*;

/**
 * Represents a user in the photo system.
 */
public class PhotoUser extends Object implements Serializable {
	private int id=-1;
	private String username=null;
	private String password=null;
	private String email=null;
	private String realname=null;
	private boolean canadd=false;

	private Hashtable acl=null;
	private Hashtable groups=null;

	/**
	 * Get a new, empty user.
	 */
	public PhotoUser() {
		super();
		acl=new Hashtable();
	}

	/**
	 * String me.
	 */
	public String toString() {
		return(username);
	}

	/**
	 * Get the username.
	 */
	public String getUsername() {
		return(username);
	}

	/**
	 * Get the list of category IDs (as Integer objects) the user has
	 * access to.
	 */
	public Enumeration getACLEntries() {
		return(acl.keys());
	}

	/**
	 * Add an ACL entry.
	 */
	public void addACLEntry(int cat) {
		Integer c=new Integer(cat);
		acl.put(c, "YEP");
	}

	/**
	 * Remove an ACL entry.
	 */
	public void removeACLEntry(int cat) {
		Integer c=new Integer(cat);
		acl.remove(c);
	}

	/**
	 * Remove all ACL entries.
	 */
	public void removeAllACLEntries() {
		acl.clear();
	}

	/**
	 * Get an Enumeration of Strings describing all the groups this user is
	 * in.
	 */
	public Enumeration getGroups() {
		if(groups==null) {
			initGroups();
		}
		return(groups.keys());
	}

	/**
	 * True if the user is in the given group.
	 */
	public boolean isInGroup(String groupName) {
		if(groups==null) {
			initGroups();
		}
		return(groups.containsKey(groupName));
	}

	// Initialize the groups
	private synchronized void initGroups() {
		if(groups==null) {
			try {
				Hashtable h=new Hashtable();
				SpyDB db=new SpyDB(new PhotoConfig());
				PreparedStatement st=db.prepareStatement(
					"select * from show_group where username = ?");
				st.setString(1, getUsername());
				ResultSet rs=st.executeQuery();
				while(rs.next()) {
					h.put(rs.getString("groupname"), "1");
				}
				rs.close();
				st.close();
				db.close();
				groups=h;
			} catch(Exception e) {
				// Spill your guts.
				e.printStackTrace();
			}
		}
	}

	/**
	 * True if the user can add.
	 */
	public boolean canAdd() {
		return(canadd);
	}

	/**
	 * Set the user's addability.
	 */
	public void canAdd(boolean canadd) {
		this.canadd=canadd;
	}

	/**
	 * Get the ID of this user.
	 */
	public int getId() {
		return(id);
	}

	/**
	 * Set the ID of this user.
	 */
	public void setId(int id) {
		this.id=id;
	}

	/**
	 * Set the username of this user.
	 */
	public void setUsername(String username) {
		this.username=username;
	}

	/**
	 * Set the real name of this user.
	 */
	public void setRealname(String realname) {
		this.realname=realname;
	}

	/**
	 * Set the E-mail address of this  user.
	 */
	public void setEmail(String email) {
		this.email=email;
	}

	/**
	 * Save the user.
	 */
	public void save() throws Exception {
		// Get a DB handle
		SpyDB db=new SpyDB(new PhotoConfig());
		Connection conn=null;
		try {
			conn=db.getConn();
			conn.setAutoCommit(false);
			PreparedStatement st=null;

			// Determine whether this is a new user or not.
			if(id>=0) {
				st=conn.prepareStatement(
					"update wwwusers set username=?, realname=?, email=?, "
						+ "password=?, canadd=?\n"
						+ "\twhere id=?"
					);
				st.setInt(6, getId());
			} else {
				st=conn.prepareStatement(
					"insert into wwwusers(username, realname, email, "
						+ "password, canadd) values(?, ?, ?, ?, ?)"
					);
			}

			// Set the common fields and update.
			st.setString(1, username);
			st.setString(2, realname);
			st.setString(3, email);
			st.setString(4, password);
			st.setBoolean(5, canadd);
			st.executeUpdate();
			st.close();

			// For new users, We need to fetch the ID
			if(id==-1) {
				Statement st2=conn.createStatement();
				ResultSet rs=st2.executeQuery(
					"select currval('wwwusers_id_seq')");
				rs.next();
				id=rs.getInt(1);
				rs.close();
				st2.close();
			}

			// OK, now let's save the ACL.

			// First, out with the old.
			st=conn.prepareStatement("delete from wwwacl where userid=?");
			st.setInt(1, getId());
			st.executeUpdate();
			st.close();

			// Then in with the new.
			st=conn.prepareStatement(
				"insert into wwwacl(userid,cat) values(?,?)");

			for(Enumeration e=getACLEntries(); e.hasMoreElements(); ) {
				Integer i=(Integer)e.nextElement();

				st.setInt(1, getId());
				st.setInt(2, i.intValue());
				st.executeUpdate();
			}
			st.close();
			conn.commit();

		} catch(Exception e) {
			if(conn!=null) {
				conn.rollback();
			}
			throw e;
		} finally {
			if(conn!=null) {
				conn.setAutoCommit(true);
			}
			// Tell it we're done.
			db.close();
		}
	}

	/**
	 * Set the user's password.
	 */
	public void setPassword(String pass) throws Exception {
		// Make sure the password is hashed
		if(pass.length()<13) {
			PhotoSecurity security=new PhotoSecurity();
			pass=security.getDigest(pass);
		}
		this.password=pass;
	}

	/**
	 * Check the user's password.
	 */
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

	/**
	 * XML the user.
	 */
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
