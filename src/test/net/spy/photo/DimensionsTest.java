// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>
// arch-tag: 1E5424E3-8B99-490C-86BF-177E2185CE4F

package net.spy.photo;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import net.spy.photo.impl.PhotoDimensionsImpl;

/**
 * Test the PhotoDimensions related code.
 */
public class DimensionsTest extends TestCase {

	/**
	 * Get an instance of DimensionsTest.
	 */
	public DimensionsTest(String name) {
		super(name);
	}

	/** 
	 * Get the test suite.
	 */
	public static Test suite() {
		return new TestSuite(DimensionsTest.class);
	}

	/** 
	 * Test the PhotoDimensionsImpl constructor for normal cases.
	 */
	public void testImplConstructor() {
		PhotoDimensions dim=new PhotoDimensionsImpl("800x600");
		assertEquals(800, dim.getWidth());
		assertEquals(600, dim.getHeight());

		dim=new PhotoDimensionsImpl("600x800");
		assertEquals(600, dim.getWidth());
		assertEquals(800, dim.getHeight());

		dim=new PhotoDimensionsImpl("0x0");
		assertEquals(0, dim.getWidth());
		assertEquals(0, dim.getHeight());

		dim=new PhotoDimensionsImpl(600, 800);
		assertEquals(600, dim.getWidth());
		assertEquals(800, dim.getHeight());
	}

	/** 
	 * Test the equals implementation on PhotoDimensionsImpl.
	 */
	public void testImplEquals() {
		PhotoDimensions dim1=new PhotoDimensionsImpl("800x600");
		PhotoDimensions dim2=new PhotoDimensionsImpl("800x600");
		assertEquals(dim1, dim2);
		assertNotSame(dim1, dim2);
		PhotoDimensions dim3=new PhotoDimensionsImpl("600x800");
		assertFalse(dim1 + " should not = " + dim2, dim1.equals(dim3));
		assertFalse(dim2 + " should not = " + dim3, dim2.equals(dim3));
		PhotoDimensions dim4=new PhotoDimensionsImpl(600, 800);
		assertEquals(dim3, dim4);
		assertEquals(dim4, dim3);
	}

	private void constructNegTest(String arg) {
		try {
			PhotoDimensions dim=new PhotoDimensionsImpl(arg);
			fail("Constructed " + dim + " from " + arg);
		} catch(IllegalArgumentException e) {
			/* Pass */
		}
	}

	/** 
	 * Test some invalid constructors.
	 */
	public void testImplConstructorInvalid() {
		constructNegTest("800");
		constructNegTest("800x600x42");
		constructNegTest("eighthundredxsevenhundred");

		constructNegTest("-800x-600");
		constructNegTest("-800x600");
		constructNegTest("800x-600");
	}

	/** 
	 * Test the PhotoDimUtil scaler implementation.
	 */
	public void testDimScaler() {
		PhotoDimensions scaleFrom=new PhotoDimensionsImpl("1280x832");
		PhotoDimensions scaleTo=new PhotoDimensionsImpl("800x600");
		PhotoDimensions expected=new PhotoDimensionsImpl("800x520");
		assertEquals(expected, PhotoDimUtil.scaleTo(scaleFrom, scaleTo));

		scaleFrom=new PhotoDimensionsImpl("832x1328");
		scaleTo=new PhotoDimensionsImpl("800x600");
		expected=new PhotoDimensionsImpl("375x600");
		assertEquals(expected, PhotoDimUtil.scaleTo(scaleFrom, scaleTo));
	}

}
