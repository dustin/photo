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

import net.spy.SpyObject;
import net.spy.util.ProgressStats;

import net.spy.photo.*;
import net.spy.photo.struts.SearchForm;

/**
 * Build a static web site of all images that can be viewed by a given
 * user.
 */
public class MakeStaticSite extends SpyObject {

	private PhotoUser pu=null;
	private SimpleDateFormat outFormat=null;
	private SimpleDateFormat dateFormat=null;
	private SimpleDateFormat tsFormat=null;

	private String destDir=null;
	private PhotoDimensions normaldim=null;

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

		// Store the images by date
		String imgdir=getImageDir(pid);

		String ext=pid.getFormat().getExtension();

		// Handle the regular file path
		File iFile=new File(imgdir + "/" + pid.getId() + ext);
		if(!iFile.exists()) {
			PhotoImage image=pih.getImage(normaldim);
			if(getLogger().isDebugEnabled()) {
				getLogger().debug("Need to get full image for " + pid);
			}
			FileOutputStream fos=new FileOutputStream(iFile);
			fos.write(image.getData());
			fos.close();
		}

		// Handle the thumbnail path
		File tnFile=new File(imgdir + "/" + pid.getId() + "_t" + ext);
		if(!tnFile.exists()) {
			if(getLogger().isDebugEnabled()) {
				getLogger().debug("Need to get thumbnail for " + pid);
			}
			PhotoImage thumb=pih.getThumbnail(pu.getId());
			FileOutputStream fos=new FileOutputStream(tnFile);
			fos.write(thumb.getData());
			fos.close();
		}
	}

	public void build() throws Exception {

		PhotoSessionData sessionData=new PhotoSessionData();

		sessionData.setUser(pu);
		sessionData.setOptimalDimensions(normaldim);

		SearchForm sf=new SearchForm();
		sf.setSdirection("desc");
		PhotoSearch ps=new PhotoSearch();
		PhotoSearchResults psr=ps.performSearch(sf, sessionData);

		// Save the XML
		System.out.println("Writing out XML");
		FileOutputStream fos=new FileOutputStream(destDir + "/index.xml");
		Search2XML s2x=Search2XML.getInstance();
		s2x.stream(psr, fos);
		fos.close();
		// Reset the cursor
		psr.set(0);
		System.out.println("Finished writing out XML");

		ProgressStats stats=new ProgressStats(psr.size());
		stats.start();
		for(Iterator i=psr.iterator(); i.hasNext(); ) {
			PhotoImageData result=(PhotoImageData)i.next();

			// Save the image
			saveImage(result);

			stats.stop();
			System.out.println(stats);
			stats.start();
		}
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
