// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>
// arch-tag: AC904A23-518F-4299-BBDA-7D59650EA7F2

package net.spy.photo;

import java.io.File;
import java.io.FileInputStream;

import junit.framework.TestCase;
import net.spy.photo.impl.PhotoDimensionsImpl;

public class PhotoImageTest extends TestCase {

	private byte[] getFileData(String filename) throws Exception {
		File f = new File(System.getProperty("basedir")
			+ "/src/test/net/spy/photo/testdata/" + filename);

		byte rv[] = new byte[(int) f.length()];
		FileInputStream fis = new FileInputStream(f);
		fis.read(rv);
		fis.close();
		return rv;
	}

	public void testJpeg() throws Exception {
		byte data[] = getFileData("skate.jpg");
		PhotoImage pi = new PhotoImage(data);
		assertSame(data, pi.getData());
		assertEquals(Format.JPEG, pi.getFormat());
		assertEquals(Format.JPEG.getId(), pi.getFormat().getId());
		assertEquals("image/jpeg", pi.getFormat().getMime());
		assertEquals("jpg", pi.getFormat().getExtension());
		assertEquals(13579, pi.size());
		assertEquals(166, pi.getWidth());
		assertEquals(146, pi.getHeight());
		assertEquals("PhotoImage ({Format image/jpeg}) 166x146",
			String.valueOf(pi));
	}

	public void testPNG() throws Exception {
		byte data[] = getFileData("spyvspy2.png");
		PhotoImage pi = new PhotoImage(data);
		assertEquals(Format.PNG, pi.getFormat());
		assertEquals(Format.PNG.getId(), pi.getFormat().getId());
		assertEquals("image/png", pi.getFormat().getMime());
		assertEquals("png", pi.getFormat().getExtension());
		assertEquals(33441, pi.size());
		assertEquals(201, pi.getWidth());
		assertEquals(219, pi.getHeight());
		assertEquals("PhotoImage ({Format image/png}) 201x219",
			String.valueOf(pi));
	}

	public void testGIF() throws Exception {
		byte data[] = getFileData("sflogo.gif");
		PhotoImage pi = new PhotoImage(data);
		assertEquals(Format.GIF, pi.getFormat());
		assertEquals(Format.GIF.getId(), pi.getFormat().getId());
		assertEquals("image/gif", pi.getFormat().getMime());
		assertEquals("gif", pi.getFormat().getExtension());
		assertEquals(1968, pi.size());
		assertEquals(88, pi.getWidth());
		assertEquals(31, pi.getHeight());
		assertEquals("PhotoImage ({Format image/gif}) 88x31",
			String.valueOf(pi));
	}
	
	private void badData(byte data[]) {
		try {
			PhotoImage pi=new PhotoImage(data);
			fail("Made a PhotoImage out of bad data, got " + pi);
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
