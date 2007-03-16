// Copyright (c) 2000  Dustin Sallings <dustin@spy.net>

package net.spy.photo.util;

import java.io.OutputStream;
import java.io.Serializable;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Superclass of all backup entries.
 */
public abstract class BackupEntry extends Object implements Serializable {

	// Data stored here.
	protected Document doc=new DocumentImpl();
	protected Element myData=null;

	// Node type
	private String nodeType="unknown_node_type";

	protected BackupEntry() {
		super();
	}

	/**
	 * Get an instance of a BackupEntry for a given Node.
	 */
	protected BackupEntry(Node n) throws Exception {
		super();
		doc=n.getOwnerDocument();
		myData=(Element)n;
	}

	/**
	 * Set the type of this node.
	 */
	protected void setNodeType(String to) {
		this.nodeType=to;
	}

	/**
	 * Get the node type.
	 */
	public String getNodeType() {
		return(nodeType);
	}

	/**
	 * Restore a backup entry to the DB.
	 */
	public abstract void restore() throws Exception;

	/**
	 * Write this entry to the given output stream.
	 */
	public void writeTo(OutputStream o) throws Exception {
		OutputFormat format = new OutputFormat(doc);
		format.setIndenting(true);
		XMLSerializer serial = new XMLSerializer(o, format);
		serial.asDOMSerializer();
		serial.serialize(doc.getDocumentElement());
	}

}
