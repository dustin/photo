// Copyright (c) 1999  Dustin Sallings
// arch-tag: 4423AD32-5D6D-11D9-BFE1-000A957659CC

// This class stores an entry from the wwwusers table.
package net.spy.photo;

import java.io.Serializable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import java.security.Principal;

import net.spy.cache.SpyCache;
import net.spy.db.SpyDB;
import net.spy.db.DBSP;
import net.spy.db.AbstractSavable;
import net.spy.db.SaveException;
import net.spy.db.SaveContext;

import net.spy.photo.sp.ModifyUser;
import net.spy.photo.sp.InsertUser;
import net.spy.photo.sp.UpdateUser;
import net.spy.photo.sp.DeleteACLForUser;
import net.spy.photo.sp.InsertACLEntry;
import net.spy.photo.sp.GetGeneratedKey;
import net.spy.photo.sp.GetAllUsers;
import net.spy.photo.sp.GetAllACLs;
import net.spy.photo.sp.GetAllRoles;

/**
 * Represents a user in the photo system.
 */
public class PhotoUser extends AbstractSavable
	implements Comparable, Serializable, Principal {

	private int id=-1;
	private String username=null;
	private String password=null;
	private String email=null;
	private String realname=null;
	private boolean canadd=false;
	private String persess=null;

	private PhotoACL acl=null;
	private Set roles=null;

	private static final String CACHE_KEY="n.s.p.PhotoUserMap";
	private static final int CACHE_TIME=3600000; // one hour

	/**
	 * Get a new, empty user.
	 */
	public PhotoUser() {
		super();
		acl=new PhotoACL();
		roles=new HashSet();
		setNew(true);
		setModified(false);
	}

	// Get the user represented by the current row of this result set
	private PhotoUser(ResultSet rs) throws SQLException, PhotoException {
		this();
		setId(rs.getInt("id"));
		setUsername(rs.getString("username"));
		setPassword(rs.getString("password"));
		setEmail(rs.getString("email"));
		setRealname(rs.getString("realname"));
		canAdd(rs.getBoolean("canadd"));
		setPersess(rs.getString("persess"));

		setNew(false);
		setModified(false);
	}

	private static void initACLs(Map userMap, PhotoUser defaultUser)
		throws PhotoUserException {
		try {
			GetAllACLs db=new GetAllACLs(PhotoConfig.getInstance());

			ResultSet rs=db.executeQuery();
			while(rs.next()) {
				Integer userId=new Integer(rs.getInt("userid"));
				PhotoUser pu=(PhotoUser)userMap.get(userId);
				if(pu == null) {
					throw new PhotoUserException("Invalid user in acl map: "
						+ userId);
				}
				int cat=rs.getInt("cat");
				if(rs.getBoolean("canview")) {
					pu.acl.addViewEntry(cat);
				}
				if(rs.getBoolean("canadd")) {
					pu.acl.addAddEntry(cat);
				}
			}
			rs.close();
			db.close();
		} catch(SQLException e) {
			throw new PhotoUserException("Error initializing ACLs", e);
		}
		// Add all of the permissions of the default user to all other users
		for(Iterator i=userMap.values().iterator(); i.hasNext(); ) {
			PhotoUser u=(PhotoUser)i.next();

			for(Iterator i2=u.getACL().iterator(); i2.hasNext(); ) {
				PhotoACLEntry acl=(PhotoACLEntry)i2.next();

				if(acl.canView()) {
					u.acl.addViewEntry(acl.getWhat());
				}
				if(acl.canAdd()) {
					u.acl.addAddEntry(acl.getWhat());
				}
			}
		}
	}

	private static CacheEntry initCacheEntry() throws PhotoUserException {
		PhotoConfig conf=PhotoConfig.getInstance();
		CacheEntry rv=new CacheEntry();
		try {
			Map users=new HashMap();

			DBSP db=new GetAllUsers(conf);
			ResultSet rs=db.executeQuery();
			while(rs.next()) {
				PhotoUser pu=new PhotoUser(rs);
				pu.setNew(false);
				pu.setModified(false);

				// Add it to the list so we can initialize the ACLs.
				users.put(new Integer(pu.getId()), pu);

				// Map the various thingies.
				rv.byId.put(new Integer(pu.getId()), pu);
				rv.byUsername.put(pu.getUsername().toLowerCase(), pu);
				rv.byEmail.put(pu.getEmail().toLowerCase(), pu);
				// Map by persistent ID.
				String psid=pu.getPersess();
				if(psid != null) {
					rv.byPersess.put(psid, pu);
				}
			}
			rs.close();
			db.close();

			// Find the default user so we can initialize the ACLs.
			String defUsername=conf.get("default_user", "guest");
			PhotoUser defaultUser=(PhotoUser)rv.byUsername.get(defUsername);
			if(defaultUser==null) {
				throw new PhotoUserException("Default user not found.");
			}
			// Add the ``guest'' role to the default user.
			defaultUser.addRole("guest");

			// Initialize all the ACLs for all the users
			initACLs(users, defaultUser);

			db=new GetAllRoles(conf);
			rs=db.executeQuery();
			while(rs.next()) {
				Integer id=new Integer(rs.getInt("userid"));
				String r=rs.getString("groupname");
				PhotoUser pu=(PhotoUser)users.get(id);
				if(pu == null) {
					throw new PhotoException("Invalid user in role map: " + id);
				}
				pu.addRole(r);
			}
			rs.close();
			db.close();

		} catch(SQLException e) {
			throw new PhotoUserException("Problem initializing user map", e);
		} catch(PhotoException e) {
			throw new PhotoUserException("Problem initializing user map", e);
		}
		return(rv);
	}

	private static CacheEntry getCacheEntry() throws PhotoUserException {
		SpyCache sc=SpyCache.getInstance();
		CacheEntry rv=(CacheEntry)sc.get(CACHE_KEY);
		if(rv==null) {
			rv=initCacheEntry();
			sc.store(CACHE_KEY, rv, CACHE_TIME);
		}

		return(rv);
	}

	/** 
	 * Look up a user by name or email address.
	 * 
	 * @param spec the username or email address
	 * @return the user
	 *
	 * @throws NoSuchPhotoUserException if the user does not exist
	 * @throws PhotoUserException if there's a problem looking up the user
	 */
	public static PhotoUser getPhotoUser(String spec)
		throws PhotoUserException {

		if(spec==null) {
			throw new NoSuchPhotoUserException("There is no null user.");
		}

		CacheEntry m=getCacheEntry();
		PhotoUser rv=(PhotoUser)m.byUsername.get(spec.toLowerCase());
		if(rv==null) {
			// If that fails, try it by email address
			rv=(PhotoUser)m.byEmail.get(spec.toLowerCase());
			if(rv == null) {
				throw new NoSuchPhotoUserException("No such user:  " + spec);
			}
		}

		return(rv);
	}

	/** 
	 * Get a user by persistent session ID.
	 */
	public static PhotoUser getPhotoUserByPersess(String persess)
		throws PhotoUserException {

		CacheEntry m=getCacheEntry();
		PhotoUser rv=(PhotoUser)m.byPersess.get(persess);
		if(rv==null) {
			throw new NoSuchPhotoUserException("No such session:  " + persess);
		}
		return(rv);
	}

	/** 
	 * Look up a user by user ID.
	 * 
	 * @param id the user ID
	 * @return the user
	 *
	 * @throws NoSuchPhotoUserException if the user does not exist
	 * @throws PhotoUserException if there's a problem looking up the user
	 */
	public static PhotoUser getPhotoUser(int id)
		throws PhotoUserException {

		CacheEntry m=getCacheEntry();
		PhotoUser rv=(PhotoUser)m.byId.get(new Integer(id));
		if(rv==null) {
			throw new NoSuchPhotoUserException("No such user (id):  " + id);
		}

		return(rv);
	}

	/** 
	 * Get all known users.
	 * @return an unmodifiable sorted set of users
	 * @throws PhotoUserException 
	 */
	public static SortedSet getAllPhotoUsers() throws PhotoUserException {
		CacheEntry m=getCacheEntry();
		SortedSet rv=new TreeSet();
		rv.addAll(m.byUsername.values());
		return(Collections.unmodifiableSortedSet(rv));
	}

	/** 
	 * Uncache and recache the users.
	 */
	public static void recache() throws PhotoUserException {
		//  Uncache
		SpyCache sc=SpyCache.getInstance();
		sc.uncache(CACHE_KEY);
		// This will cause a recache.
		getCacheEntry();
	}

	/**
	 * String me.
	 */
	public String toString() {
		StringBuffer sb=new StringBuffer(64);

		sb.append("{PhotoUser: username=");
		sb.append(username);
		sb.append("}");

		return (sb.toString());
	}

	/** 
	 * Hashcode of this object.
	 * 
	 * @return the hashcode of the username
	 */
	public int hashCode() {
		return(username.hashCode());
	}

	/** 
	 * True if these objects are equal.
	 * 
	 * @param o the other object
	 * @return true if o is a PhotoUser with the same ID
	 */
	public boolean equals(Object o) {
		boolean rv=false;
		if(o instanceof PhotoUser) {
			PhotoUser u=(PhotoUser)o;
			rv= (id == u.id);
		}
		return(rv);
	}

	/** 
	 * Compare these users by username.
	 * 
	 * @param o the object to compare to
	 * @return this.username.compareTo(o.username)
	 */
	public int compareTo(Object o) {
		PhotoUser pu=(PhotoUser)o;
		int rv=username.compareTo(pu.username);
		return(rv);
	}

	/**
	 * Get the username.
	 */
	public String getUsername() {
		return(username);
	}

	/** 
	 * The principal name (username).
	 */
	public String getName() {
		return(getUsername());
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
	public void setPersess(String persess) {
		this.persess=persess;
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

	void addRole(String role) {
		roles.add(role);
	}

	/**
	 * Get a Collection of Strings describing all the roles this user has.
	 */
	public Collection getRoles() {
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
		setModified(true);
	}

	/**
	 * Set the username of this user.
	 */
	public void setUsername(String username) {
		this.username=username.toLowerCase();
		setModified(true);
	}

	/**
	 * Set the real name of this user.
	 */
	public void setRealname(String realname) {
		this.realname=realname;
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
	public void setEmail(String email) {
		this.email=email.toLowerCase();
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
		db.setUsername(username);
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
		}

		setSaved();
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
		setModified(true);
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
		StringBuffer sb=new StringBuffer(128);

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

	private static class CacheEntry extends Object {
		public Map byId=null;
		public Map byUsername=null;
		public Map byEmail=null;
		public Map byPersess=null;

		public CacheEntry() {
			byId=new HashMap();
			byUsername=new HashMap();
			byEmail=new HashMap();
			byPersess=new HashMap();
		}
	}
}
