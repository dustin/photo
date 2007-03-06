package net.spy.photo;

import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.SpyObject;

/**
 * Chain interface for filtering PhotoServlet image requests.
 */
public class PhotoServletChain extends SpyObject {

	private Iterator<? extends PhotoServletFilter> fili=null;

	public PhotoServletChain(Collection<PhotoServletFilter> c) {
		super();
		fili=c.iterator();
	}

	public void doChain(PhotoImage pid, User u, PhotoDimensions dims,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		if(fili.hasNext()) {
			PhotoServletFilter fil=fili.next();
			fil.doFilter(pid, u, dims, req, res, this);
		}
	}
}
