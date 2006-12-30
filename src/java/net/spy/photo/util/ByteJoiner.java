package net.spy.photo.util;

import java.util.LinkedList;
import java.util.List;

/**
 * Assemble a list of bytes into a single byte array.
 */
public class ByteJoiner {

    private List<byte[]> chunkedBytes=new LinkedList<byte[]>();

    private int totalBytes = 0;

    public void addChunk(byte[] chunk) {
        if (chunk == null) {
            throw new NullPointerException("Cannot add null chunk");
        }
        chunkedBytes.add(chunk);
        totalBytes += chunk.length;
    }

    public byte[] getByteArray() {
        int bytesCopied = 0;
        byte[] byteArray = new byte[totalBytes];

        for (byte[] element : chunkedBytes) {
            System.arraycopy(element, 0, byteArray, bytesCopied,
                            element.length);
            bytesCopied += element.length;
        }

        return byteArray;
    }

}
