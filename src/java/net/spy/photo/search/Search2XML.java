// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>
// arch-tag: 4F839A3B-5D6D-11D9-95E5-000A957659CC

package net.spy.photo.search;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.org.apache.xerces.internal.dom.DOMImplementationImpl;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import net.spy.SpyObject;
import net.spy.photo.Keyword;
import net.spy.photo.PhotoImageData;

/**
 * Convert photo search results to XML.
 */
public class Search2XML extends SpyObject {

	private DOMImplementationImpl dom=null;

	private SimpleDateFormat dateFormat=null;
	private SimpleDateFormat tsFormat=null;

	private static Search2XML instance=null;

	/**
	 * Get an instance of Search2XML.
	 */
	private Search2XML() {
		super();

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

	private Element addKeywords(Document doc, String name, Collection c) {
		Element el=doc.createElement(name);
		for(Iterator i=c.iterator(); i.hasNext();) {
			Keyword k=(Keyword)i.next();
			Element kwel=doc.createElement("keyword");
			kwel.setAttribute("id", String.valueOf(k.getId()));
			el.appendChild(kwel);
		}
		return(el);
	}

	private void stream(PhotoImageData pid, OutputStream os) throws Exception {
		Document doc=dom.createDocument(null, "photo", null);
		Element root=doc.getDocumentElement();

		Element el=root;

		el.appendChild(addNode(doc, "id", pid.getId()));
		el.appendChild(addNode(doc, "addedby", pid.getAddedBy().getName()));
		el.appendChild(addNode(doc, "cat", pid.getCatName()));
		el.appendChild(addDate(doc, "taken", pid.getTaken()));
		el.appendChild(addTimestamp(doc, "ts", pid.getTimestamp()));
		el.appendChild(addKeywords(doc, "keywords", pid.getKeywords()));
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

	private void kw2xml(Set kwset, OutputStream os) throws Exception {
		Document doc=dom.createDocument(null, "keywordmap", null);
		Element root=doc.getDocumentElement();

		Element el=root;

		for(Iterator i=kwset.iterator(); i.hasNext(); ) {
			Keyword kw=(Keyword)i.next();
			Element kwel=doc.createElement("keyword");
			kwel.setAttribute("id", String.valueOf(kw.getId()));
			kwel.setAttribute("word", kw.getKeyword());
			el.appendChild(kwel);
		}

		OutputFormat format = new OutputFormat(doc);
		format.setIndenting(true);
		format.setOmitDocumentType(true);
		format.setOmitXMLDeclaration(true);
		XMLSerializer serial = new XMLSerializer(os, format);
		serial.asDOMSerializer();
		serial.serialize(root);
	}

	public void stream(SearchResults psr, OutputStream os)
		throws Exception {

		os.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes());
		os.write("<photoexport>\n".getBytes());

		Set<Keyword> keywords=new HashSet<Keyword>();
		for(PhotoImageData result : psr) {
			keywords.addAll(result.getKeywords());
		}

		kw2xml(keywords, os);

		// yeah, this is lame, we do two passes through here to get all of the
		// keywords and stuff
		psr.set(0);
		os.write("<album>\n".getBytes());
		for(PhotoImageData result : psr) {
			stream(result, os);
		}

		os.write("</album>\n".getBytes());
		os.write("</photoexport>\n".getBytes());
	}

}