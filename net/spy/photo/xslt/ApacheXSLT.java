// Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
// $Id: ApacheXSLT.java,v 1.1 2001/12/29 06:19:40 dustin Exp $

package net.spy.photo.xslt;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import net.spy.SpyUtil;
import net.spy.cache.*;

import net.spy.photo.*;

// Apache XSL
import org.apache.xalan.xslt.XSLTProcessorFactory;
import org.apache.xalan.xslt.XSLTInputSource;
import org.apache.xalan.xslt.XSLTResultTarget;
import org.apache.xalan.xslt.XSLTProcessor;
import org.xml.sax.SAXException;

/**
 * Perform XSLT with the Apache code
 */
public class ApacheXSLT extends PhotoXSLT {

	/**
	 * Construct.
	 */
	public ApacheXSLT() {
		super();
	}

	/**
	 * Process, Apache style.
	 */
	public void processXSLT(String xml, String stylesheet,
		OutputStream out) throws Exception {

		// Get a StringReader for the XML input.
		XSLTInputSource xml_input=new XSLTInputSource(new StringReader(xml));

		XSLTProcessor processor = XSLTProcessorFactory.getProcessor();
		XSLTInputSource xslt_stylesheet=new XSLTInputSource(stylesheet);
		processor.process(xml_input, xslt_stylesheet,
			new XSLTResultTarget(out));

	}
}
