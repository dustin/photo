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
		assertEquals(dim1.hashCode(), dim2.hashCode());
		assertNotSame(dim1, dim2);
		PhotoDimensions dim3=new PhotoDimensionsImpl("600x800");
		assertFalse(dim1 + " should not = " + dim2, dim1.equals(dim3));
		assertFalse(dim2 + " should not = " + dim3, dim2.equals(dim3));
		assertFalse(dim2 + ".hashCode() should not = " + dim3 + ".hashCode()",
			(dim2.hashCode() == dim3.hashCode()));
		PhotoDimensions dim4=new PhotoDimensionsImpl(600, 800);
		assertEquals(dim3, dim4);
		assertEquals(dim4, dim3);

		PhotoDimensions dim5=new PhotoDimensionsImpl(800, 601);
		assertFalse(dim1 + " should not = " + dim5, dim1.equals(dim5));
		assertFalse(dim1 + " should not = " + dim5, dim2.equals(dim5));
		assertFalse(dim1 + ".hashCode() should not = " + dim5 + ".hashCode()",
			(dim1.hashCode() == dim5.hashCode()));

		PhotoDimensions dim6=new PhotoDimensionsImpl(801, 600);
		assertFalse(dim1 + " should not = " + dim6, dim1.equals(dim6));
		assertFalse(dim1 + " should not = " + dim6, dim2.equals(dim6));
		assertFalse(dim1 + ".hashCode() should not = " + dim6 + ".hashCode()",
			(dim1.hashCode() == dim6.hashCode()));
	}

	private void constructNegTest(String arg) {
		try {
			PhotoDimensions dim=new PhotoDimensionsImpl(arg);
			fail("Constructed " + dim + " from " + arg);
		} catch(IllegalArgumentException e) {
			/* Pass */
		}
	}

	private void constructNegTest(int w, int h) {
		try {
			PhotoDimensions dim=new PhotoDimensionsImpl(w, h);
			fail("Constructed " + dim + " from " + w + "x" + h);
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

		constructNegTest(-800, -600);
		constructNegTest(-800, 600);
		constructNegTest(800, -600);
	}

	/** 
	 * Test the dim scale factor code.
	 */
	public void testDimScaleFactor() {
		PhotoDimensions scaleFrom=new PhotoDimensionsImpl("800x600");
		PhotoDimensions scaleTo=new PhotoDimensionsImpl("1600x1200");
		float expectedFactor=2.0f;
		assertEquals(expectedFactor,
			PhotoUtil.getScaleFactor(scaleFrom, scaleTo), 0.01);

		scaleTo=new PhotoDimensionsImpl("400x300");
		expectedFactor=0.5f;
		assertEquals(expectedFactor,
			PhotoUtil.getScaleFactor(scaleFrom, scaleTo), 0.01);
	}

	/** 
	 * Test dim scaling by factors.
	 */
	public void testDimScaleByFactor() {
		PhotoDimensions scaleFrom=new PhotoDimensionsImpl("800x600");
		float scaleFactor=1.0f;
		PhotoDimensions expected=new PhotoDimensionsImpl("800x600");
		assertEquals(expected, PhotoUtil.scaleBy(scaleFrom, scaleFactor));

		scaleFrom=new PhotoDimensionsImpl("800x600");
		scaleFactor=0.5f;
		expected=new PhotoDimensionsImpl("400x300");
		assertEquals(expected, PhotoUtil.scaleBy(scaleFrom, scaleFactor));

		scaleFrom=new PhotoDimensionsImpl("800x600");
		scaleFactor=2.0f;
		expected=new PhotoDimensionsImpl("1600x1200");
		assertEquals(expected, PhotoUtil.scaleBy(scaleFrom, scaleFactor));
	}

	/** 
	 * Test the PhotoUtil scaler implementation.
	 */
	public void testDimScaler() {
		// don't scale
		PhotoDimensions scaleFrom=new PhotoDimensionsImpl("800x600");
		PhotoDimensions scaleTo=new PhotoDimensionsImpl("1280x832");
		PhotoDimensions expected=new PhotoDimensionsImpl("800x600");
		assertSame(scaleFrom, PhotoUtil.scaleTo(scaleFrom, scaleTo));
		assertEquals(expected, PhotoUtil.scaleTo(scaleFrom, scaleTo));

		// Scale down keeping x
		scaleFrom=new PhotoDimensionsImpl("1280x832");
		scaleTo=new PhotoDimensionsImpl("800x600");
		expected=new PhotoDimensionsImpl("800x520");
		assertNotSame(scaleFrom, PhotoUtil.scaleTo(scaleFrom, scaleTo));
		assertEquals(expected, PhotoUtil.scaleTo(scaleFrom, scaleTo));

		// Scale down keeping y
		scaleFrom=new PhotoDimensionsImpl("832x1328");
		scaleTo=new PhotoDimensionsImpl("800x600");
		expected=new PhotoDimensionsImpl("375x600");
		assertEquals(expected, PhotoUtil.scaleTo(scaleFrom, scaleTo));

		// This case failed when deployed
		scaleFrom=new PhotoDimensionsImpl("683x430");
		scaleTo=new PhotoDimensionsImpl("220x146");
		expected=new PhotoDimensionsImpl("220x138");
		assertEquals(expected, PhotoUtil.scaleTo(scaleFrom, scaleTo));
	}

	/** 
	 * Test the PhotoUtil.smallerThan implementation.
	 */
	public void testSmallerThan() {
		PhotoDimensions ob1=new PhotoDimensionsImpl("800x600");
		PhotoDimensions ob2=new PhotoDimensionsImpl("640x480");
		assertTrue(PhotoUtil.smallerThan(ob2, ob1));
		assertFalse(PhotoUtil.smallerThan(ob1, ob2));
		assertFalse(PhotoUtil.smallerThan(ob1, ob1));
		assertFalse(PhotoUtil.smallerThan(ob2, ob2));

		ob1=new PhotoDimensionsImpl("640x480");
		ob2=new PhotoDimensionsImpl("481x640");
		assertTrue(PhotoUtil.smallerThan(ob1, ob2));
	}

}
