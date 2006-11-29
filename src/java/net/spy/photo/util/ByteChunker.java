// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>
// arch-tag: 3C03A2FA-CC61-4429-9919-C52AF35DAA23

package net.spy.photo.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.spy.SpyObject;

public class ByteChunker extends SpyObject implements Iterable<byte[]> {

    byte[] data=null;
    int chunkSize=0;

    /** 
     * Construct a ByteChunker with the given byte array and chunk size.
     * 
     * @param d the byte array
     * @param c the chunk size
     */
    public ByteChunker(byte d[], int c) {
        super();
        if(d == null) {
            throw new NullPointerException("Can't chunk a null byte array");
        }
        if(c < 1) {
            throw new IllegalArgumentException(
                    "Can't make chunks smaller than one byte");
        }
        data=d;
        chunkSize=c;
    }

    /** 
     * Get an iterator over the chunked bytes.
     */
    public Iterator<byte[]> iterator() {
        return new Iterator<byte[]>() {
            int i=0;

            public boolean hasNext() {
                return i<data.length;
            }

            public byte[] next() {
                if(!hasNext()) {
                    throw new NoSuchElementException(
                        "Can't iterate past the end");
                }

                int max=data.length-i;
                if(max > chunkSize) {
                    max=chunkSize;
                }
                byte rv[]=new byte[max];
                System.arraycopy(data, i, rv, 0, max);
                i+=max;
                return rv;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

}
