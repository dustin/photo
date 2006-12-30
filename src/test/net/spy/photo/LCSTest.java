package net.spy.photo;

import net.spy.photo.util.LCS;

import junit.framework.TestCase;

/**
 * Test the LCS implementation.
 */
public class LCSTest extends TestCase {

	private LCS lcs=LCS.getInstance();

	public void testLCS() {
		assertEquals("pub", lcs.lcs("public", "supubr"));
		assertEquals("", lcs.lcs("pub", "sec"));
		assertEquals("pub", lcs.lcs("notpublic", "xpyuzb"));
	}
}
