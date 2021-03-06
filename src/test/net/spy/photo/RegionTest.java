// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>

package net.spy.photo;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.spy.photo.impl.PhotoRegionImpl;

/**
 * Test the region related code.
 */
public class RegionTest extends TestCase {

	/**
	 * Get an instance of RegionTest.
	 */
	public RegionTest(String name) {
		super(name);
	}

	/** 
	 * Get the test suite.
	 */
	public static Test suite() {
		return new TestSuite(RegionTest.class);
	}

	private void assertNotEquals(Object ob1, Object ob2) {
		assertFalse(ob1 + " should not equal " + ob2,
			ob1.equals(ob2));
	}

	/** 
	 * Test basic equality.
	 */
	public void testEquality() {
		PhotoRegion pr1=new PhotoRegionImpl(5, 5, 70, 70);
		PhotoRegion pr2=new PhotoRegionImpl(5, 5, 70, 70);
		assertEquals(pr1, pr2);
		assertEquals(pr1.hashCode(), pr2.hashCode());
		assertNotEquals(pr1, new PhotoRegionImpl(4, 5, 70, 70));
		assertNotEquals(pr1, new PhotoRegionImpl(5, 4, 70, 70));
		assertNotEquals(pr1, new PhotoRegionImpl(5, 5, 69, 70));
		assertNotEquals(pr1, new PhotoRegionImpl(5, 5, 70, 69));
	}

	/** 
	 * Test region scaling.
	 */
	public void testScaling() {
		PhotoRegion src=new PhotoRegionImpl(50, 50, 50, 50);
		PhotoRegion rslt=PhotoUtil.scaleRegion(src, 1.0f);
		assertEquals(src, rslt);

		rslt=PhotoUtil.scaleRegion(src, 0.5f);
		assertEquals(new PhotoRegionImpl(25, 25, 25, 25), rslt);

		rslt=PhotoUtil.scaleRegion(src, 2.0f);
		assertEquals(new PhotoRegionImpl(100, 100, 100, 100), rslt);
	}

}
