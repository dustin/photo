// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>

package net.spy.photo.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.spy.db.AbstractSavable;
import net.spy.db.SaveContext;
import net.spy.db.SaveException;
import net.spy.photo.Category;
import net.spy.photo.MutableCategory;
import net.spy.photo.PhotoACL;
import net.spy.photo.PhotoACLEntry;
import net.spy.photo.sp.DeleteACLForCat;
import net.spy.photo.sp.GetGeneratedKey;
import net.spy.photo.sp.InsertACLEntry;
import net.spy.photo.sp.InsertCategory;
import net.spy.photo.sp.ModifyCategory;
import net.spy.photo.sp.UpdateCategory;

/**
 * Database category representation.
 */
public class DBCategory extends AbstractSavable implements MutableCategory {

	private int id=-1;
	private String name=null;

	private PhotoACL acl=null;

	/**
	 * Get a new empty dbcategory.
	 */
	public DBCategory() {
		super();
		acl=new PhotoACL();
	}

	/** 
	 * Get a DBCategory that is a copy of the given Category.
	 */
	public DBCategory(Category cat) {
		super();
		this.id=cat.getId();
		this.name=cat.getName();
		this.acl=cat.getACL().copy();
		if(id > 0) {
			setNew(false);
		} else {
			setNew(true);
		}
		setModified(false);
	}

	/** 
	 * Get an instance of DBCategory from a ResultSet row.
	 */
	public DBCategory(ResultSet rs) throws SQLException {
		this();
		id=rs.getInt("id");
		name=rs.getString("name");
		setNew(false);
		setModified(false);
	}

	/** 
	 * Return true if the object passed as an argument is a Category object
	 * with the same ID.
	 */
	@Override
	public boolean equals(Object o) {
		boolean rv=false;
		if(o instanceof Category) {
			Category cat=(Category)o;
			rv=id == cat.getId();
		}
		return (rv);
	}

	/** 
	 * Get the hash code.
	 * 
	 * @return the ID of this category
	 */
	@Override
	public int hashCode() {
		return (getId());
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
			getLogger().info("Creating a new category:  " + this);
			saveCat=new InsertCategory(conn);
		} else {
			// Existing category
			getLogger().info("Modifying an existing category:  " + this);
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

		for(PhotoACLEntry aclEntry : acl) {
			getLogger().info("Mapping " + aclEntry + " to cat " + id);

			iacl.setUserId(aclEntry.getWhat());
			iacl.setCanView(aclEntry.canView());
			iacl.setCanAdd(aclEntry.canAdd());
			iacl.executeUpdate();
		}
		iacl.close();
		iacl=null;
	}

	// End savable implementation

	/**
	 * Get the ACL entries for this category.
	 */
	public PhotoACL getACL() {
		return(acl);
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
	@Override
	public String toString() {
		StringBuffer sb=new StringBuffer(64);

		sb.append("{DBCategory name=");
		sb.append(name);
		sb.append(", id=");
		sb.append(id);
		sb.append("}");

		return (sb.toString());
	}

}
