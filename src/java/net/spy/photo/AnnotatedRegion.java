// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>

package net.spy.photo;

import java.util.Collection;
import java.util.Date;

/**
 * An annotated region.
 */
public interface AnnotatedRegion extends PhotoRegion {

	/** 
	 * Get the ID of this annotated region.
	 */
	int getId();

	/** 
	 * Get the title of this annotated region.
	 */
	String getTitle();

	/** 
	 * Get the keywords associated with this annotated region.
	 */
	Collection<Keyword> getKeywords();

	/** 
	 * Get the user who created this annotation.
	 */
	User getUser();

	/** 
	 * Get the timestamp indicating when this annotation was added.
	 */
	Date getTimestamp();

}
