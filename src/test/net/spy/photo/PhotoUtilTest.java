// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>
// arch-tag: 970B1339-709B-410D-80D8-E930FA8A572C

package net.spy.photo;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

public class PhotoUtilTest extends TestCase {

	public void testDateParsing() throws Exception {
		Date d=PhotoUtil.parseDate("2005/10/05");
		assertEquals(d, PhotoUtil.parseDate("2005/10/5"));
		assertEquals(d, PhotoUtil.parseDate("2005-10-05"));
		assertEquals(d, PhotoUtil.parseDate("2005/10/5"));
		assertEquals(d, PhotoUtil.parseDate("10/5/2005"));
		assertEquals(d, PhotoUtil.parseDate("10-5-2005"));

		// Bad parses should return null
		assertNull(PhotoUtil.parseDate("05/10/05"));
		assertNull(PhotoUtil.parseDate("today"));
	}

	/**
	 * In the spirit of Eiffel, the implementation is basically copied here to
	 * make sure the result is consistent.
	 */
	public void testToday() throws Exception {
		SimpleDateFormat f=new SimpleDateFormat("MM/dd/yyyy");
		assertEquals(PhotoUtil.getToday(), f.format(new Date()));
	}
	
}
