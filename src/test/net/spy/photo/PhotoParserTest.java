// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>

package net.spy.photo;

import java.io.File;
import java.io.FileInputStream;

import junit.framework.TestCase;

public class PhotoParserTest extends TestCase {

	private PhotoParser parser=null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		parser=PhotoParser.getInstance();
	}

	private byte[] getFileData(String filename) throws Exception {
		File f = new File(System.getProperty("basedir")
			+ "/src/test/net/spy/photo/testdata/" + filename);

		byte rv[] = new byte[(int) f.length()];
		FileInputStream fis = new FileInputStream(f);
		fis.read(rv);
		fis.close();
		return rv;
	}

	/*
	private void scaleTest(String n, int w, int h) throws Exception {
		byte data[] = getFileData(n);
		PhotoImage pi = new PhotoImage(data);
		PhotoImageScaler scaler=new PhotoImageScaler(pi);
		PhotoImage scaled=scaler.getScaledImage(
				new PhotoDimensionsImpl("50x50"), 90);
		assertEquals(w, scaled.getWidth());
		assertEquals(h, scaled.getHeight());
	}

	public void testScaling() throws Exception {
		scaleTest("skate.jpg", 50, 43);
		scaleTest("spyvspy2.png", 45, 50);
		scaleTest("sflogo.gif", 50, 17);
	}
	*/

	public void testJpeg() throws Exception {
		byte data[] = getFileData("skate.jpg");
		PhotoParser.Result res=parser.parseImage(data);
		assertEquals(Format.JPEG, res.getFormat());
		assertEquals(Format.JPEG.getId(), res.getFormat().getId());
		assertEquals("image/jpeg", res.getFormat().getMime());
		assertEquals("jpg", res.getFormat().getExtension());
		assertEquals(13579, data.length);
		assertEquals(166, res.getWidth());
		assertEquals(146, res.getHeight());
		assertEquals("f57097246c70ee48d1e16f4ae6577b1c", res.getMd5());
	}

	public void testPNG() throws Exception {
		byte data[] = getFileData("spyvspy2.png");
		PhotoParser.Result res=parser.parseImage(data);
		assertEquals(Format.PNG, res.getFormat());
		assertEquals(Format.PNG.getId(), res.getFormat().getId());
		assertEquals("image/png", res.getFormat().getMime());
		assertEquals("png", res.getFormat().getExtension());
		assertEquals(33441, data.length);
		assertEquals(201, res.getWidth());
		assertEquals(219, res.getHeight());
		assertEquals("903a8f0871ef8d75b2127e15b716083b", res.getMd5());
	}

	public void testGIF() throws Exception {
		byte data[] = getFileData("sflogo.gif");
		PhotoParser.Result res=parser.parseImage(data);
		assertEquals(Format.GIF, res.getFormat());
		assertEquals(Format.GIF.getId(), res.getFormat().getId());
		assertEquals("image/gif", res.getFormat().getMime());
		assertEquals("gif", res.getFormat().getExtension());
		assertEquals(1968, data.length);
		assertEquals(88, res.getWidth());
		assertEquals(31, res.getHeight());
		assertEquals("70038412b7daaa63ade5b8e6b7fa5045", res.getMd5());
	}
	
	private void badData(byte data[]) {
		try {
			PhotoParser.Result res=parser.parseImage(data);
			fail("Made a PhotoImage out of bad data, got " + res);
		} catch(PhotoException e) {
			assertNotNull(e.getMessage());
		} catch(IllegalArgumentException e) {
			assertNotNull(e.getMessage());
		}
	}

	public void testBadData() throws Exception {
		badData(null); // null data is always bad
		badData(new byte[0]); // as is empty data
		badData(new byte[40]); // smaller than the smallest
		// big enough for anything, but nothing
		badData(getFileData("test.random"));
	}

	/*
	private void equalityTest(String name) throws Exception {
		PhotoImage pi1=new PhotoImage(getFileData(name));
		PhotoImage pi2=new PhotoImage(getFileData(name));
		// No two instances are considered equal, or have the same hash code
		assertFalse(pi1.equals(pi2));
		assertFalse(pi1.hashCode() == pi2.hashCode());
		// But by dimensions, equality does work.  This is a little weird...
		PhotoDimensions pd=new PhotoDimensionsImpl(
				pi1.getWidth(), pi2.getHeight());
		assertEquals(pi1, pd);
		assertEquals(pi2, pd);
	}

	public void testEquality() throws Exception {
		equalityTest("sflogo.gif");
		equalityTest("skate.jpg");
		equalityTest("spyvspy2.png");
	}
	*/
	
	public void testBadFormatRequest() {
		try {
			Format f=Format.getFormat(19925);
			fail("Shouldn't be able to get format 19925, got " + f);
		} catch(IllegalArgumentException e) {
			assertEquals("Invalid format id:  19925", e.getMessage());
		}
	}
	
	public void testUnknownFormat() {
		Format f=Format.getFormat(Format.UNKNOWN.getId());
		assertEquals("image/unknown", f.getMime());
		assertEquals("unk", f.getExtension());
		assertEquals(Format.UNKNOWN.getId(), f.getId());
	}
}
