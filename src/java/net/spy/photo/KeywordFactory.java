// arch-tag: E84A7C82-1269-4A26-81C4-6DA26305063A

package net.spy.photo;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

import net.spy.db.DBSPLike;
import net.spy.db.Saver;
import net.spy.factory.GenFactory;
import net.spy.photo.impl.KeywordImpl;
import net.spy.photo.sp.GetKeywords;
import net.spy.util.CloseUtil;

public class KeywordFactory extends GenFactory<Keyword> {

	private static final String CACHE_KEY="keywords";
	private static final int CACHE_TIME=3600000;

	private static AtomicReference<KeywordFactory> instanceRef=
		new AtomicReference<KeywordFactory>(null);

	private Set<String> ignoredKeywords=null;

	protected KeywordFactory() {
		super(CACHE_KEY, CACHE_TIME);
		ignoredKeywords=new HashSet<String>();
		ignoredKeywords.addAll(Arrays.asList(new String[]{
				"of", "the", "a", "for", "to", "in", 
			}));
	}

	public static KeywordFactory getInstance() {
		KeywordFactory rv=instanceRef.get();
		if(rv == null) {
			rv=new KeywordFactory();
			instanceRef.compareAndSet(null, rv);
		}
		return(rv);
	}

	@Override
	protected Collection<Keyword> getInstances() {
		Collection<Keyword> rv=new TreeSet<Keyword>();
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
			CloseUtil.close((DBSPLike)db);
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
		Keyword rv=null;
		// Skip ignored keywords
		if(!ignoredKeywords.contains(kw)) {
			rv=getObject(Keyword.BYWORD, kw);
			if(rv == null && create) {
				// If this didn't match an existing keyword, make a new one
				rv=new KeywordImpl(kw);
				// Save it
				Saver s=new Saver(PhotoConfig.getInstance());
				s.save((KeywordImpl)rv);
				// Cache it
				getCache().cacheInstance(rv);
			}
		}
		return(rv);
	}

	/**
	 * Parse the keyword string to a set of keywords.
	 * 
	 * @param s the input string
	 * @param create true if missing keywords should be created
	 * @return the Keywords instance
	 * @throws Exception if we can't get keywords
	 */
	public Keywords getKeywords(String s, boolean create) throws Exception {
		Set<Keyword> positive=new HashSet<Keyword>();
		Set<Keyword> negative=new HashSet<Keyword>();
		Set<String> missing=new HashSet<String>();

		StringTokenizer st = new StringTokenizer(s);
		while(st.hasMoreTokens()) {
			Collection<Keyword> addTo=positive;
			String kwstring = st.nextToken();
			if(kwstring.startsWith("-")) {
				kwstring=kwstring.substring(1);
				addTo=negative;
			}
			// Skip ignored keywords
			if(!ignoredKeywords.contains(kwstring)) {
				Keyword k = getKeyword(kwstring, create);
				if(k == null) {
					missing.add(kwstring);
				} else {
					addTo.add(k);
				}
			}
		}
		return(new Keywords(positive, negative, missing));
	}

	/**
	 * Result set from a keyword string parsing.
	 */
	public static class Keywords extends Object {
		private Set<Keyword> positive=null;
		private Set<Keyword> negative=null;
		private Set<String> missing=null;

		public Keywords(Set<Keyword>p, Set<Keyword>n, Set<String> m) {
			super();
			positive=p;
			negative=n;
			missing=m;
		}

		/**
		 * Get the set of negative keywords.
		 */
		public Set<Keyword> getNegative() {
			return negative;
		}

		/**
		 * Get the set of positive keywords.
		 */
		public Set<Keyword> getPositive() {
			return positive;
		}

		/**
		 * Get the set of missing keyword strings.
		 */
		public Set<String> getMissing() {
			return(missing);
		}
	}

}
