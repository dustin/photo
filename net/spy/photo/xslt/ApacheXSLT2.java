// Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
// $Id: ApacheXSLT2.java,v 1.2 2002/05/23 20:45:27 dustin Exp $
// Apache Xalan 2 support by Robert Searle

package net.spy.photo.xslt;

import java.io.*;
import java.util.Hashtable;

import javax.servlet.*;
import javax.servlet.http.*;

import net.spy.SpyUtil;
import net.spy.cache.*;

import net.spy.photo.*;

// Apache XSL
import org.xml.sax.SAXException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.Templates;
// Imported TraX classes



/**
 * Perform XSLT with the Apache code
 */
public class ApacheXSLT2 extends PhotoXSLT {
	private final static transient TransformerFactory tFactory =
		TransformerFactory.newInstance();
	private final static transient Transformer transformer =
		defaultTemplates.newTransformer();
	/**
	 * Construct.
	 */
	public ApacheXSLT2() {
		super();
	}

	/**
	 * Process, Apache style.
	 */
	public void processXSLT(String xml, String stylesheet,
		OutputStream out) throws Exception {

		// Get a StringReader for the XML input.
		StreamSource xml_input=new StreamSource(new StringReader(xml));

		StreamSource styleSheetSource = new StreamSource(stylesheet);
		Templates defaultTemplates = tFactory.newTemplates(styleSheetSource);

		transformer.transform (xml_input, new StreamResult(out));
	}
}
