// Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoXML.java,v 1.2 2001/12/21 07:50:08 dustin Exp $

package net.spy.photo;

import java.util.*;
import net.spy.*;

public class PhotoXML extends Object {
	private static final String xmlheader="<?xml version=\"1.0\"?>\n";
	private String title=null;
	private Vector bodyparts=null;

	private String currentStylesheet=
		"http://bleu.west.spy.net/~dustin/jphoto/inc/xsl/default.xsl";

	public PhotoXML() {
		super();
		bodyparts=new Vector();
	}

	public String toString() {
		StringBuffer sb=new StringBuffer();

		// Add the XML header.
		sb.append(xmlheader);
		sb.append("<?xml-stylesheet type=\"text/xsl\" href=\""
			+ currentStylesheet + "\"?>\n");
		sb.append("<page>\n");
		// Add a title if we've got one.
		if(title!=null) {
			sb.append("\t<heading>\n");
			sb.append("\t\t<title>");
			sb.append(normalize(title, true));
			sb.append("\t\t</title>\n");
			sb.append("\t</heading>\n");
		}
		for(Enumeration e=bodyparts.elements(); e.hasMoreElements(); ) {
			sb.append((String)e.nextElement());
			sb.append("\n");
		}
		sb.append("</page>\n");
		return(sb.toString());
	}

	public void setTitle(String title) {
		this.title=title;
	}

	public void addBodyPart(String body) {
		bodyparts.addElement(body);
	}

	public static String normalize(String xml, boolean canonical) {
		return(PhotoXSLT.normalize(xml, canonical));
	}
}
