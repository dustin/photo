package net.spy.photo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.spy.photo.search.ImmutableArraySet;

import junit.framework.TestCase;

/**
 * Test the immutable array set.
 */
public class ImmutableArraySetTest extends TestCase {

	// This does a pretty basic overall test of the immutable array set.
	public void testBasicStuff() {
		Set<String> hs=new HashSet<String>(Arrays.asList("a", "b", "c"));
		Set<String> ias=new ImmutableArraySet<String>(hs);

		assertTrue(ias.containsAll(hs));
		assertTrue(hs.containsAll(ias));

		assertEquals(ias.size(), hs.size());

		assertFalse(ias.isEmpty());
		assertTrue(ias.contains("c"));
		assertFalse(ias.contains("d"));
	}
}
