// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: A5CA3C86-5D6D-11D9-A49F-000A957659CC

package net.spy.photo.rpc;

import java.util.Collection;
import java.util.Vector;

import net.spy.photo.Category;
import net.spy.photo.CategoryFactory;
import net.spy.photo.PhotoException;
import net.spy.photo.User;

/**
 * Get the categories a user has access to.
 */
public class GetCategories extends RPCMethod {

	/**
	 * Get an instance of GetCategories.
	 */
	public GetCategories() {
		super();
	}

	/**
	 * Get the names of the categories to which the user has add access.
	 * 
	 * @param username
	 *            username
	 * @param password
	 *            password
	 * @return Vector of Strings
	 * @throws PhotoException
	 *             if there's trouble getting the cat list
	 */
	public Vector getAddable(String username, String password)
		throws PhotoException {
		// Authenticate the user
		authenticate(username, password);

		Vector<String> rv = new Vector<String>();

		User u = getUser();
		CategoryFactory cf = CategoryFactory.getInstance();
		Collection<Category> cats = cf.getCatList(
			u.getId(), CategoryFactory.ACCESS_WRITE);
		for(Category c : cats) {
			rv.add(c.getName());
		}

		return (rv);
	}
}
