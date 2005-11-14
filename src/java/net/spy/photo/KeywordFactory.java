// arch-tag: E84A7C82-1269-4A26-81C4-6DA26305063A

package net.spy.photo;

import java.util.Comparator;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import net.spy.db.Saver;
import net.spy.factory.CacheEntry;
import net.spy.factory.GenFactory;
import net.spy.factory.HashCacheEntry;
import net.spy.jwebkit.SAXAble;
import net.spy.jwebkit.XMLUtils;
import net.spy.photo.impl.KeywordImpl;
import net.spy.photo.search.Search;
import net.spy.photo.sp.GetKeywords;
import net.spy.photo.struts.SearchForm;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class KeywordFactory extends GenFactory<Keyword> {

	private static final String CACHE_KEY="keywords";
	private static final int CACHE_TIME=3600000;

	/**
	 * KeywordMatch sorter by keyword (alphabetically).
	 */
	public static final Comparator<KeywordMatch> KEYWORDMATCH_BY_KEYWORD=new KWMByKeyword();

	/**
	 * KeywordMatch sorter by frequency (most frequent first).
	 */
	public static final Comparator<KeywordMatch> KEYWORMATCH_BY_FREQUENCY=new KWMByFreq();

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

	/**
	 * Get all of the keywords applicable to the given user.
	 * 
	 * @param u the given user
	 * @return a map of 
	 * @throws Exception
	 */
	public Collection<KeywordMatch> getKeywordsForUser(User u) throws Exception {
		Map<String, KeywordMatch> rv=new TreeMap<String, KeywordMatch>();
		SearchForm sf=new SearchForm();
		sf.setSdirection("desc");
		for(PhotoImageData pid : Search.getInstance().performSearch(sf, u)) {
			for(Keyword kw : pid.getKeywords()) {
				KeywordMatch km=rv.get(kw.getKeyword());
				if(km == null) {
					km=new KeywordMatch(kw, pid.getId());
					rv.put(kw.getKeyword(), km);
				}
				km.increment();
			}
		}
		return(rv.values());
	}

	/**
	 * A keyword match from getKeywordsForUser().
	 */
	public static class KeywordMatch implements SAXAble {
		private Keyword keyword=null;
		private int count=0;
		private int imgId=0;
		public KeywordMatch(Keyword kw, int img) {
			super();
			keyword=kw;
			imgId=img;
		}
		public void increment() {
			count++;
		}
		public int getCount() {
			return count;
		}
		public int getImgId() {
			return imgId;
		}
		public Keyword getKeyword() {
			return keyword;
		}
		public void writeXml(ContentHandler handler) throws SAXException {
			XMLUtils x=XMLUtils.getInstance();
			x.startElement(handler, "kwmatch");
			x.doElement(handler, "id", String.valueOf(keyword.getId()));
			x.doElement(handler, "word", keyword.getKeyword());
			x.doElement(handler, "count", String.valueOf(count));
			x.doElement(handler, "img", String.valueOf(imgId));
			x.endElement(handler, "kwmatch");
		}
	}

	private static class KWMByKeyword implements Comparator<KeywordMatch> {

		public int compare(KeywordMatch k1, KeywordMatch k2) {
			return(k1.getKeyword().getKeyword().compareTo(k2.getKeyword().getKeyword()));
		}
		
	}

	private static class KWMByFreq extends KWMByKeyword {

		public int compare(KeywordMatch k1, KeywordMatch k2) {
			int f1=k1.getCount();
			int f2=k2.getCount();
			int rv=0;
			if(f1 > f2) {
				rv=-1;
			} else if(f1 < f2) {
				rv=1;
			} else {
				rv=0;
			}
			if(rv == 0) {
				rv=super.compare(k1, k2);
			}
			return(rv);
		}
		
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