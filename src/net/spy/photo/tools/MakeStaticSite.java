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

import net.spy.photo.*;
import net.spy.photo.struts.SearchForm;

/**
 * Build a static web site of all images that can be viewed by a given
 * user.
 */
public class MakeStaticSite extends Object {

	private PhotoUser pu=null;
	private SimpleDateFormat outFormat=null;

	/**
	 * Get an instance of MakeStaticSite.
	 */
	public MakeStaticSite(PhotoUser pu) {
		super();
		this.pu=pu;
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

	private String fixDate(Date d) throws Exception {
		return(outFormat.format(d));
	}

	public void build(String destDir, PhotoDimensions normaldim)
		throws Exception {

		PhotoSessionData sessionData=new PhotoSessionData();

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

		MakeStaticSite mss=new MakeStaticSite(pu);
		mss.build(destDir, maxdim);
	}

}
