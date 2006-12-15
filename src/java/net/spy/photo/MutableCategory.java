// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>

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
