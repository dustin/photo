// Copyright (c) 1999  Dustin Sallings
//
// $Id: Profile.java,v 1.10 2003/01/07 09:38:51 dustin Exp $

// This class stores an entry from the wwwusers table.

package net.spy.photo;

import java.io.Serializable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.spy.SpyDB;
import net.spy.db.DBSP;
import net.spy.db.AbstractSavable;
import net.spy.db.SaveContext;
import net.spy.db.SaveException;

import net.spy.util.PwGen;

import net.spy.photo.sp.GetGeneratedKey;
import net.spy.photo.sp.ModifyUserProfile;
import net.spy.photo.sp.UpdateUserProfile;
import net.spy.photo.sp.InsertUserProfile;
import net.spy.photo.sp.InsertProfileACL;
import net.spy.photo.sp.DeleteACLForProfile;

/**
 * Represents a user in the photo system.
 */
public class Profile extends AbstractSavable implements Serializable {
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
		setNew(true);
		setModified(false);
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

		setNew(false);
		setModified(false);
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
		setModified(true);
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
		setModified(true);
	}

	// Savable implementation

	/**
	 * Save the Profile.
	 */
	public void save(Connection conn, SaveContext context)
		throws SaveException, SQLException {

		ModifyUserProfile db=null;

		// Determine whether this is a new user or not.
		if(isNew()) {
			db=new InsertUserProfile(conn);
			((InsertUserProfile)db).setName(getName());
		} else {
			db=new UpdateUserProfile(conn);
			((UpdateUserProfile)db).setProfileId(getId());
		}

		// Set the common fields and update.
		db.setDescription(getDescription());
		db.setExpires(new java.sql.Date(expires.getTime()));
		db.executeUpdate();
		db.close();

		// For new users, We need to fetch the ID
		if(isNew()) {
			GetGeneratedKey gkey=new GetGeneratedKey(conn);
			gkey.setSeq("user_profiles_profile_id_seq");
			ResultSet rs=gkey.executeQuery();
			rs.next();
			id=rs.getInt("key");
			rs.close();
			gkey.close();
		}

		// OK, now let's save the ACL.

		// First, out with the old.
		DeleteACLForProfile dap=new DeleteACLForProfile(conn);
		dap.setProfileId(getId());
		dap.executeUpdate();
		dap.close();

		// Then in with the new.
		InsertProfileACL ins=new InsertProfileACL(conn);
		ins.setProfileId(getId());

		for(Iterator it=getACLEntries().iterator(); it.hasNext(); ) {
			Integer i=(Integer)it.next();

			ins.setCatId(i.intValue());
			ins.executeUpdate();
		}
		ins.close();

		setSaved();
	}

}
