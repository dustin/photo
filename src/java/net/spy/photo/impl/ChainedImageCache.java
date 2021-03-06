// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>

package net.spy.photo.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import net.spy.SpyObject;
import net.spy.photo.ImageCache;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoException;

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

	private List<ImageCache> getList=null;
	private List<ImageCache> putList=null;

	/**
	 * Get an instance of ChainedImageCache.
	 */
	public ChainedImageCache() {
		super();

		// Find all of the unique names and load the objects
		Map<String, ImageCache> m=new HashMap<String, ImageCache>();
		List<String> getListNames=parseList("chaincache.get");
		loadMap(m, getListNames);
		List<String> putListNames=parseList("chaincache.put");
		loadMap(m, putListNames);

		// Load the getList
		if(getListNames != null) {
			getList=new ArrayList<ImageCache>();
			for(String nm : getListNames) {
				getList.add(m.get(nm));
			}
		}
		// Load the putList
		if(putListNames != null) {
			putList=new ArrayList<ImageCache>();
			for(String nm : putListNames) {
				putList.add(m.get(nm));
			}
		}
	}

	private void loadMap(Map<String, ImageCache> m, List<String> l) {
		if(l != null) {
			try {
				for(String nm : l) {
					if(!m.containsKey(nm)) {
						@SuppressWarnings("unchecked")
						Class<ImageCache> c=(Class<ImageCache>)Class.forName(nm);
						ImageCache o=c.newInstance();
						m.put(nm, o);
					}
				}
			} catch(Exception e) {
				throw new RuntimeException(
					"Problem loading chained image caches", e);
			}
		}
	}

	private List<String> parseList(String key) {
		List<String> rv=null;

		PhotoConfig conf=PhotoConfig.getInstance();

		String tmp=conf.get(key);
		if(tmp != null) {
			StringTokenizer st=new StringTokenizer(tmp);
			if(st.countTokens() > 0) {
				rv=new ArrayList<String>();
				while(st.hasMoreTokens()) {
					String nt=st.nextToken();
					if(ChainedImageCache.class.getName().equals(nt)) {
						throw new IllegalArgumentException(
							"Can't chain a chain");
					}
					rv.add(nt);
				}
			}
		}
		return(rv);
	}

	/** 
	 * Get a PhotoImage from the first ImageCache implementation that has one
	 * for the given key.
	 */
	public byte[] getImage(String key) throws PhotoException {
		byte[] rv=null;
		if(getList != null) {
			Collection<ImageCache> storeMatch=new ArrayList<ImageCache>();
			for(Iterator<ImageCache> i=getList.iterator();
					rv==null && i.hasNext();) {
				ImageCache ic=i.next();
				getLogger().debug("Looking in cache %s for %s", ic, key);
				rv=ic.getImage(key);
				if(rv == null && putList != null && putList.contains(ic)) {
					storeMatch.add(ic);
				}
			}
			if(rv != null) {
				for(ImageCache ic : storeMatch) {
					getLogger().debug("Backcaching %s to %s", key, ic);
					ic.putImage(key, rv);
				}
			}
		}
		return(rv);
	}

	/** 
	 * Store a PhotoImage in all chained caches.
	 */
	public void putImage(String key, byte[] image) throws PhotoException {
		if(putList != null) {
			for(ImageCache ic : putList) {
				getLogger().debug("Storing %s in %s", key, ic);
				ic.putImage(key, image);
			}
		}
	}

	public boolean willStore(String key, byte[] image) {
		boolean rv=false;
		for(ImageCache ic : putList) {
			rv |= ic.willStore(key, image);
		}
		return rv;
	}

}
