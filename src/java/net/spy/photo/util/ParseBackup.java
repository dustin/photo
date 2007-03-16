// Copyright (c) 2000  Dustin Sallings <dustin@spy.net>

package net.spy.photo.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.zip.GZIPInputStream;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import net.spy.photo.PhotoException;
import net.spy.util.Base64;

/**
 * Restore image backups.
 */
public class ParseBackup extends DefaultHandler {

	private String prefix="";
	private LinkedList<String> nodes=new LinkedList<String>();
	private int tagnumber=0;

	private Restorable handler=null;

	/**
	 * Get the Restorable object.
	 */
	public Restorable getRestorable() {
		return(handler);
	}

	/**
	 * Marks the start of a document.
	 */
	@Override
	public void startDocument() {
		System.out.println("Starting!");
		handler=new AlbumRestorable();
	}

	/**
	 * Marks the end of a document.
	 */
	@Override
	public void endDocument() {
		System.out.println("Done!");
		System.out.println(handler);
	}

	/**
	 * Called for the beginning of a tag.
	 */
	@Override
	public void startElement(String uri, String name,
		String qName, Attributes atts) {

		tagnumber++;
		nodes.add(name);
		prefix="";
		for(String n : nodes) {
			prefix += "/" + n;
		}

		System.out.print("Found " + prefix);
		if(atts.getLength() > 0) {
			System.out.print(" (");
			for(int i=0; i<atts.getLength(); i++) {
				System.out.print("{");
				System.out.print(atts.getLocalName(i));
				System.out.print("=");
				System.out.print(atts.getValue(i));
				System.out.print("} ");
			}
			System.out.print(")");
		}
		// Newline
		System.out.println("");
	}

	/**
	 * Called after the end of an attribute.
	 */
	@Override
	public void endElement(String uri, String name, String qName) {
		System.out.println("Ending " + name);
		// Get that node out of the list.
		nodes.removeLast();
	}

	/**
	 * Actually capture the useful content.
	 */
	@Override
	public void characters(char ch[], int start, int length)
		throws SAXException {

		char chars[]=new char[length];
		System.arraycopy(ch, start, chars, 0, length);
		try {
			handler.addCharacters(prefix, chars);
		} catch(PhotoException pe) {
			throw new SAXException(pe);
		}
		/*
		for(int i=start; i < start + length; i++) {
			System.out.print(ch[i]);
		}
		*/
		System.out.println("");
	}

	/**
	 * Restore an image or a series of images.
	 */
	public static void main(String args[]) throws Exception {
		ParseBackup pr=new ParseBackup();

		// Get the XML reader
		XMLReader xr = XMLReaderFactory.createXMLReader();
		// Set the handlers
		xr.setContentHandler(pr);
		xr.setErrorHandler(pr);

		for(int i=0; i<args.length; i++) {
			// Open the stream
			FileInputStream fis=new FileInputStream(args[i]);
			GZIPInputStream gis=new GZIPInputStream(fis);
			InputStreamReader reader=new InputStreamReader(gis);

			//  Parse it
			xr.parse(new InputSource(reader));

			// Close the parts
			reader.close();
			gis.close();
			fis.close();

			// Get the restorable
			Restorable r=pr.getRestorable();

			// Create an output
			FileOutputStream fos=new FileOutputStream("/tmp/testout");
			Base64 base64=new Base64();
			fos.write(base64.decode(
				r.getContent("/photo_album_object/image_data/image_row")));
			fos.close();
		}
	}
}
