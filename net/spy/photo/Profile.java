// Copyright (c) 1999  Dustin Sallings
//
// $Id: Profile.java,v 1.1 2001/07/20 09:51:56 dustin Exp $

// This class stores an entry from the wwwusers table.

package net.spy.photo;

import java.sql.*;
import java.util.*;
import java.io.Serializable;

import net.spy.*;
import net.spy.util.*;

/**
 * Represents a user in the photo system.
 */
public class Profile extends Object implements Serializable {
	private int id=-1;
	private String name=null;
	private String description=null;

	private Hashtable acl=null;

	/**
	 * Get a new, empty user profile.
	 */
	public Profile() {
		super();
		acl=new Hashtable();
		name=PwGen.getPass(16);
	}

	/**
	 * Look up a user profile.
	 */
	public Profile(String id) throws Exception {
		super();
		SpyDB db=new SpyDB(new PhotoConfig());
		PreparedStatement pst=db.prepareStatement(
			"select * from user_profiles where name=?");
		pst.setString(1, id);
		ResultSet rs=pst.executeQuery();
		if(!rs.next()) {
			rs.close();
			pst.close();
			db.close();
			throw new Exception("No such profile:  " + id);
		}

		this.description=rs.getString("description");
		this.id=rs.getInt("profile_id");
		this.name=id;
		rs.close();
		pst.close();
		db.close();
		initACLs();
	}

	// Get the ACL entries out of the DB.
	private void initACLs() throws Exception {
		acl=new Hashtable();
		SpyDB db=new SpyDB(new PhotoConfig());
		PreparedStatement pst=db.prepareStatement(
			"select cat_id from user_profile_acls where profile_id=?");
		pst.setInt(1, getId());
		ResultSet rs=pst.executeQuery();
		while(rs.next()) {
			addACLEntry(rs.getInt("cat_id"));
		}
		rs.close();
		pst.close();
		db.close();
	}

	/**
	 * String me.
	 */
	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append("Profile:  ");
		sb.append(name);
		sb.append(":\n");
		for(Enumeration e=getACLEntries(); e.hasMoreElements(); ) {
			sb.append("\t");
			sb.append(e.nextElement());
			sb.append("\n");
		}
		return(sb.toString());
	}

	/**
	 * Get the username.
	 */
	public String getName() {
		return(name);
	}

	/**
	 * Get the description of this PhotoProfile.
	 */
	public String getDescription() {
		return(description);
	}

	/**
	 * Set the description of this PhotoProfile.
	 */
	public void setDescription(String description) {
		this.description=description;
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
	 * Save the Profile.
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
					"update user_profiles set description=? "
						+ "\twhere id=?"
					);
				st.setInt(2, getId());
			} else {
				st=conn.prepareStatement(
					"insert into user_profiles(description, name)\n"
						+ " values(?, ?)"
					);
				st.setString(2, getName());
			}

			// Set the common fields and update.
			st.setString(1, getDescription());
			st.executeUpdate();
			st.close();

			// For new users, We need to fetch the ID
			if(id==-1) {
				Statement st2=conn.createStatement();
				ResultSet rs=st2.executeQuery(
					"select currval('user_profiles_profile_id_seq')");
				rs.next();
				id=rs.getInt(1);
				rs.close();
				st2.close();
			}

			// OK, now let's save the ACL.

			// First, out with the old.
			st=conn.prepareStatement("delete from user_profile_acls\n"
				+ " where profile_id=?");
			st.setInt(1, getId());
			st.executeUpdate();
			st.close();

			// Then in with the new.
			st=conn.prepareStatement(
				"insert into user_profile_acls(profile_id,cat_id) values(?,?)");

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
	 * Test.
	 */
	public static void main(String args[]) throws Exception {
		Profile p=null;
		if(args.length>0) {
			p=new Profile(args[0]);
		} else {
			p=new Profile();
			p.addACLEntry(19);
			p.addACLEntry(15);
			p.addACLEntry(9);
			p.addACLEntry(4);
			p.addACLEntry(8);
			p.addACLEntry(3);
			p.setDescription("Any User");

			p.save();
		}
		System.out.println(p);
	}
}
