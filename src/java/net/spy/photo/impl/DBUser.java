// Copyright (c) 1999  Dustin Sallings
// arch-tag: 4423AD32-5D6D-11D9-BFE1-000A957659CC

package net.spy.photo.impl;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.spy.db.AbstractSavable;
import net.spy.db.SaveContext;
import net.spy.db.SaveException;
import net.spy.photo.MutableUser;
import net.spy.photo.Persistent;
import net.spy.photo.PhotoACL;
import net.spy.photo.PhotoACLEntry;
import net.spy.photo.PhotoException;
import net.spy.photo.PhotoSecurity;
import net.spy.photo.User;
import net.spy.photo.UserFactory;
import net.spy.photo.sp.DeleteACLForUser;
import net.spy.photo.sp.GetGeneratedKey;
import net.spy.photo.sp.InsertACLEntry;
import net.spy.photo.sp.InsertUser;
import net.spy.photo.sp.ModifyUser;
import net.spy.photo.sp.UpdateUser;

/**
 * Represents a user in the photo system.
 */
public class DBUser extends AbstractSavable
	implements Serializable, MutableUser {

	private int id=-1;
	private String name=null;
	private String password=null;
	private String email=null;
	private String realname=null;
	private boolean canadd=false;
	private String persess=null;

	private PhotoACL acl=null;
	private Set<String> roles=null;

	/**
	 * Get a new, empty user.
	 */
	public DBUser() {
		super();
		acl=new PhotoACL();
		roles=new HashSet<String>();
		setNew(true);
		setModified(false);
	}

	// Get the user represented by the current row of this result set
	public DBUser(ResultSet rs) throws SQLException, PhotoException {
		this();
		setId(rs.getInt("id"));
		setName(rs.getString("username"));
		setPassword(rs.getString("password"));
		setEmail(rs.getString("email"));
		setRealname(rs.getString("realname"));
		canAdd(rs.getBoolean("canadd"));
		setPersess(rs.getString("persess"));

		setNew(false);
		setModified(false);
	}

	/**
	 * String me.
	 */
	public String toString() {
		StringBuffer sb=new StringBuffer(64);

		sb.append("{DBUser: username=");
		sb.append(name);
		sb.append("}");

		return (sb.toString());
	}

	/** 
	 * Hashcode of this object.
	 * 
	 * @return the hashcode of the username
	 */
	public int hashCode() {
		return(name.hashCode());
	}

	/** 
	 * True if these objects are equal.
	 * 
	 * @param o the other object
	 * @return true if o is a DBUser with the same ID
	 */
	public boolean equals(Object o) {
		boolean rv=false;
		if(o instanceof User) {
			User u=(User)o;
			rv= (id == u.getId());
		}
		return(rv);
	}

	/** 
	 * The principal name (username).
	 */
	public String getName() {
		return(name);
	}

	/**
	 * Get the user's E-mail address.
	 */
	public String getEmail() {
		return(email);
	}

	/** 
	 * Set the persistent session ID.
	 */
	public void setPersess(String to) {
		this.persess=to;
		setModified(true);
	}

	/** 
	 * Get the persistent session ID.
	 */
	public String getPersess() {
		return(persess);
	}

	/**
	 * Get the ACL list.
	 */
	public PhotoACL getACL() {
		return(acl);
	}

	public void addRole(String role) {
		roles.add(role);
	}

	public void removeRole(String role) {
		roles.remove(role);
	}

	public void clearRoles() {
		roles.clear();
	}

	/**
	 * Get a Collection of Strings describing all the roles this user has.
	 */
	public Collection<String> getRoles() {
		return(Collections.unmodifiableSet(roles));
	}

	/**
	 * True if the user is in the given group.
	 */
	public boolean isInRole(String roleName) {
		return(roles.contains(roleName));
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
		return(acl.canAdd(cat));
	}

	/**
	 * True if the user can view images in the specific category.
	 */
	public boolean canView(int cat) {
		return(acl.canView(cat));
	}

	public void canAdd(boolean to) {
		this.canadd=to;
		if(canadd) {
			addRole(User.CANADD);
		} else {
			removeRole(User.CANADD);
		}
	}

	/**
	 * Set the user's addability.
	 */
	public void setCanAdd(boolean to) {
		this.canadd=to;
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
	public void setId(int to) {
		this.id=to;
		setModified(true);
	}

	/**
	 * Set the username of this user.
	 */
	public void setName(String username) {
		this.name=username.toLowerCase();
		setModified(true);
	}

	/**
	 * Set the real name of this user.
	 */
	public void setRealname(String to) {
		this.realname=to;
		setModified(true);
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
	public void setEmail(String to) {
		this.email=to.toLowerCase();
		setModified(true);
	}

	// Savable implementation

	/**
	 * Save the user.
	 */
	public void save(Connection conn, SaveContext context)
		throws SaveException, SQLException {

		ModifyUser db=null;

		// Determine whether this is a new user or not.
		if(isNew()) {
			db=new InsertUser(conn);
		} else {
			db=new UpdateUser(conn);
			((UpdateUser)db).setUserId(getId());
		}

		// Set the common fields and update.
		db.setUsername(name);
		db.setRealname(realname);
		db.setEmail(email);
		db.setPassword(password);
		db.setCanadd(canadd);
		db.setPersess(persess);

		db.executeUpdate();
		db.close();
		db=null;

		// For new users, We need to fetch the ID
		if(id==-1) {
			GetGeneratedKey gkey=new GetGeneratedKey(conn);
			gkey.setSeq("wwwusers_id_seq");
			ResultSet rs=gkey.executeQuery();
			rs.next();
			id=rs.getInt("key");
			rs.close();
			gkey.close();
		}

		// OK, now let's save the ACL if it's changed.

		if(acl.isModified()) {

			// First, out with the old.
			DeleteACLForUser dacl=new DeleteACLForUser(conn);
			dacl.setUserId(getId());
			dacl.executeUpdate();
			dacl.close();

			// Then in with the new.
			InsertACLEntry ins=new InsertACLEntry(conn);
			ins.setUserId(getId());

			for(Iterator i=acl.iterator(); i.hasNext(); ) {
				PhotoACLEntry aclEntry=(PhotoACLEntry)i.next();

				ins.setCatId(aclEntry.getWhat());
				ins.setCanView(aclEntry.canView());
				ins.setCanAdd(aclEntry.canAdd());
				ins.executeUpdate();
			}
			ins.close();
		} else {
			getLogger().info("ACL was not modified...not updating.");
		}
	}

	/**
	 * Set the user's password.
	 */
	public void setPassword(String pass) {
		// Make sure the password is hashed
		if(pass.length()<13) {
			PhotoSecurity security=Persistent.getSecurity();
			try {
				pass=security.getDigest(pass);
			} catch(Exception e) {
				throw new RuntimeException("Error digesting password", e);
			}
		}
		this.password=pass;
		setModified(true);
	}

	/**
	 * Get the user's hashed password.	Useful for administration forms and
	 * stuff.
	 */
	public String getPassword() {
		return(password);
	}

	private Object writeReplace() throws ObjectStreamException {
		return(new SerializedForm(getId()));
	}

	private static class SerializedForm implements Serializable {
		private int uid=0;

		public SerializedForm(int i) {
			super();
			this.uid=i;
		}

		private Object readResolve() throws ObjectStreamException {
			User rv=null;
			try {
				UserFactory uf=UserFactory.getInstance();
				rv=uf.getUser(uid);
			} catch(PhotoException e) {
				InvalidObjectException t=new InvalidObjectException(
					"Problem resolving user " + uid);
				t.initCause(e);
				throw t;
			}
			return(rv);
		}
	}
}
