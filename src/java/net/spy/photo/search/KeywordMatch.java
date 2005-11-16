// arch-tag: ABC21F3A-E2D0-4775-9065-0AEFFB6D8FC3

package net.spy.photo.search;

import java.util.Comparator;

import net.spy.jwebkit.SAXAble;
import net.spy.jwebkit.XMLUtils;
import net.spy.photo.Keyword;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * A keyword match from getKeywordsForUser().
 */
public class KeywordMatch implements SAXAble {

	/**
	 * KeywordMatch sorter by keyword (alphabetically).
	 */
	public static final Comparator<KeywordMatch> BY_KEYWORD=new KWMByKeyword();

	/**
	 * KeywordMatch sorter by frequency (most frequent first).
	 */
	public static final Comparator<KeywordMatch> BY_FREQUENCY=new KWMByFreq();	

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
}
