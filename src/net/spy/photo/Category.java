// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// arch-tag: C2FF0E34-5D6C-11D9-9841-000A957659CC

package net.spy.photo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import net.spy.SpyObject;
import net.spy.db.SpyDB;
import net.spy.db.DBSP;
import net.spy.db.AbstractSavable;
import net.spy.db.SaveContext;
import net.spy.db.SaveException;
import net.spy.cache.SpyCache;

import net.spy.photo.sp.GetAllCategories;
import net.spy.photo.sp.DeleteACLForCat;
import net.spy.photo.sp.InsertACLEntry;
import net.spy.photo.sp.InsertCategory;
import net.spy.photo.sp.UpdateCategory;
import net.spy.photo.sp.ModifyCategory;
import net.spy.photo.sp.GetAllACLs;
import net.spy.photo.sp.GetGeneratedKey;

/**
 * Category representation.
 */
public class Category extends AbstractSavable implements Comparable {

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

	private int id=-1;
	private String name=null;

	private PhotoACL acl=null;

	/**
	 * Get an instance of Category.
	 */
	public Category() {
		super();
		acl=new PhotoACL();
	}

	private Category(ResultSet rs) throws SQLException {
		this();
		id=rs.getInt("id");
		name=rs.getString("name");
		setNew(false);
		setModified(false);
	}

	private static Map getCategoryMap() throws SQLException {
		Map rv=null;

		SpyCache sc=SpyCache.getInstance();

		rv=(Map)sc.get(CACHE_KEY);
		if (rv==null) {
			rv=new HashMap();

			GetAllCategories db=new GetAllCategories(PhotoConfig.getInstance());

			ResultSet rs=db.executeQuery();
			while(rs.next()) {
				Category cat=new Category(rs);

				// Map it by name and id
				rv.put(cat.getName(), cat);
				rv.put(new Integer(cat.getId()), cat);
			}

			db.close();

			// Load all of the ACLs for the categories
			loadACLs(rv);

			// remember it
			sc.store(CACHE_KEY, rv, CACHE_TIME);
		}

		return (rv);
	}

	/**
	 * Lookup a category by integer ID.
	 */
	public static Category lookupCategory(int catId) throws Exception {
		Map catMap=getCategoryMap();

		Category rv=(Category)catMap.get(new Integer(catId));

		if(rv == null) {
			throw new Exception("No such category:  " + catId);
		}

		return(rv);
	}

	/**
	 * Look up a category by name.
	 */
	public static Category lookupCategory(String catName) throws Exception {
		Map catMap=getCategoryMap();

		Category rv=(Category)catMap.get(catName);

		if(rv == null) {
			throw new Exception("No such category:  " + catName);
		}

		return(rv);
	}

	/** 
	 * Recache the categories.
	 */
	public static void recache() throws Exception {
		// Uncache the current map
		SpyCache sc=SpyCache.getInstance();
		sc.uncache(CACHE_KEY);
		// This will cause a recache
		getCategoryMap();
	}

	/** 
	 * Compare categories by name.
	 */
	public int compareTo(Object o) {
		Category cat=(Category)o;

		String thisName=getName().toLowerCase();
		String thatName=cat.getName().toLowerCase();

		return (thisName.compareTo(thatName));
	}

	/** 
	 * True if the categories have the same name.
	 */
	public boolean equals(Object o) {
		boolean rv=false;

		try {
			rv=compareTo(o) == 0;
		} catch(ClassCastException cce) {
			// Remain false
		}

		return (rv);
	}

	/** 
	 * Get the hash code.
	 * 
	 * @return the ID of this category
	 */
	public int hashCode() {
		return (getId());
	}

	// Overwrite the ACL entry
	private void setAcl(PhotoACL to) {
		acl=to;
		setModified(true);
	}

	/**
	 * Load the ACLs for this Category instance.
	 */
	private static void loadACLs(Map cats) throws SQLException {

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
				cat.acl.addViewEntry(uid);
			}
			if(rs.getBoolean("canadd")) {
				cat.acl.addAddEntry(uid);
			}
		}
		rs.close();
		db.close();
	}

	// Savable implementation

	/**
	 * Save this category and ACL entries.
	 */
	public void save(Connection conn, SaveContext context)
		throws SaveException, SQLException {
		ModifyCategory saveCat=null;

		// What to do here depends on whether it's a new category or a
		// modification of an existing category.
		if(isNew()) {
			saveCat=new InsertCategory(conn);
		} else {
			// Existing user
			saveCat=new UpdateCategory(conn);
			((UpdateCategory)saveCat).setId(id);
		}

		// Set the common fields
		saveCat.setName(name);
		// Save the category proper
		saveCat.executeUpdate();
		saveCat.close();
		saveCat=null;

		// If'n it's a new category, let's look up the ACL we just saved.
		if(id==-1) {
			GetGeneratedKey ggk=new GetGeneratedKey(conn);
			ggk.setSeq("cat_id_seq");
			ResultSet rs=ggk.executeQuery();
			if(!rs.next()) {
				throw new SaveException(
					"No results returned while looking up new cat id");
			}
			id=rs.getInt(1);
			rs.close();
			ggk.close();
			getLogger().info("New category:  " + id);
		}

		// OK, now deal with the ACLs

		// Out with the old
		DeleteACLForCat dacl=new DeleteACLForCat(conn);
		dacl.setCat(id);
		dacl.executeUpdate();
		dacl.close();
		dacl=null;

		// In with the new

		InsertACLEntry iacl=new InsertACLEntry(conn);
		iacl.setCatId(id);

		for(Iterator i=acl.iterator(); i.hasNext(); ) {
			PhotoACLEntry aclEntry=(PhotoACLEntry)i.next();

			iacl.setUserId(aclEntry.getWhat());
			iacl.setCanView(aclEntry.canView());
			iacl.setCanAdd(aclEntry.canAdd());
			iacl.executeUpdate();
		}
		iacl.close();
		iacl=null;

		setSaved();
	}

	// End savable implementation

	/**
	 * Get the ACL entries for this category.
	 */
	public PhotoACL getACL() {
		return(acl);
	}

	private static SortedSet getInternalCatList(int uid, int access)
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

			PhotoACLEntry aclE=cat.acl.getEntry(uid);

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
	public static SortedSet getCatList(int uid, int access)
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
	public static SortedSet getAdminCatList() throws PhotoException {
		SortedSet rv=null;
		try {
			Map catMap=getCategoryMap();
			rv=new TreeSet();
			rv.addAll(catMap.values());
		} catch(Exception e) {
			throw new PhotoException("Error getting admin category list", e);
		}
		return(rv);
	}

	/**
	 * Get the ID of this category.
	 */
	public int getId() {
		return(id);
	}

	/**
	 * Get the name of this category.
	 */
	public String getName() {
		return(name);
	}

	/**
	 * Set the name of this category.
	 */
	public void setName(String to) {
		this.name=to;
		setModified(true);
	}

	/**
	 * String me.
	 */
	public String toString() {
		StringBuffer sb=new StringBuffer(64);

		sb.append("{Category name=");
		sb.append(name);
		sb.append(", id=");
		sb.append(id);
		sb.append("}");

		return (sb.toString());
	}

	/**
	 * Testing and what not.
	 */
	public static void main(String args[]) throws Exception {
		Collection c=getCatList(Integer.parseInt(args[0]), ACCESS_READ);
		for(Iterator i=c.iterator(); i.hasNext(); ) {
			Category cat=(Category)i.next();
			System.out.println(cat);
		}
	}

}
