// Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
// $Id: PhotoXSLT.java,v 1.2 2000/11/10 07:17:18 dustin Exp $

package net.spy.photo;

import org.xml.sax.SAXException;
import org.apache.xalan.xslt.XSLTProcessorFactory;
import org.apache.xalan.xslt.XSLTInputSource;
import org.apache.xalan.xslt.XSLTResultTarget;
import org.apache.xalan.xslt.XSLTProcessor;
import java.io.StringReader;
import java.io.Writer;

import javax.servlet.*;
import javax.servlet.http.*;

import net.spy.SpyUtil;
import net.spy.cache.*;

public class PhotoXSLT extends Object {
	public static void sendXML(String xml, String stylesheet, Writer out)
		throws Exception {

		// Get a StringReader for the XML input.
		XSLTInputSource xml_input=new XSLTInputSource(new StringReader(xml));

		PhotoConfig conf=new PhotoConfig();
		stylesheet=lookupStylesheet(conf, stylesheet);
		XSLTProcessor processor = XSLTProcessorFactory.getProcessor();

		// See if we have cached a compiled stylesheet.
		XSLTInputSource xslt_stylesheet=null;
		SpyCache cache=new SpyCache();
		xslt_stylesheet=(XSLTInputSource)cache.get("xslt_" + stylesheet);

		// If it's not in cache, get one and cache it.
		if(xslt_stylesheet==null) {
			// Get a new stylesheet
			xslt_stylesheet=new XSLTInputSource(stylesheet);
			// Cache it for fifteen minutes by default - accepts seconds from the conf file.
			System.err.println("cache timeout: "
				+ conf.getInt("xslt_cache_timeout", 900));
			cache.store("xslt_" + stylesheet, xslt_stylesheet, 
				((long)(conf.getInt("xslt_cache_timeout", 900)))*1000 );
		}

		// Now, do the processing, and send the output to the browser.
		processor.process(xml_input, xslt_stylesheet,
			new XSLTResultTarget(out));
	}

	protected static String lookupStylesheet(PhotoConfig conf, String ss) {
		String ret=null;
		if(ss==null) {
			ret=conf.get("xslt.default");
		} else {
			ret=conf.get("xslt." + ss);
			if(ret==null) {
				ret=conf.get("xslt.default");
			}
		}

		return(ret);
	}

	public static String normalize(String s, boolean canonical) {
		StringBuffer str=new StringBuffer();

		int len=(s!=null)?s.length():0;

		for(int i=0; i<len; i++) {
			char ch=s.charAt(i);

			switch(ch) {
				case '<': {
					str.append("&lt;");
					break;
				}
				case '>': {
					str.append("&gt;");
					break;
				}
				case '&': {
					str.append("&amp;");
					break;
				}
				case '"': {
					str.append("quot;");
					break;
				}
				case '\r':
				case '\n': {
					if(canonical) {
						str.append("&#");
						str.append(Integer.toString(ch));
						str.append(';');
						break;
					}
				}
				default: {
					str.append(ch);
				}
			}
		}
		return(str.toString());
	}
}
