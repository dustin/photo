// Copyright (c) 2003  Dustin Sallings <dustin@spy.net>
//
// $Id: MakeStaticSite.java,v 1.1 2003/06/02 00:40:33 dustin Exp $

package net.spy.photo.tools;

import java.util.Iterator;
import java.util.Date;
import java.text.SimpleDateFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;

import net.spy.photo.*;
import net.spy.photo.struts.SearchForm;

/**
 * Build a static web site of all images that can be viewed by a given
 * user.
 */
public class MakeStaticSite extends Object {

	private PhotoUser pu=null;
	private SimpleDateFormat inFormat=null;
	private SimpleDateFormat outFormat=null;

	/**
	 * Get an instance of MakeStaticSite.
	 */
	public MakeStaticSite(PhotoUser pu) {
		super();
		this.pu=pu;
		inFormat=new SimpleDateFormat("yyyy-MM-dd");
		outFormat=new SimpleDateFormat("yyyyMMdd");
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

	private String fixDate(String d) throws Exception {
		return(outFormat.format(inFormat.parse(d)));
	}

	public void build(String destDir) throws Exception {
		PhotoSessionData sessionData=new PhotoSessionData();

		PhotoDimensions normaldim=new PhotoDimensionsImpl("800x600");

		sessionData.setUser(pu);
		sessionData.setOptimalDimensions(normaldim);

		SearchForm sf=new SearchForm();
		PhotoSearch ps=new PhotoSearch();
		PhotoSearchResults psr=ps.performSearch(sf, sessionData);

		System.out.println(psr);
		for(Iterator i=psr.iterator(); i.hasNext(); ) {
			PhotoImageData result=(PhotoImageData)i.next();
			System.out.println(result);
			PhotoImageHelper pih=new PhotoImageHelper(result.getId());

			PhotoImage thumb=pih.getThumbnail(pu.getId());
			PhotoImage image=pih.getImage(normaldim);
			System.out.println("Got " + thumb + " and " + image);

			// Store the images by date
			String imgdir=destDir + "/images/" + fixDate(result.getTaken());
			File f=new File(imgdir);
			f.mkdirs();

			FileOutputStream fos=new FileOutputStream(imgdir + "/"
				+ result.getId() + getExtension(image));
			fos.write(image.getData());
			fos.close();
			fos=new FileOutputStream(imgdir + "/" + result.getId() + "_t"
				+ getExtension(thumb));
			fos.write(thumb.getData());
			fos.close();

			FileWriter fr=new FileWriter(imgdir + "/"
				+ result.getId() + "_k.txt");
			fr.write(result.getKeywords());
			fr.close();

			fr=new FileWriter(imgdir + "/"
				+ result.getId() + "_d.txt");
			fr.write(result.getDescr());
			fr.close();
		}
	}

	/** 
	 * First arg is the username.
	 */
	public static void main(String args[]) throws Exception {
		PhotoUser pu=PhotoUser.getPhotoUser(args[0]);
		System.out.println("Creating site for " + pu);
		MakeStaticSite mss=new MakeStaticSite(pu);
		mss.build("/tmp/thesite");
	}

}
