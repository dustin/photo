// Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
// $Id: ResinXSLT.java,v 1.1 2001/12/29 06:19:40 dustin Exp $

package net.spy.photo.xslt;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import net.spy.SpyUtil;
import net.spy.cache.*;

import net.spy.photo.*;

// Resin XSL
import com.caucho.transform.*;
import com.caucho.xsl.*;

/**
 * Perform XSLT translations and stuff.
 */
public class ResinXSLT extends PhotoXSLT {

	/**
	 * Get an instance.
	 */
	public ResinXSLT() {
		super();
	}

	// Resin version of the sender.
	public void processXSLT(String xml, String stylesheet,
		OutputStream out) throws Exception {

		// Get a stylesheet factory
		StylesheetFactory factory=new Xsl();
		Stylesheet style=factory.newStylesheet(stylesheet);

		StreamTransformer transformer=style.newStreamTransformer();
		transformer.transformString(xml, out);
	}
}
