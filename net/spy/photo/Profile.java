// Copyright (c) 1999  Dustin Sallings
//
// $Id: Profile.java,v 1.7 2002/11/03 07:33:35 dustin Exp $

// This class stores an entry from the wwwusers table.

package net.spy.photo;

import java.io.Serializable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.spy.SpyDB;

import net.spy.util.PwGen;

/**
 * Represents a user in the photo system.
 */
public class Profile extends Object implements Serializable {
	private int id=-1;
	private String name=null;
	private String description=null;
	private Date expires=null;

	private Set acl=null;

	/**
	 * Get a new, empty user profile.
	 */
	public Profile() {
		super();
		acl=new HashSet();
		name=PwGen.getPass(16);
		// Expires in thirty days.
		expires=new Date(System.currentTimeMillis() + (86400L*30L*1000L));
	}

	/**
	 * Look up a user profile.
	 */
	public Profile(String id) throws Exception {
		super();
		SpyDB db=new SpyDB(new PhotoConfig());
		PreparedStatement pst=db.prepareStatement(
			"select * from user_profiles where name=? and expires>now()");
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
		this.expires=rs.getDate("expires");
		rs.close();
		pst.close();
		db.close();
		initACLs();
	}

	// Get the ACL entries out of the DB.
	private void initACLs() throws Exception {
		acl=new HashSet();
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
		StringBuffer sb=new StringBuffer(128);
		sb.append("Profile:  ");
		sb.append(name);
		sb.append(", expires:  ");
		sb.append(expires);
		sb.append(":\n");
		for(Iterator i=getACLEntries().iterator(); i.hasNext(); ) {
			sb.append("\t");
			sb.append(i.next());
			sb.append("\n");
		}
		return(sb.toString());
	}

	/**
	 * Get the profile name.
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
	 * Get the Set of category IDs (as Integer objects) the user has
	 * access to.
	 */
	public Collection getACLEntries() {
		return(Collections.unmodifiableSet(acl));
	}

	/**
	 * Add an ACL entry.
	 */
	public void addACLEntry(int cat) {
		Integer c=new Integer(cat);
		acl.add(c);
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
					"update user_profiles set description=?, expires=? "
						+ "\twhere id=?"
					);
				st.setInt(3, getId());
			} else {
				st=conn.prepareStatement(
					"insert into user_profiles(description, expires, name)\n"
						+ " values(?, ?, ?)"
					);
				st.setString(3, getName());
			}

			// Set the common fields and update.
			st.setString(1, getDescription());
			st.setDate(2, new java.sql.Date(expires.getTime()));
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

			for(Iterator it=getACLEntries().iterator(); it.hasNext(); ) {
				Integer i=(Integer)it.next();

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
			// p.addACLEntry(19);
			// p.addACLEntry(15);
			// p.addACLEntry(9);
			// p.addACLEntry(4);
			// p.addACLEntry(8);
			// p.addACLEntry(3);

			p.addACLEntry(8);
			p.addACLEntry(19);
			p.addACLEntry(4);
			p.addACLEntry(1);
			p.addACLEntry(3);
			p.addACLEntry(9);

			p.setDescription("Noelani's Special Thing");

			p.save();
		}
		System.out.println(p);
	}
}
