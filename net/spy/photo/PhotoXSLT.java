// Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
// $Id: PhotoXSLT.java,v 1.5 2001/12/29 06:19:40 dustin Exp $

package net.spy.photo;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import net.spy.SpyUtil;
import net.spy.cache.*;

/**
 * Perform XSLT translations and stuff.
 */
public abstract class PhotoXSLT extends Object {

	private static PhotoXSLT _processor=null;

	/**
	 * Abstract XML sending thingy.
	 */
	public static void sendXML(String xml, String stylesheet,
		ServletResponse response)
		throws Exception {

		OutputStream out=response.getOutputStream();

		// Lookup the stylesheet
		PhotoConfig conf=new PhotoConfig();
		stylesheet=lookupStylesheet(conf, stylesheet);

		// Make sure we have a processor ready.
		verifyProcessor();

		_processor.processXSLT(xml, stylesheet, out);

	}

	// Do what it takes to get the processor ready.
	private static synchronized void verifyProcessor() throws Exception {
		if(_processor==null) {
			PhotoConfig conf=new PhotoConfig();
			Class c=Class.forName(conf.get("xslt_processor"));
			_processor=(PhotoXSLT)c.newInstance();
		}
	}

	/**
	 * Perform engine-specific processing.
	 */
	public abstract void processXSLT(String xml, String stylesheet,
		OutputStream out) throws Exception;

	/**
	 * Look up a stylesheet based on the name.
	 */
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

	/**
	 * Normalize a string for use in XML.
	 */
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
