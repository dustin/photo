// arch-tag: E84A7C82-1269-4A26-81C4-6DA26305063A

package net.spy.photo;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import net.spy.db.Saver;
import net.spy.factory.CacheEntry;
import net.spy.factory.GenFactory;
import net.spy.factory.HashCacheEntry;
import net.spy.photo.impl.KeywordImpl;
import net.spy.photo.sp.GetKeywords;

public class KeywordFactory extends GenFactory<Keyword> {

	private static final String CACHE_KEY="keywords";
	private static final int CACHE_TIME=3600000;

	private static KeywordFactory instance=null;

	protected KeywordFactory() {
		super(CACHE_KEY, CACHE_TIME);
	}

	public static synchronized KeywordFactory getInstance() {
		if(instance == null) {
			instance=new KeywordFactory();
		}
		return(instance);
	}

	@Override
	protected Collection<Keyword> getInstances() {
		Collection rv=new TreeSet();
		GetKeywords db=null;
		try {
			db=new GetKeywords(PhotoConfig.getInstance());
			ResultSet rs=db.executeQuery();
			while(rs.next()) {
				KeywordImpl k=new KeywordImpl(rs.getString("word"),
					rs.getInt("word_id"));
				rv.add(k);
			}
			rs.close();
		} catch(Exception e) {
			throw new RuntimeException("Couldn't initialize keywords", e);
		} finally {
			if(db != null) {
				db.close();
			}
		}
		return(rv);
	}

	/**
	 * Get a keyword by word.
	 * 
	 * @param word the word
	 * @return a new Keyword instance, or null if there's no such word
	 */
	public Keyword getKeyword(String word) throws Exception {
		return(getKeyword(word, false));
	}

	/** 
	 * Get a keyword by word.  Optionally, a new keyword instance may be
	 * created if one does not exist.
	 */
	public Keyword getKeyword(String kw, boolean create)
		throws Exception {
		if(kw==null) {
			throw new NullPointerException("There is no null keyword.");
		}
		kw=kw.trim().toLowerCase();
		KeywordCacheEntry c=(KeywordCacheEntry)getCache();
		Keyword rv=c.getByWord(kw);
		if(rv == null && create) {
			// If this didn't match an existing keyword, make a new one
			rv=new KeywordImpl(kw);
			// Save it
			Saver s=new Saver(PhotoConfig.getInstance());
			s.save((KeywordImpl)rv);
			// Cache it
			getCache().cacheInstance(rv);
		}
		return(rv);
	}

	protected CacheEntry<Keyword> getNewCacheEntry() {
		return(new KeywordCacheEntry());
	}

	private static class KeywordCacheEntry extends HashCacheEntry<Keyword> {
		private Map<String, Keyword> byWord=null;
		public KeywordCacheEntry() {
			super();
			byWord=new HashMap<String, Keyword>();
		}
		public void cacheInstance(Keyword k) {
			super.cacheInstance(k);
			byWord.put(k.getKeyword(), k);
		}
		public Keyword getByWord(String k) {
			return(byWord.get(k));
		}
	}
}
