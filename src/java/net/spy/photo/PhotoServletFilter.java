package net.spy.photo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interface for 
 */
public interface PhotoServletFilter {

	/**
	 * Process an item in the chain.
	 * 
	 * @param pid the image data
	 * @param u the user making the request
	 * @param dims the requested dimensions
	 * @param req the servlet request
	 * @param res the servlet response
	 * @param chain the chain
	 * @throws Exception if it doesn't like something
	 */
	void doFilter(PhotoImage pid, User u, PhotoDimensions dims,
			HttpServletRequest req, HttpServletResponse res,
			PhotoServletChain chain)
		throws Exception;
}
