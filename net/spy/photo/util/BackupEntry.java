/*
 * Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
 *
 * $Id: BackupEntry.java,v 1.3 2000/11/28 09:52:11 dustin Exp $
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
	protected Hashtable ht=null;

	// Node type
	protected String nodeType="unknown_node_type";

	public BackupEntry() throws Exception {
		super();
		ht=new Hashtable();
	}

	public BackupEntry(Node n) throws Exception {
		super();
		ht=new Hashtable();
		initFromNode(n);

		System.out.println("This BackupEntry:\n" + ht);
	}

	public void writeTo(OutputStream o) throws Exception {
		Document doc= new DocumentImpl();

		// Create a node of the type we're making.
		Element root = doc.createElement(nodeType);

		for(Enumeration e=ht.keys(); e.hasMoreElements(); ) {
			String k=(String)e.nextElement();
			String v=(String)ht.get(k);
			Element el=doc.createElement(k);
			el.appendChild( doc.createTextNode(v) );
			root.appendChild(el);
		}

		doc.appendChild(root);

		OutputFormat format = new OutputFormat(doc);
		format.setIndenting(true);
		XMLSerializer serial = new XMLSerializer(o, format);
		serial.asDOMSerializer();
		serial.serialize(doc.getDocumentElement());
	}

	// We have the node, now populate our hash table with each of the
	// element nodes found under this node.
	private void initFromNode(Node n) throws Exception {
		NodeList nl=n.getChildNodes();
		for(int i=0; i<nl.getLength(); i++) {
			Node child=nl.item(i);
			if(child.getNodeType()==Node.ELEMENT_NODE) {
				saveChild(child);
			}
		}
	}

	// Extract the key and value from the child Node.
	private void saveChild(Node n) throws Exception {
		String key=n.getNodeName();
		StringBuffer v=new StringBuffer();

		NodeList nl=n.getChildNodes();
		for(int i=0; i<nl.getLength(); i++) {
			Node child=nl.item(i);
			if(child.getNodeType()==Node.TEXT_NODE) {
				v.append(child.getNodeValue());
			}
		}

		// Save the key/value pair.
		ht.put(key, v.toString());
	}
}
