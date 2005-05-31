// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>
// arch-tag: 1633E05A-B8FE-4216-83B3-B86A3C63F58F

package net.spy.photo;

import java.util.Collection;

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

}
