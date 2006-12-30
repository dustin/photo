package net.spy.photo.util;

import net.spy.SpyObject;

/**
 * Longest common subsequence computation.
 */
public class LCS extends SpyObject {

	private static LCS instance=new LCS();

	/**
	 * Get the singleton LCS instance.
	 */
	public static LCS getInstance() {
		return instance;
	}

	private LCS() {
		super();
	}

	/**
	 * Compute the LCS of two strings.
	 *
	 * @param one first string
	 * @param two second string
	 * @return string form of the longest common subsequence.
	 */
	public String lcs(String one, String two) {
		int[][] opt=new int[one.length()+1][two.length()+1];

		// compute length of LCS and all subproblems via dynamic programming
		for(int i=one.length()-1; i>=0; i--) {
			for(int j=two.length()-1; j>=0; j--) {
                if (one.charAt(i) == two.charAt(j)) {
                    opt[i][j] = opt[i+1][j+1] + 1;
                } else { 
                    opt[i][j] = Math.max(opt[i+1][j], opt[i][j+1]);
                }
			}
		}

		// Now that the lengths have been computed, find the actual LCS
		StringBuilder sb=new StringBuilder();

        int i = 0, j = 0;
        while(i < one.length() && j < two.length()) {
            if (one.charAt(i) == two.charAt(j)) {
            	sb.append(one.charAt(i));
                i++;
                j++;
            } else if (opt[i+1][j] >= opt[i][j+1]) {
            	i++;
            } else {
            	j++;
            }
        }

		return sb.toString();
	}
}
