// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>

package net.spy.photo;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;

import net.spy.SpyObject;

/**
 * ImageCache implementation that wraps other ImageCache implementations to
 * allow different combinations of storage and retrieval.
 *
 * The following PhotoConfig variables are used:
 * <ul>
 *  <li><code>chaincache.get - list of ImageCache classes to use for gets<li>
 *  <li><code>chaincache.put - list of ImageCache classes to use for puts<li>
 * </ul>
 */
public class ChainedImageCache extends SpyObject implements ImageCache {

	private List getList=null;
	private List putList=null;

	/**
	 * Get an instance of ChainedImageCache.
	 */
	public ChainedImageCache() {
		super();

		// Find all of the unique names and load the objects
		Map m=new HashMap();
		List getListNames=parseList("chaincache.get");
		loadMap(m, getListNames);
		List putListNames=parseList("chaincache.put");
		loadMap(m, putListNames);

		// Load the getList
		if(getListNames != null) {
			getList=new ArrayList();
			for(Iterator i=getListNames.iterator(); i.hasNext();) {
				getList.add(m.get(i.next()));
			}
		}
		// Load the putList
		if(putListNames != null) {
			putList=new ArrayList();
			for(Iterator i=putListNames.iterator(); i.hasNext();) {
				putList.add(m.get(i.next()));
			}
		}
	}

	private void loadMap(Map m, List l) {
		if(l != null) {
			try {
				for(Iterator i=l.iterator(); i.hasNext();) {
					String nm=(String)i.next();
					if(!m.containsKey(nm)) {
						Class c=Class.forName(nm);
						Object o=c.newInstance();
						m.put(nm, o);
					}
				}
			} catch(Exception e) {
				throw new RuntimeException(
					"Problem loading chained image caches", e);
			}
		}
	}

	private List parseList(String key) {
		List rv=null;

		PhotoConfig conf=PhotoConfig.getInstance();

		String tmp=conf.get(key);
		if(tmp != null) {
			StringTokenizer st=new StringTokenizer(tmp);
			if(st.countTokens() > 0) {
				rv=new ArrayList();
				while(st.hasMoreTokens()) {
					String nt=st.nextToken();
					if(ChainedImageCache.class.getName().equals(nt)) {
						throw new IllegalArgumentException(
							"Can't chain a chain");
					}
					rv.add(st.nextToken());
				}
			}
		}
		return(rv);
	}

	/** 
	 * Get a PhotoImage from the first ImageCache implementation that has one
	 * for the given key.
	 */
	public PhotoImage getImage(String key) throws PhotoException {
		PhotoImage rv=null;
		if(getList != null) {
			for(Iterator i=getList.iterator(); rv == null && i.hasNext(); ) {
				ImageCache ic=(ImageCache)i.next();
				rv=ic.getImage(key);
			}
		}
		return(rv);
	}

	/** 
	 * Store a PhotoImage in all chained caches.
	 */
	public void putImage(String key, PhotoImage image) throws PhotoException {
		if(putList != null) {
			for(Iterator i=putList.iterator(); i.hasNext(); ) {
				ImageCache ic=(ImageCache)i.next();
				ic.putImage(key, image);
			}
		}
	}

}
