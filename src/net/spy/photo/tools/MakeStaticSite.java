// Copyright (c) 2003  Dustin Sallings <dustin@spy.net>
//
// $Id: MakeStaticSite.java,v 1.2 2003/12/02 03:34:58 dustin Exp $

package net.spy.photo.tools;

import java.util.Iterator;
import java.util.Date;
import java.text.SimpleDateFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;

import org.apache.xerces.dom.DOMImplementationImpl;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import net.spy.util.ProgressStats;

import net.spy.photo.*;
import net.spy.photo.struts.SearchForm;

/**
 * Build a static web site of all images that can be viewed by a given
 * user.
 */
public class MakeStaticSite extends Object {

	private PhotoUser pu=null;
	private SimpleDateFormat outFormat=null;
	private SimpleDateFormat dateFormat=null;
	private SimpleDateFormat tsFormat=null;

	private String destDir=null;
	private PhotoDimensions normaldim=null;

	private Document doc=null;
	private Element root=null;

	/**
	 * Get an instance of MakeStaticSite.
	 */
	public MakeStaticSite(PhotoUser pu, String dir, PhotoDimensions dim) {

		super();
		this.pu=pu;
		this.destDir=dir;
		this.normaldim=dim;
		outFormat=new SimpleDateFormat("yyyyMMdd");
		dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		tsFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		DOMImplementationImpl dom=new DOMImplementationImpl();
		doc=dom.createDocument(null, "album", null);
		root=doc.getDocumentElement();
	}

	private String getExtension(PhotoImage image) {
		String rv=".huh";
		switch(image.getFormat()) {
			case PhotoImage.FORMAT_JPEG:
				rv=".jpg";
				break;
			case PhotoImage.FORMAT_PNG:
				rv=".png";
				break;
			case PhotoImage.FORMAT_GIF:
				rv=".gif";
				break;
		}
		return(rv);
	}

	private String fixDate(Date d) throws Exception {
		return(outFormat.format(d));
	}

	private String getImageDir(PhotoImageData pid) throws Exception {
		String rv=destDir + "/images/" + fixDate(pid.getTaken());
		File f=new File(rv);
		f.mkdirs();

		return(rv);
	}

	private void saveImage(PhotoImageData pid) throws Exception {

		PhotoImageHelper pih=new PhotoImageHelper(pid.getId());

		PhotoImage thumb=pih.getThumbnail(pu.getId());
		PhotoImage image=pih.getImage(normaldim);
		// System.out.println("Got " + thumb + " and " + image);

		// Store the images by date
		String imgdir=getImageDir(pid);

		FileOutputStream fos=new FileOutputStream(imgdir + "/"
			+ pid.getId() + getExtension(image));
		fos.write(image.getData());
		fos.close();
		fos=new FileOutputStream(imgdir + "/" + pid.getId() + "_t"
			+ getExtension(thumb));
		fos.write(thumb.getData());
		fos.close();
	}

	private void saveTextMeta(PhotoImageData pid) throws Exception {

		String imgdir=getImageDir(pid);

		FileWriter fr=new FileWriter(imgdir + "/" + pid.getId() + "_k.txt");
		fr.write(pid.getKeywords());
		fr.close();

		fr=new FileWriter(imgdir + "/" + pid.getId() + "_d.txt");
		fr.write(pid.getDescr());
		fr.close();
	}

	private Element addNode(String name, String val) {
		Element el=doc.createElement(name);
		el.appendChild(doc.createTextNode(val));
		return(el);
	}

	private Element addNode(String name, int val) {
		return(addNode(name, String.valueOf(val)));
	}

	private Element addDate(String name, Date d) {
		return(addNode(name, dateFormat.format(d)));
	}

	private Element addTimestamp(String name, Date d) {
		return(addNode(name, tsFormat.format(d)));
	}

	private void saveXmlMeta(PhotoImageData pid) throws Exception {
		Element el=doc.createElement("photo");
		root.appendChild(el);

		el.appendChild(addNode("id", pid.getId()));
		el.appendChild(addNode("cat", pid.getCatName()));
		el.appendChild(addDate("taken", pid.getTaken()));
		el.appendChild(addTimestamp("ts", pid.getTimestamp()));
		el.appendChild(addNode("keywords", pid.getKeywords()));
		el.appendChild(addNode("descr", pid.getDescr()));
		el.appendChild(addNode("size", pid.getSize()));
		el.appendChild(addNode("width", pid.getDimensions().getWidth()));
		el.appendChild(addNode("height", pid.getDimensions().getHeight()));
		el.appendChild(addNode("tnwidth", pid.getTnDims().getWidth()));
		el.appendChild(addNode("tnheight", pid.getTnDims().getHeight()));
	}

	public void build() throws Exception {

		PhotoSessionData sessionData=new PhotoSessionData();

		sessionData.setUser(pu);
		sessionData.setOptimalDimensions(normaldim);

		SearchForm sf=new SearchForm();
		sf.setSdirection("desc");
		PhotoSearch ps=new PhotoSearch();
		PhotoSearchResults psr=ps.performSearch(sf, sessionData);

		ProgressStats stats=new ProgressStats(psr.size());

		stats.start();
		for(Iterator i=psr.iterator(); i.hasNext(); ) {
			PhotoImageData result=(PhotoImageData)i.next();

			// Save the image
			saveImage(result);

			// Save the textual meta data
			saveTextMeta(result);

			// Save the XML version
			saveXmlMeta(result);

			stats.stop();
			System.out.println(stats);
			stats.start();
		}

		// Save the XML
		FileOutputStream fos=new FileOutputStream(destDir + "/index.xml");

		OutputFormat format = new OutputFormat(doc);
		format.setIndenting(true);
		XMLSerializer serial = new XMLSerializer(fos, format);
		serial.asDOMSerializer();
		serial.serialize(root);

		fos.close();
	}

	/** 
	 * First arg is the username.
	 */
	public static void main(String args[]) throws Exception {
		if(args.length < 2) {
			String err="Arguments required:  username destDir [max dims]";
			System.err.println(err);
			throw new Exception(err);
		}
		String username=args[0];
		String destDir=args[1];
		PhotoDimensions maxdim=new PhotoDimensionsImpl("800x600");
		if(args.length > 2) {
			maxdim=new PhotoDimensionsImpl(args[2]);
		}

		PhotoUser pu=PhotoUser.getPhotoUser(username);
		System.out.println("Creating site for " + pu + " max size:  " + maxdim);

		MakeStaticSite mss=new MakeStaticSite(pu, destDir, maxdim);
		mss.build();
	}

}
