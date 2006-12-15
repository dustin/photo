
package net.spy.photo;

import net.spy.factory.CacheKey;

/**
 * Keyword interface.
 */
public interface Keyword extends Instance {

	/**
	 * Cache key by word.
	 */
	public static final String BYWORD="word";

	/**
	 * Get the keyword value.
	 */
	@CacheKey(name=BYWORD)
	String getKeyword();

}
