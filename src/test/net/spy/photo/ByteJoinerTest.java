package net.spy.photo;

import java.util.Arrays;

import junit.framework.TestCase;

import net.spy.photo.util.ByteJoiner;

public class ByteJoinerTest extends TestCase {

	private String dump(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append((char) b);
		}
		return sb.toString();
	}

	private void checkSequence(byte[] expected, byte[] b) {
		assertEquals(expected.length, b.length);
		assertTrue(dump(b) + " != " + dump(expected),
				Arrays.equals(b, expected));
	}

	public void testEmptyJoin() {
		ByteJoiner bj = new ByteJoiner();
		checkSequence(new byte[0], bj.getByteArray());
	}

	public void testOneByteArrayJoining() throws Exception {
		ByteJoiner bj = new ByteJoiner();
		byte[] b = new byte[1];

		b = "a".getBytes();
		bj.addChunk(b);
		checkSequence(b, "a".getBytes());
	}

	public void testMultiChunkJoining() throws Exception {
		ByteJoiner bj = new ByteJoiner();

		byte[][] in = { "i'm ".getBytes(), "a ".getBytes(),
				"little ".getBytes(), "tea ".getBytes(), "pot".getBytes() };

		for (byte[] b : in) {
			bj.addChunk(b);
		}
		checkSequence(bj.getByteArray(), "i'm a little tea pot".getBytes());
	}

	public void testMultiChunkMultiAddJoining() throws Exception {
		ByteJoiner bj = new ByteJoiner();

		byte[][] in1 = { "i ".getBytes(), "want ".getBytes(), "to ".getBytes(),
				"rock ".getBytes(), "and ".getBytes(), "roll ".getBytes(),
				"all ".getBytes(), "night".getBytes() };
		byte[][] in2 = { " and ".getBytes(), "something ".getBytes(),
				"something".getBytes() };

		for (byte[] b : in1) {
			bj.addChunk(b);
		}

		checkSequence(bj.getByteArray(), "i want to rock and roll all night"
				.getBytes());

		for (byte[] b : in2) {
			bj.addChunk(b);
		}

		checkSequence(bj.getByteArray(),
				"i want to rock and roll all night and something something"
						.getBytes());
	}

	public void testAddNullChunk() throws Exception {
		ByteJoiner bj = new ByteJoiner();

		try {
			bj.addChunk(null);
		} catch (NullPointerException ne) {
			return;
		}

		fail("Did not catch NullPointerException when adding null chunk.");
	}

}
