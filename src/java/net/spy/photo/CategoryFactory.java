// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>
// arch-tag: 38AE1F10-5EE7-11D9-B828-000A957659CC

package net.spy.photo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

import net.spy.db.DBSPLike;
import net.spy.db.Savable;
import net.spy.db.Saver;
import net.spy.factory.GenFactory;
import net.spy.photo.impl.DBCategory;
import net.spy.photo.sp.GetAllACLs;
import net.spy.photo.sp.GetAllCategories;
import net.spy.util.CloseUtil;

/**
 * Category access.
 */
public class CategoryFactory extends GenFactory<Category> {

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

	private static AtomicReference<CategoryFactory> instanceRef=
		new AtomicReference<CategoryFactory>(null);

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
	public static CategoryFactory getInstance() {
		CategoryFactory rv=instanceRef.get();
		if(rv == null) {
			rv=new CategoryFactory();
			instanceRef.compareAndSet(null, rv);
		}
		return(rv);
	}

	protected Collection<Category> getInstances() {
		Map<Integer, Category> rv=new HashMap<Integer, Category>();

		GetAllCategories db=null;
		try {
			db=new GetAllCategories(PhotoConfig.getInstance());

			ResultSet rs=db.executeQuery();
			while(rs.next()) {
				Category cat=new DBCategory(rs);
				rv.put(cat.getId(), cat);
			}

			CloseUtil.close((DBSPLike)db);
			db=null;

			// Load all of the ACLs for the categories
			loadACLs(rv);

		} catch(SQLException e) {
			throw new RuntimeException("Could not load categories", e);
		} finally {
			CloseUtil.close((DBSPLike)db);
		}

		return(rv.values());
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
		return(new DBCategory(getObject(id)));
	}

	/** 
	 * Persist a mutable category.
	 */
	public void persist(MutableCategory inst) throws Exception {
		Saver saver=new Saver(PhotoConfig.getInstance());
		saver.save((Savable)inst);
		recache();
		// Also recache the users
		UserFactory.getInstance().recache();
	}

	/** 
	 * Throw a RuntimeException if an invalid ID is called.
	 */
	protected Category handleNullLookup(int id) {
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
		for(Category cat : getObjects()) {
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
	private void loadACLs(Map<Integer, Category> cats) throws SQLException {

		GetAllACLs db=new GetAllACLs(PhotoConfig.getInstance());

		try {
			ResultSet rs=db.executeQuery();
			while(rs.next()) {
				Integer catId=new Integer(rs.getInt("cat"));

				Category cat=cats.get(catId);
				if(cat == null) {
					throw new RuntimeException(
							"Invalid cat acl mapping " + catId);
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
		} finally {
			CloseUtil.close((DBSPLike)db);
		}
	}

	private SortedSet<Category> getInternalCatList(int uid, int access)
		throws PhotoException {

		boolean seeksRead=((access&ACCESS_READ)>0);
		boolean seeksWrite=((access&ACCESS_WRITE)>0);

		// Make sure some access method is given.
		if(access==0) {
			throw new PhotoException("No access method given.");
		}

		SortedSet<Category> rv=getAdminCatList();

		for(Iterator<Category> i=rv.iterator(); i.hasNext();) {
			Category cat=i.next();

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
	public SortedSet<Category> getCatList(int uid, int access)
		throws PhotoException {

		// The set for this user
		SortedSet<Category> baseSet=getInternalCatList(uid, access);
		// The set for the default user
		SortedSet<Category> defSet=getInternalCatList(
			PhotoUtil.getDefaultId(), access);

		// Add all of the default set to the base set.
		baseSet.addAll(defSet);

		return(baseSet);
	} // getCatList(int,int)

	/**
	 * Get a Collection of all categories (for administrative actions).
	 * @return a new SortedSet of Categories.
	 * @throws PhotoException if it can't find the categories
	 */
	public SortedSet<Category> getAdminCatList() throws PhotoException {
		SortedSet<Category> rv=null;
		try {
			rv=new TreeSet<Category>(comparator);
			rv.addAll(getObjects());
		} catch(Exception e) {
			throw new PhotoException("Error getting admin category list", e);
		}
		return(rv);
	}

	private static class CategoryComparator implements Comparator<Category> {
		public int compare(Category cat1, Category cat2) {
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
