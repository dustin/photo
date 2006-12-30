package net.spy.photo.util;

import java.io.IOException;
import java.io.InputStream;

public class StreamUtil {

    private static StreamUtil instance=new StreamUtil();

    private StreamUtil() {
        super();
    }

    /**
     * Get the singleton StreamUtil instance.
     */
    public static StreamUtil getInstance() {
        assert instance != null;
        return instance;
    }

    /**
     * Read all of the bytes of the given input stream into a byte array.
     * 
     * @param is the given input stream
     * @return the bytes from the stream
     * @throws IOException if an error occurs while reading the stream
     */
    public byte[] getBytes(InputStream is) throws IOException {
        ByteJoiner bj = new ByteJoiner();
        byte[] tmp = new byte[8192];
        int read=0;

        do {
            read = is.read(tmp);
            if(read > 0) {
                byte[] x = new byte[read];
                System.arraycopy(tmp, 0, x, 0, read);
                bj.addChunk(x);
            }
        } while(read > 0);

        return bj.getByteArray();
    }
}
