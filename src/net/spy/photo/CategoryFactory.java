// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>
// arch-tag: 38AE1F10-5EE7-11D9-B828-000A957659CC

package net.spy.photo;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Comparator;

import net.spy.db.Saver;
import net.spy.db.Savable;
import net.spy.factory.GenFactory;

import net.spy.photo.impl.DBCategory;
import net.spy.photo.sp.GetAllCategories;
import net.spy.photo.sp.GetAllACLs;

/**
 * Category access.
 */
public class CategoryFactory extends GenFactory {

	/**
	 * Flag to list categories that can be read by the user.
	 */
	public static final int ACCESS_READ=1;
	/**
	 * Flag to list categories to which the user may add.
	 */
	public static final int ACCESS_WRITE=2;

	private static final String CACHE_KEY="photo_cat_map";
	private static final long CACHE_TIME=86400000;

	private static CategoryFactory instance=null;

	private CategoryComparator comparator=null;

	/**
	 * Get an instance of Category.
	 */
	private CategoryFactory() {
		super(CACHE_KEY, CACHE_TIME);
		comparator=new CategoryComparator();
	}

	/** 
	 * Get the singleton instance of CategoryFactory.
	 */
	public static synchronized CategoryFactory getInstance() {
		if(instance == null) {
			instance=new CategoryFactory();
		}
		return(instance);
	}

	protected Collection getInstances() {
		Map rv=new HashMap();

		try {
			GetAllCategories db=new GetAllCategories(PhotoConfig.getInstance());

			ResultSet rs=db.executeQuery();
			while(rs.next()) {
				Category cat=new DBCategory(rs);
				rv.put(new Integer(cat.getId()), cat);
			}

			db.close();

			// Load all of the ACLs for the categories
			loadACLs(rv);

		} catch(SQLException e) {
			throw new RuntimeException("Could not load categories", e);
		}

		return(rv.values());
	}

	/** 
	 * Get a Category by ID.
	 */
	public Category getCategory(int id) {
		return( (Category)getObject(id));
	}

	/** 
	 * Create a new mutable category.
	 */
	public MutableCategory createNew() {
		return(new DBCategory());
	}

	/** 
	 * Get a mutable version of a given category.
	 */
	public MutableCategory getMutable(int id) {
		return(new DBCategory(getCategory(id)));
	}

	/** 
	 * Persist a mutable category.
	 */
	public void persist(MutableCategory inst) throws Exception {
		Saver saver=new Saver(PhotoConfig.getInstance());
		saver.save((Savable)inst);
		recache();
	}

	/** 
	 * Throw a RuntimeException if an invalid ID is called.
	 */
	protected Object handleNullLookup(int id) {
		throw new RuntimeException("No such category id:  " + id);
	}

	/** 
	 * Get a category by name.
	 */
	public Category getCategory(String name) {
		if(name == null) {
			throw new NullPointerException("Category name may not be null");
		}
		Category rv=null;
		for(Iterator i=getObjects().iterator(); i.hasNext() && rv == null; ) {
			Category cat=(Category)i.next();
			if(name.equals(cat.getName())) {
				rv=cat;
			}
		}
		if(rv == null) { 
			throw new RuntimeException("No such category:  " + name);
		}
		return(rv);
	}

	/**
	 * Load the ACLs for this Category instance.
	 */
	private void loadACLs(Map cats) throws SQLException {

		GetAllACLs db=new GetAllACLs(PhotoConfig.getInstance());

		ResultSet rs=db.executeQuery();
		while(rs.next()) {
			Integer catId=new Integer(rs.getInt("cat"));

			Category cat=(Category)cats.get(catId);
			if(cat == null) {
				throw new RuntimeException("Invalid cat acl mapping " + catId);
			}

			int uid=rs.getInt("userid");
			if(rs.getBoolean("canview")) {
				cat.getACL().addViewEntry(uid);
			}
			if(rs.getBoolean("canadd")) {
				cat.getACL().addAddEntry(uid);
			}
		}
		rs.close();
		db.close();
	}

	private SortedSet getInternalCatList(int uid, int access)
		throws PhotoException {

		boolean seeksRead=((access&ACCESS_READ)>0);
		boolean seeksWrite=((access&ACCESS_WRITE)>0);

		// Make sure some access method is given.
		if(access==0) {
			throw new PhotoException("No access method given.");
		}

		SortedSet rv=getAdminCatList();

		for(Iterator i=rv.iterator(); i.hasNext();) {
			Category cat=(Category)i.next();

			PhotoACLEntry aclE=cat.getACL().getEntry(uid);

			boolean keep=false;
			// If we got an entry, see if it works for us.
			if(aclE != null) {
				if(seeksRead && aclE.canView()) {
					keep=true;
				}
				if(seeksWrite && aclE.canAdd()) {
					keep=true;
				}
			}

			if(!keep) {
				i.remove();
			}
		}

		return(rv);
	}

	/**
	 * Get a category list.
	 *
	 * @param uid The numeric UID of the user.
	 * @param access A bitmask describing the access required for the
	 * search (ord together).
	 */
	public SortedSet getCatList(int uid, int access)
		throws PhotoException {

		// The set for this user
		SortedSet baseSet=getInternalCatList(uid, access);
		// The set for the default user
		SortedSet defSet=getInternalCatList(PhotoUtil.getDefaultId(), access);

		// Add all of the default set to the base set.
		baseSet.addAll(defSet);

		return(baseSet);
	} // getCatList(int,int)

	/**
	 * Get a Collection of all categories (for administrative actions).
	 * @return a new SortedSet of Categories.
	 * @throws PhotoException if it can't find the categories
	 */
	public SortedSet getAdminCatList() throws PhotoException {
		SortedSet rv=null;
		try {
			rv=new TreeSet(comparator);
			rv.addAll(getObjects());
		} catch(Exception e) {
			throw new PhotoException("Error getting admin category list", e);
		}
		return(rv);
	}

	private static class CategoryComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			Category cat1=(Category)o1;
			Category cat2=(Category)o2;
			int rv=cat1.getName().compareTo(cat2.getName());
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

}
