// Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoXML.java,v 1.1 2000/12/27 06:05:25 dustin Exp $

package net.spy.photo;

import java.util.*;
import net.spy.*;

public class PhotoXML extends Object {
	protected static final String xmlheader="<?xml version=\"1.0\"?>\n";
	protected String title=null;
	protected Vector bodyparts=null;

	public PhotoXML() {
		super();
		bodyparts=new Vector();
	}

	public String toString() {
		StringBuffer sb=new StringBuffer();

		// Add the XML header.
		sb.append(xmlheader);
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
