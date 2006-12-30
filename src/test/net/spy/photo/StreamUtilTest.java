package net.spy.photo;

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

import net.spy.photo.util.StreamUtil;

public class StreamUtilTest extends TestCase {

    private StreamUtil su=null;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        su=StreamUtil.getInstance();
    }

    public void testEmpty() throws Exception {
        byte[] rv=su.getBytes(new ByteArrayInputStream(new byte[0]));
        assertEquals(0, rv.length);
    }

    public void testSimple() throws Exception {
        byte[] rv=su.getBytes(new ByteArrayInputStream("abc".getBytes()));
        assertEquals(3, rv.length);
        assertEquals('a', rv[0]);
        assertEquals('b', rv[1]);
        assertEquals('c', rv[2]);
    }

}
