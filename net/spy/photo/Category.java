// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: Category.java,v 1.1 2002/05/11 09:24:34 dustin Exp $

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

	/**
	 * Get an instance of Category.
	 */
	public Category() {
		super();
	}

	private Category(ResultSet rs) throws SQLException {
		id=rs.getInt("id");
		name=rs.getString("name");
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
