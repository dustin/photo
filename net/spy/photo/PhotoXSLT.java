// Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
// $Id: PhotoXSLT.java,v 1.4 2001/07/19 08:02:11 dustin Exp $

package net.spy.photo;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import net.spy.SpyUtil;
import net.spy.cache.*;

// Resin XSL
import com.caucho.transform.*;
import com.caucho.xsl.*;

// Apache XSL
import org.apache.xalan.xslt.XSLTProcessorFactory;
import org.apache.xalan.xslt.XSLTInputSource;
import org.apache.xalan.xslt.XSLTResultTarget;
import org.apache.xalan.xslt.XSLTProcessor;
import org.xml.sax.SAXException;

/**
 * Perform XSLT translations and stuff.
 */
public class PhotoXSLT extends Object {

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

		try {
			sendXMLResin(xml, stylesheet, out);
		} catch(Throwable t) {
			t.printStackTrace();
			sendXMLApache(xml, stylesheet, out);
		}
	}

	// The apache one.
	private static void sendXMLApache(String xml, String stylesheet,
		OutputStream out) throws Exception {

		// Get a StringReader for the XML input.
		XSLTInputSource xml_input=new XSLTInputSource(new StringReader(xml));

		XSLTProcessor processor = XSLTProcessorFactory.getProcessor();
		XSLTInputSource xslt_stylesheet=new XSLTInputSource(stylesheet);
		processor.process(xml_input, xslt_stylesheet,
			new XSLTResultTarget(out));

	}
		
	// Resin version of the sender.
	private static void sendXMLResin(String xml, String stylesheet,
		OutputStream out) throws Exception {

		// Get a stylesheet factory
		StylesheetFactory factory=new Xsl();
		Stylesheet style=factory.newStylesheet(stylesheet);

		StreamTransformer transformer=style.newStreamTransformer();
		transformer.transformString(xml, out);
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
