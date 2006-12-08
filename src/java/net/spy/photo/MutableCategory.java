// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>
// arch-tag: 4CBAA16B-6084-11D9-A621-000A957659CC

package net.spy.photo;

import net.spy.db.Savable;

/**
 * Mutable category.
 */
public interface MutableCategory extends Mutable, Category, Savable {

	/** 
	 * Set the name of this category.
	 */
	void setName(String to);

}
