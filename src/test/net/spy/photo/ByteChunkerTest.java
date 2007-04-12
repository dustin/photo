// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>

package net.spy.photo;

import java.util.Arrays;
import java.util.Iterator;

import junit.framework.TestCase;

import net.spy.photo.impl.DBPermanentStorage;
import net.spy.photo.util.ByteChunker;

public class ByteChunkerTest extends TestCase {

    private void assertSequence(byte[][] expected, ByteChunker in) {
        Iterator<byte[]> bit=in.iterator();
        for(int i=0; i<expected.length; i++) {
            assertTrue(bit.hasNext());
            byte wanted[]=expected[i];
            byte got[]=bit.next();
            if(!Arrays.equals(wanted, got)) {
                fail("At " + i + " expected " + Arrays.toString(wanted)
                    + ", but got " + Arrays.toString(got));
            }
        }
        assertFalse(bit.hasNext());
    }

    public void testByOne() throws Exception {
        byte data[]=new byte[]{1, 2, 3, 4};
        assertSequence(new byte[][]{new byte[]{1}, new byte[]{2},
                new byte[]{3}, new byte[]{4}}, new ByteChunker(data, 1));
    }

    private byte[][] getBytes(String[] in) {
        byte rv[][]=new byte[in.length][];
        for(int i=0; i<in.length; i++) {
            rv[i]=in[i].getBytes();
        }
        return rv;
    }

    public void testByOneString() throws Exception {
        byte data[]="Test".getBytes();
        assertSequence(getBytes(new String[]{"T", "e", "s", "t"}),
            new ByteChunker(data, 1));
    }

    public void testByThree() throws Exception {
        byte data[]="Testing...123...".getBytes();
        assertSequence(getBytes(new String[]{
            "Tes", "tin", "g..", ".12", "3..", "."
            }),
            new ByteChunker(data, 3));
    }

    /**
     * Test an actual use case I have, which is to have base64 encoded chunks
     * at a particular split size fall below a specific column size.
     */
    public void testBase64EncodedChunks() throws Exception {
        byte data[]=new byte[4096];
        int seen=0;
        for(byte b[] : new ByteChunker(data, DBPermanentStorage.CHUNK_SIZE)) {
            seen+=b.length;
        }
        assertEquals(data.length, seen);
    }


}
