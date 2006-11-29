// arch-tag: 39B92AF7-0B85-40C8-8D60-9626371CDB6A

package net.spy.photo;

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
	String getKeyword();

}
