// Copyright (c) 2005  Dustin Sallings
// arch-tag: DD147F28-5FB1-11D9-BD2C-000A957659CC

package net.spy.photo;

import java.sql.ResultSet;

import java.util.Collection;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;

import net.spy.factory.GenFactory;
import net.spy.factory.CacheEntry;
import net.spy.factory.HashCacheEntry;
import net.spy.factory.Instance;

import net.spy.photo.impl.DBUser;

import net.spy.photo.sp.GetAllUsers;
import net.spy.photo.sp.GetAllACLs;
import net.spy.photo.sp.GetAllRoles;

/**
 * Represents a user in the photo system.
 */
public class UserFactory extends GenFactory {

	private static final String CACHE_KEY="net.spy.photo.DBUser";
	private static final int CACHE_TIME=3600000; // one hour

	private static UserFactory instance=null;

	private Comparator userComparator=null;

	/**
	 * Get a new, empty user.
	 */
	private UserFactory() {
		super(CACHE_KEY, CACHE_TIME);
		userComparator=new UserComparator();
	}

	/** 
	 * Get the singleton instance of UserFactory.
	 */
	public static synchronized UserFactory getInstance() {
		if(instance == null) {
			instance=new UserFactory();
		}
		return(instance);
	}

	private void initACLs(Map idMap, DBUser defaultUser) throws Exception {
		GetAllACLs db=new GetAllACLs(PhotoConfig.getInstance());

		ResultSet rs=db.executeQuery();
		while(rs.next()) {
			Integer userId=new Integer(rs.getInt("userid"));
			DBUser pu=(DBUser)idMap.get(userId);
			if(pu == null) {
				throw new PhotoUserException("Invalid user in acl map: "
					+ userId);
			}
			int cat=rs.getInt("cat");
			if(rs.getBoolean("canview")) {
				pu.getACL().addViewEntry(cat);
			}
			if(rs.getBoolean("canadd")) {
				pu.getACL().addAddEntry(cat);
			}
		}
		rs.close();
		db.close();
		// Add all of the permissions of the default user to all other users
		for(Iterator i=idMap.values().iterator(); i.hasNext(); ) {
			DBUser u=(DBUser)i.next();

			for(Iterator i2=u.getACL().iterator(); i2.hasNext(); ) {
				PhotoACLEntry acl=(PhotoACLEntry)i2.next();

				if(acl.canView()) {
					u.getACL().addViewEntry(acl.getWhat());
				}
				if(acl.canAdd()) {
					u.getACL().addAddEntry(acl.getWhat());
				}
			}
		}
	}

	private Collection fetchAllUsers() throws Exception {
		PhotoConfig conf=PhotoConfig.getInstance();
		Map users=new HashMap();
		Map idMap=new HashMap();

		GetAllUsers db=new GetAllUsers(conf);
		ResultSet rs=db.executeQuery();
		while(rs.next()) {
			DBUser pu=new DBUser(rs);

			// Add it to the list so we can initialize the ACLs.
			users.put(pu.getName(), pu);
			idMap.put(new Integer(pu.getId()), pu);
		}
		rs.close();
		db.close();

		// Find the default user so we can initialize the ACLs.
		String defUsername=conf.get("default_user", "guest");
		DBUser defaultUser=(DBUser)users.get(defUsername);
		if(defaultUser==null) {
			throw new PhotoUserException("Default user not found.");
		}
		// Add the ``guest'' role to the default user.
		defaultUser.addRole("guest");

		// Initialize all the ACLs for all the users
		initACLs(idMap, defaultUser);

		GetAllRoles db2=new GetAllRoles(conf);
		rs=db2.executeQuery();
		while(rs.next()) {
			Integer id=new Integer(rs.getInt("userid"));
			String r=rs.getString("groupname");
			DBUser pu=(DBUser)idMap.get(id);
			if(pu == null) {
				throw new PhotoException("Invalid user in role map: " + id);
			}
			pu.addRole(r);
		}
		rs.close();
		db2.close();

		return(users.values());
	}

	protected CacheEntry getNewCacheEntry() {
		return(new UserCacheEntry());
	}

	protected Collection getInstances() {
		Collection rv=null;
		try {
			rv=fetchAllUsers();
		} catch(Exception e) {
			throw new RuntimeException("Could not load users", e);
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
	public User getUser(String spec) throws PhotoUserException {

		if(spec==null) {
			throw new NoSuchPhotoUserException("There is no null user.");
		}

		UserCacheEntry m=(UserCacheEntry)getCache();
		User rv=m.getByUsername(spec.toLowerCase());
		if(rv==null) {
			// If that fails, try it by email address
			rv=m.getByEmail(spec.toLowerCase());
			if(rv == null) {
				throw new NoSuchPhotoUserException("No such user:  " + spec);
			}
		}

		return(rv);
	}

	/** 
	 * Get a user by persistent session ID.
	 */
	public User getUserByPersess(String persess) throws PhotoUserException {

		UserCacheEntry m=(UserCacheEntry)getCache();
		User rv=m.getByPersess(persess);
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
	public User getUser(int id) throws PhotoUserException {
		User rv=(User)getObject(id);
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
	public SortedSet getAllUsers() throws PhotoUserException {
		SortedSet rv=new TreeSet(userComparator);
		rv.addAll(getObjects());
		return(Collections.unmodifiableSortedSet(rv));
	}

	private static class UserComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			User u1=(User)o1;
			User u2=(User)o2;
			int rv=u1.getName().compareTo(u2.getName());
			return(rv);
		}

		public boolean equals(Object o) {
			boolean rv=false;
			if(o == this) {
				rv = true;
			}
			return(rv);
		}
	}

	private static class UserCacheEntry extends HashCacheEntry {
		public Map byUsername=null;
		public Map byEmail=null;
		public Map byPersess=null;

		public UserCacheEntry() {
			super();
			byUsername=new HashMap();
			byEmail=new HashMap();
			byPersess=new HashMap();
		}

		public void cacheInstance(Instance i) {
			super.cacheInstance(i);
			User u=(User)i;
			byUsername.put(u.getName(), u);
			byEmail.put(u.getEmail(), u);
			if(u.getPersess() != null) {
				byPersess.put(u.getPersess(), u);
			}
		}

		public User getByUsername(String username) {
			return( (User)byUsername.get(username));
		}

		public User getByEmail(String email) {
			return( (User)byEmail.get(email));
		}

		public User getByPersess(String persess) {
			return( (User)byPersess.get(persess));
		}
	}
}
