// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: UploadImage.java,v 1.1 2002/06/25 05:21:35 dustin Exp $

package net.spy.photo.tools;

import java.util.*;
import java.net.*;
import java.io.*;
import java.text.*;

import org.apache.xmlrpc.*;

/**
 * Upload an image.
 */
public class UploadImage extends Object {

	private URL url=null;

	/**
	 * Get an instance of UploadImage.
	 *
	 * @param rpcUrl XML-RPC server that will be receiving the images.
	 */
	public UploadImage(URL rpcUrl) {
		super();
		this.url=rpcUrl;
	}

	/**
	 * Add an image.
	 *
	 * @param username username who will be uploading the image
	 * @param password password of the user who will be uploading the image
	 * @param keywords Image keywords
	 * @param info Description of the image
	 * @param category Name of the category into which the image goes
	 * @param taken Date the picture was taken
	 * @param imageData The actual image data to be stored.
	 */
	public int addImage(String username, String password,
			String keywords, String info, String category,
			Date taken, byte[] imageData)
			throws XmlRpcException, IOException {

		XmlRpcClient xmlrpc = new XmlRpcClient(url);

		Vector params = new Vector();
		Hashtable paramStruct=new Hashtable();
		paramStruct.put("username", username);
		paramStruct.put("password", password);
		paramStruct.put("keywords", keywords);
		paramStruct.put("info", info);
		paramStruct.put("category", category);
		paramStruct.put("taken", taken);
		paramStruct.put("image", imageData);

		params.addElement(paramStruct);

		// this method returns a string
		Integer result = (Integer)xmlrpc.execute("addImage.addImage", params);

		// return the new image id
		return(result.intValue());
	}

	private static void usage() throws Exception {
		System.err.println("UploadImage url username password "
			+ "keywords info category taken /path/to/image");
		throw new Exception("Usage error");
	}

	private static Date parseDate(String date) throws ParseException {
		SimpleDateFormat sdf=new SimpleDateFormat("MM/DD/yyyy");
		Date rv=sdf.parse(date);
		return(rv);
	}

	private static byte[] readData(File file) throws IOException {
		byte rv[]=new byte[(int)file.length()];
		FileInputStream fis=new FileInputStream(file);
		fis.read(rv);
		fis.close();
		return(rv);
	}

	/**
	 * Testing and what not.
	 */
	public static void main(String args[]) throws Exception {
		int i=0;
		if(args.length < 8) {
			usage();
		}
		URL url=new URL(args[i++]);
		String username=args[i++];
		String password=args[i++];
		String keywords=args[i++];
		String info=args[i++];
		String category=args[i++];
		Date taken=parseDate(args[i++]);

		// Get the image uploader
		UploadImage uploader=new UploadImage(url);

		// One call per image
		for(; i<args.length; i++) {
			// Get the data
			File image=new File(args[i]);
			byte data[]=readData(image);

			// Do the upload.
			int newid=uploader.addImage(username, password, keywords, info,
				category, taken, data);
			System.out.println("Added new image " + image + " as " + newid);
		}
	}
}
