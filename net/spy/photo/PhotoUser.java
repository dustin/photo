// Copyright (c) 1999  Dustin Sallings
//
// $Id: PhotoUser.java,v 1.21 2002/07/10 03:38:08 dustin Exp $

// This class stores an entry from the wwwusers table.

package net.spy.photo;

import java.io.Serializable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.spy.SpyDB;

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

	private ArrayList acl=null;
	private Set groups=null;

	/**
	 * Get a new, empty user.
	 */
	public PhotoUser() {
		super();
		acl=new ArrayList();
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
	 * Get the user's E-mail address.
	 */
	public String getEmail() {
		return(email);
	}

	/**
	 * Get the ACL list.
	 */
	public Collection getACLEntries() {
		return(Collections.unmodifiableCollection(acl));
	}

	/**
	 * Get an ACL entry for the given category.
	 *
	 * @param cat the category ID for which you want the entry
	 *
	 * @return the entry, or null if there is no entry for the given cat.
	 */
	public PhotoACLEntry getACLEntryForCat(int cat) {
		PhotoACLEntry rv=null;

		for(Iterator i=getACLEntries().iterator(); rv==null && i.hasNext();) {
			PhotoACLEntry acl=(PhotoACLEntry)i.next();

			if(acl.getCat() == cat) {
				rv=acl;
			}
		}

		return(rv);
	}

	/**
	 * Same as above, but create a new (empty) one if it doesn't exist
	 */
	private PhotoACLEntry getACLEntryForCat2(int cat) {
		PhotoACLEntry rv=getACLEntryForCat(cat);

		if(rv==null) {
			rv=new PhotoACLEntry(getId(), cat);
			acl.add(rv);
		}

		return(rv);
	}

	/**
	 * Add an ACL entry permitting view access to a given category.
	 */
	public void addViewACLEntry(int cat) {
		PhotoACLEntry aclEntry=getACLEntryForCat2(cat);
		aclEntry.setCanView(true);
	}

	/**
	 * Add an ACL entry permitting add access to a given category.
	 */
	public void addAddACLEntry(int cat) {
		PhotoACLEntry aclEntry=getACLEntryForCat2(cat);
		aclEntry.setCanAdd(true);
	}

	/**
	 * Remove an ACL entry.
	 */
	public void removeACLEntry(int cat) {
		PhotoACLEntry entry=getACLEntryForCat(cat);
		if(entry!=null) {
			acl.remove(entry);
		}
	}

	/**
	 * Remove all ACL entries.
	 */
	public void removeAllACLEntries() {
		acl.clear();
	}

	/**
	 * Get a Collection of Strings describing all the groups this user is
	 * in.
	 */
	public Collection getGroups() {
		if(groups==null) {
			initGroups();
		}
		return(Collections.unmodifiableSet(groups));
	}

	/**
	 * True if the user is in the given group.
	 */
	public boolean isInGroup(String groupName) {
		if(groups==null) {
			initGroups();
		}
		return(groups.contains(groupName));
	}

	// Initialize the groups
	private synchronized void initGroups() {
		if(groups==null) {
			try {
				HashSet s=new HashSet();
				SpyDB db=new SpyDB(new PhotoConfig());
				PreparedStatement st=db.prepareStatement(
					"select * from show_group where username = ?");
				st.setString(1, getUsername());
				ResultSet rs=st.executeQuery();
				while(rs.next()) {
					s.add(rs.getString("groupname"));
				}
				rs.close();
				st.close();
				db.close();
				groups=s;
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
	 * True if the user can add to the specific category.
	 */
	public boolean canAdd(int cat) {
		boolean rv=false;
		PhotoACLEntry acl=getACLEntryForCat(cat);
		if(acl!=null && acl.canAdd()) {
			rv=true;
		}
		return(rv);
	}

	/**
	 * True if the user can view images in the specific category.
	 */
	public boolean canView(int cat) {
		boolean rv=false;
		PhotoACLEntry acl=getACLEntryForCat(cat);
		if(acl!=null && acl.canView()) {
			rv=true;
		}
		return(rv);
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
		this.username=username.toLowerCase();
	}

	/**
	 * Set the real name of this user.
	 */
	public void setRealname(String realname) {
		this.realname=realname;
	}

	/**
	 * Get the real name of this user.
	 */
	public String getRealname() {
		return(realname);
	}

	/**
	 * Set the E-mail address of this  user.
	 */
	public void setEmail(String email) {
		this.email=email.toLowerCase();
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
				"insert into wwwacl(userid,cat,canview,canadd) "
				+ "values(?,?,?,?)");

			for(Iterator i=getACLEntries().iterator(); i.hasNext(); ) {
				PhotoACLEntry aclEntry=(PhotoACLEntry)i.next();

				st.setInt(1, getId());
				st.setInt(2, aclEntry.getCat());
				st.setBoolean(3, aclEntry.canView());
				st.setBoolean(4, aclEntry.canAdd());
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
	public void setPassword(String pass) throws PhotoException {
		// Make sure the password is hashed
		if(pass.length()<13) {
			PhotoSecurity security=new PhotoSecurity();
			try {
				pass=security.getDigest(pass);
			} catch(Exception e) {
				throw new PhotoException("Error digesting password", e);
			}
		}
		this.password=pass;
	}

	/**
	 * Get the user's hashed password.	Useful for administration forms and
	 * stuff.
	 */
	public String getPassword() {
		return(password);
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
			// Let it return false.
		}
		return(ret);
	}

	/**
	 * XML the user.
	 */
	public String toXML() {
		StringBuffer sb=new StringBuffer();

		sb.append("<photo_user>\n");

		sb.append("\t<id>");
		sb.append(id);
		sb.append("</id>\n");

		sb.append("\t<username>");
		sb.append(username);
		sb.append("</username>\n");

		sb.append("\t<email>");
		sb.append(email);
		sb.append("</email>\n");

		sb.append("\t<realname>");
		sb.append(realname);
		sb.append("</realname>\n");

		if(canadd) {
			sb.append("\t<canadd/>\n");
		}

		sb.append("</photo_user>\n");

		return(sb.toString());
	}
}
