// Copyright (c) 2005  Dustin Sallings
// arch-tag: DD147F28-5FB1-11D9-BD2C-000A957659CC

package net.spy.photo;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

import net.spy.db.Savable;
import net.spy.db.Saver;
import net.spy.factory.GenFactory;
import net.spy.photo.impl.DBUser;
import net.spy.photo.sp.GetAllACLs;
import net.spy.photo.sp.GetAllRoles;
import net.spy.photo.sp.GetAllUsers;
import net.spy.util.CloseUtil;

/**
 * Instantiate and lookup Users.
 */
public class UserFactory extends GenFactory<User> {

	private static final String CACHE_KEY="net.spy.photo.DBUser";
	private static final int CACHE_TIME=3600000; // one hour

	private static AtomicReference<UserFactory> instanceRef=
		new AtomicReference<UserFactory>(null);

	private Comparator<User> userComparator=null;

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
	public static UserFactory getInstance() {
		UserFactory rv=instanceRef.get();
		if(rv == null) {
			rv=new UserFactory();
			instanceRef.compareAndSet(null, rv);
		}
		return(rv);
	}

	private void initACLs(Map<Integer, User> idMap, User defaultUser)
		throws Exception {

		GetAllACLs db=new GetAllACLs(PhotoConfig.getInstance());
		try {
			ResultSet rs=db.executeQuery();
			while(rs.next()) {
				int userId=rs.getInt("userid");
				User pu=idMap.get(userId);
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
		} finally {
			CloseUtil.close(db);
		}
		// Add all of the permissions of the default user to all other users
		for(User u : idMap.values()) {
			for(Iterator<PhotoACLEntry> i=u.getACL().iterator(); i.hasNext();) {
				PhotoACLEntry acl=i.next();
				if(acl.canView()) {
					u.getACL().addViewEntry(acl.getWhat());
				}
				if(acl.canAdd()) {
					u.getACL().addAddEntry(acl.getWhat());
				}
			}
		}
	}

	private Collection<User> fetchAllUsers() throws Exception {
		PhotoConfig conf=PhotoConfig.getInstance();
		Map<String, User> users=new HashMap<String, User>();
		Map<Integer, User> idMap=new HashMap<Integer, User>();

		GetAllUsers db=new GetAllUsers(conf);
		try {
			ResultSet rs=db.executeQuery();
			while(rs.next()) {
				User pu=new DBUser(rs);
				((DBUser)pu).addRole(User.AUTHENTICATED);

				// Add it to the list so we can initialize the ACLs.
				users.put(pu.getName(), pu);
				idMap.put(pu.getId(), pu);
			}
			rs.close();
		} finally {
			CloseUtil.close(db);
		}

		// Find the default user so we can initialize the ACLs.
		String defUsername=conf.get("default_user", "guest");
		User defaultUser=users.get(defUsername);
		if(defaultUser==null) {
			throw new PhotoUserException("Default user not found.");
		}
		// remove ``authenticated''from the default user
		((DBUser)defaultUser).removeRole(User.AUTHENTICATED);

		// Initialize all the ACLs for all the users
		initACLs(idMap, defaultUser);

		GetAllRoles db2=new GetAllRoles(conf);
		try {
			ResultSet rs=db2.executeQuery();
			while(rs.next()) {
				Integer id=new Integer(rs.getInt("userid"));
				String r=rs.getString("groupname");
				User pu=idMap.get(id);
				if(pu == null) {
					throw new PhotoException("Invalid user in role map: " + id);
				}
				((DBUser)pu).addRole(r);
			}
			rs.close();
		} finally {
			CloseUtil.close(db2);
		}

		return(users.values());
	}

	@Override
	protected Collection<User> getInstances() {
		Collection<User> rv=null;
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

		User rv=getObject(User.BY_USERNAME, spec.toLowerCase());
		if(rv==null) {
			// If that fails, try it by email address
			rv=getObject(User.BY_EMAIL, spec.toLowerCase());
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

		assert persess!=null;

		User rv=getObject(User.BY_PERSESS, persess);
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
		User rv=getObject(id);
		if(rv==null) {
			throw new NoSuchPhotoUserException("No such user (id):  " + id);
		}

		return(rv);
	}

	// Mutable stuff
	public MutableUser getMutable(int id) throws PhotoUserException {
		return((MutableUser)getUser(id));
	}

	public MutableUser createNew() {
		return(new DBUser());
	}

	public void persist(MutableUser inst) throws Exception {
		Saver saver=new Saver(PhotoConfig.getInstance());
		saver.save((Savable)inst);
		recache();
		// Also recache the categories when the user changes
		CategoryFactory.getInstance().recache();
	}

	/** 
	 * Get all known users.
	 * @return an unmodifiable sorted set of users
	 * @throws PhotoUserException 
	 */
	public SortedSet<User> getAllUsers() throws PhotoUserException {
		SortedSet<User> rv=new TreeSet<User>(userComparator);
		rv.addAll(getObjects());
		return(Collections.unmodifiableSortedSet(rv));
	}

	static class UserComparator implements Comparator<User> {
		public int compare(User u1, User u2) {
			int rv=u1.getName().compareTo(u2.getName());
			return(rv);
		}

		@Override
		public boolean equals(Object o) {
			boolean rv=false;
			if(o == this) {
				rv = true;
			}
			return(rv);
		}
	}

}
