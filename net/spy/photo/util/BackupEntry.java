/*
 * Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
 *
 * $Id: BackupEntry.java,v 1.4 2000/11/29 07:02:19 dustin Exp $
 */

package net.spy.photo.util;

import java.sql.*;
import java.util.*;
import java.io.*;

import org.w3c.dom.*;
import org.xml.sax.*;

import org.apache.xml.serialize.*;
import org.apache.xerces.dom.DocumentImpl;

import net.spy.*;
import net.spy.photo.*;

public class BackupEntry extends Object implements Serializable {

	// Data stored here.
	protected Document doc=null;

	// Node type
	protected String nodeType="unknown_node_type";

	public BackupEntry() throws Exception {
		super();
		doc=new DocumentImpl();
	}

	public BackupEntry(Node n) throws Exception {
		super();
		doc=new DocumentImpl();
		doc.appendChild(n);
	}

	public void writeTo(OutputStream o) throws Exception {
		OutputFormat format = new OutputFormat(doc);
		format.setIndenting(true);
		XMLSerializer serial = new XMLSerializer(o, format);
		serial.asDOMSerializer();
		serial.serialize(doc.getDocumentElement());
	}

}
