// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: Category.java,v 1.4 2002/06/25 00:18:01 dustin Exp $

package net.spy.photo;

import java.sql.*;
import java.util.*;

import net.spy.*;
import net.spy.db.*;

/**
 * Category representation.
 */
public class Category extends Object {

	/**
	 * Flag to list categories that can be read by the user.
	 */
	public static final int ACCESS_READ=1;
	/**
	 * Flag to list categories to which the user may add.
	 */
	public static final int ACCESS_WRITE=2;

	private int id=-1;
	private String name=null;

	private Vector acl=null;

	/**
	 * Get an instance of Category.
	 */
	public Category() {
		super();
		acl=new Vector();
	}

	private Category(ResultSet rs) throws SQLException {
		id=rs.getInt("id");
		name=rs.getString("name");
	}

	/**
	 * Lookup a category by integer ID.
	 */
	public static Category lookupCategory(int catId) throws Exception {
		Category rv=null;

		SpyCacheDB db=new SpyCacheDB(new PhotoConfig());
		ResultSet rs=db.executeQuery("select * from cat where id="
			+ catId, 3600);
		if(!rs.next()) {
			throw new Exception("No such category:  " + catId);
		}
		rv=new Category(rs);
		rs.close();
		db.close();

		return(rv);
	}

	/**
	 * Look up a category by name.
	 */
	public static Category lookupCategory(String catName) throws Exception {
		Category rv=null;

		SpyCacheDB db=new SpyCacheDB(new PhotoConfig());
		PreparedStatement pst=db.prepareStatement(
			"select * from cat where name=?", 3600);
		pst.setString(1, catName);
		ResultSet rs=pst.executeQuery();
		if(!rs.next()) {
			throw new Exception("No such category:  " + catName);
		}
		rv=new Category(rs);
		if(rs.next()) {
			throw new Exception("Too many matches for category " + catName);
		}
		rs.close();
		db.close();
		return(rv);
	}

	/**
	 * Load the ACLs for this Category instance.
	 */
	public void loadACLs() throws Exception {
		
		SpyDB db=new SpyDB(new PhotoConfig());
		PreparedStatement pst=db.prepareStatement(
			"select distinct userid, canview, canadd\n"
				+ " from wwwacl where cat=?");
		pst.setInt(1, id);
		ResultSet rs=pst.executeQuery();

		// Add the ACL entries
		acl=new Vector();
		while(rs.next()) {
			int uid=rs.getInt("userid");
			if(rs.getBoolean("canview")) {
				addViewACLEntry(uid);
			}
			if(rs.getBoolean("canadd")) {
				addAddACLEntry(uid);
			}
		}
		rs.close();
		pst.close();
		db.close();
	}

	/**
	 * Save this category and ACL entries.
	 */
	public void save() throws Exception {
		SpyDB db=new SpyDB(new PhotoConfig());
		Connection conn=null;
		try {
			conn=db.getConn();
			conn.setAutoCommit(false);
			PreparedStatement pst=null;

			// What to do here depends on whether it's a new category or a
			// modification of an existing category.
			if(id>=0) {
				// Existing user
				pst=conn.prepareStatement("update cat set name=? where id=?");
				pst.setInt(2, id);
			} else {
				pst=conn.prepareStatement("insert into cat(name) values(?)");
			}

			// Set the common fields
			pst.setString(1, name);
			// Save the category proper
			pst.executeUpdate();
			pst.close();

			// If'n it's a new category, let's look up the ACL we just saved.
			if(id==-1) {
				Statement st=conn.createStatement();
				ResultSet rs=st.executeQuery(
					"select currval('cat_id_seq')");
				if(!rs.next()) {
					throw new PhotoException(
						"No results returned while looking up new cat id");
				}
				id=rs.getInt(1);
				rs.close();
				st.close();
				System.err.println("New category:  " + id);
			}

			// OK, now deal with the ACLs

			// Out with the old
			pst=conn.prepareStatement("delete from wwwacl where cat=?");
			pst.setInt(1, id);
			pst.executeUpdate();
			pst.close();

			// In with the new

			pst=conn.prepareStatement(
				"insert into wwwacl(userid, cat, canview, canadd) "
					+ "values(?,?,?,?)");

			for(Enumeration e=getACLEntries(); e.hasMoreElements(); ) {
				PhotoACLEntry aclEntry=(PhotoACLEntry)e.nextElement();

				pst.setInt(1, aclEntry.getUid());
				pst.setInt(2, id);
				pst.setBoolean(3, aclEntry.canView());
				pst.setBoolean(4, aclEntry.canAdd());
				pst.executeUpdate();
			}
			pst.close();
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
			// Tell it we're done
			db.close();
		}
	}

