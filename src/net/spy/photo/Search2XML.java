// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>

package net.spy.photo;

import java.io.OutputStream;
import java.util.Iterator;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.apache.xerces.dom.DOMImplementationImpl;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import net.spy.SpyObject;

/**
 * Convert photo search results to XML.
 */
public class Search2XML extends SpyObject {

	private DOMImplementationImpl dom=null;

	private SimpleDateFormat outFormat=null;
	private SimpleDateFormat dateFormat=null;
	private SimpleDateFormat tsFormat=null;

	private static Search2XML instance=null;

	/**
	 * Get an instance of Search2XML.
	 */
	private Search2XML() {
		super();

		outFormat=new SimpleDateFormat("yyyyMMdd");
		dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		tsFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		dom=new DOMImplementationImpl();
	}

	/** 
	 * Get the singleton Search2XML instance.
	 */
	public static synchronized Search2XML getInstance() {
		if(instance == null) {
			instance = new Search2XML();
		}
		return(instance);
	}

	private Element addNode(Document doc, String name, String val) {
		Element el=doc.createElement(name);
		el.appendChild(doc.createTextNode(val));
		return(el);
	}

	private Element addNode(Document doc, String name, int val) {
		return(addNode(doc, name, String.valueOf(val)));
	}

	private Element addDate(Document doc, String name, Date d) {
		return(addNode(doc, name, dateFormat.format(d)));
	}

	private Element addTimestamp(Document doc, String name, Date d) {
		return(addNode(doc, name, tsFormat.format(d)));
	}

	private void stream(PhotoImageData pid, OutputStream os) throws Exception {
		Document doc=dom.createDocument(null, "photo", null);
		Element root=doc.getDocumentElement();

		Element el=root;

		el.appendChild(addNode(doc, "id", pid.getId()));
		el.appendChild(addNode(doc, "addedby", pid.getAddedBy().getUsername()));
		el.appendChild(addNode(doc, "cat", pid.getCatName()));
		el.appendChild(addDate(doc, "taken", pid.getTaken()));
		el.appendChild(addTimestamp(doc, "ts", pid.getTimestamp()));
		el.appendChild(addNode(doc, "keywords", pid.getKeywords()));
		el.appendChild(addNode(doc, "descr", pid.getDescr()));
		el.appendChild(addNode(doc, "size", pid.getSize()));
		el.appendChild(addNode(doc, "width", pid.getDimensions().getWidth()));
		el.appendChild(addNode(doc, "height", pid.getDimensions().getHeight()));
		el.appendChild(addNode(doc, "tnwidth", pid.getTnDims().getWidth()));
		el.appendChild(addNode(doc, "tnheight", pid.getTnDims().getHeight()));
		el.appendChild(addNode(doc, "extension",
			pid.getFormat().getExtension()));

		OutputFormat format = new OutputFormat(doc);
		format.setIndenting(true);
		format.setOmitDocumentType(true);
		format.setOmitXMLDeclaration(true);
		XMLSerializer serial = new XMLSerializer(os, format);
		serial.asDOMSerializer();
		serial.serialize(root);
	}

	public void stream(PhotoSearchResults psr, OutputStream os)
		throws Exception {

		os.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes());
		os.write("<album>".getBytes());
		for(Iterator i=psr.iterator(); i.hasNext();) {
			PhotoImageData result=(PhotoImageData)i.next();
			stream(result, os);
		}
		os.write("</album>".getBytes());
	}

}