	/**
	 * Get the ACL entries for this category.
	 */
	public Enumeration getACLEntries() {
		return(acl.elements());
	}

	/**
	 * Get an ACL entry for the given user.
	 *
	 * @param userid the numeric user ID of the user to look up
	 *
	 * @return the entry, or null if there's no entry for the given user
	 */
	public PhotoACLEntry getACLEntryForUser(int userid) {
		PhotoACLEntry rv=null;

		for(Enumeration e=getACLEntries(); rv==null && e.hasMoreElements();) {
			PhotoACLEntry acl=(PhotoACLEntry)e.nextElement();

			if(acl.getUid() == userid) {
				rv=acl;
			}
		}

		return(rv);
	}

	// Same as above, but create a new entry if there isn't one.
	private PhotoACLEntry getACLEntryForUser2(int userid) {
		PhotoACLEntry rv=getACLEntryForUser(userid);
		if(rv==null) {
			rv=new PhotoACLEntry(userid, getId());
			acl.addElement(rv);
		}
		return(rv);
	}

	/**
	 * Add an ACL entry permitting the given user ID to view this
	 * category.
	 */
	public void addViewACLEntry(int userid) {
		PhotoACLEntry aclEntry=getACLEntryForUser2(userid);
		aclEntry.setCanView(true);
	}

	/**
	 * Add an ACL entry permitting the given user ID to add to this
	 * category.
	 */
	public void addAddACLEntry(int userid) {
		PhotoACLEntry aclEntry=getACLEntryForUser2(userid);
		aclEntry.setCanAdd(true);
	}

	/**
	 * Remove an entry for a given user.
	 */
	public void removeACLEntry(int userid) {
		PhotoACLEntry entry=getACLEntryForUser(userid);
		if(entry!=null) {
			acl.remove(entry);
		}
	}

	/**
	 * Remove all ACL entries.
	 */
	public void removeAllACLEntries() {
		acl.clear();
	}

	/**
	 * Get a category list.
	 *
	 * @param uid The numeric UID of the user.
	 * @param access A bitmask describing the access required for the
	 * search (ord together).
	 */
	public static Collection getCatList(int uid, int access)
		throws PhotoException {

		Vector v=new Vector();

		if( ((access&ACCESS_READ)>0) && ((access&ACCESS_WRITE)>0) ) {
			throw new PhotoException(
				"Cannot combine read and write access yet.");
		}

		try {
			String op=null;
			if( (access&ACCESS_READ)>0) {
				op="canview";
			} else if((access&ACCESS_WRITE)>0) {
				op="canadd";
			} else {
				throw new PhotoException("No access method given.");
			}

			SpyCacheDB db=new SpyCacheDB(new PhotoConfig());

			StringBuffer query=new StringBuffer();
			query.append("select * from cat where id in\n");
			query.append("(select cat from wwwacl where\n");
			query.append("    (userid=? or userid=?) ");
			query.append("     and ");
			query.append(op);
			query.append("=true)\n");
			query.append("order by name");

			PreparedStatement pst=db.prepareStatement(query.toString(), 300);
			pst.setInt(1, uid);
			pst.setInt(2, PhotoUtil.getDefaultId());

			ResultSet rs=pst.executeQuery();

			while(rs.next()) {
				v.addElement(new Category(rs));
			}
			rs.close();
			pst.close();
			db.close();
		} catch(SQLException se) {
			throw new PhotoException("Error getting category list", se);
		}

		return(v);
	}

	/**
	 * Get a list of all categories (for administrative actions).
	 */
	public static Enumeration getAdminCatList() throws PhotoException {
		Vector v=new Vector();
		try {
			SpyDB db=new SpyDB(new PhotoConfig());
			ResultSet rs=db.executeQuery("select * from cat order by name");
			while(rs.next()) {
				v.addElement(new Category(rs));
			}
		} catch(Exception e) {
			throw new PhotoException("Error getting admin category list", e);
		}
		return(v.elements());
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
	}

	/**
	 * String me.
	 */
	public String toString() {
		return(name + " (" + id + ")");
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
