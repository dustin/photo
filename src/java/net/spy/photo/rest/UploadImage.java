// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>

package net.spy.photo.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.Category;
import net.spy.photo.CategoryFactory;
import net.spy.photo.Persistent;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoException;
import net.spy.photo.PhotoImageFactory;
import net.spy.photo.User;
import net.spy.photo.impl.SavablePhotoImage;
import net.spy.photo.log.PhotoLogUploadEntry;
import net.spy.photo.util.MetaDataExtractor;

public class UploadImage extends BaseRestServlet {

	private static final long RECACHE_DELAY=120000;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException {
		try {
			handlePost(req, res);
		} catch(ServletException e) {
			throw e;
		} catch(IOException e) {
			throw e;
		} catch(RuntimeException e) {
			throw e;
		} catch(Exception e) {
			throw new ServletException("Problem handling put request", e);
		}
	}
	
	private void handlePost(HttpServletRequest req, HttpServletResponse res)
		throws Exception {

		String category=getStringParameter(req, "category");
		String keywords=getStringParameter(req, "keywords");
		String info=getStringParameter(req, "info");

		// Look up the category.
		int catId=-1;
		try {
			CategoryFactory cf=CategoryFactory.getInstance();
			Category cat=cf.getCategory(category);
			catId=cat.getId();
		} catch(Exception e) {
			throw new PhotoException("Error looking up category.", e);
		}

		// Verify the user is allowed to add to this category
		User user=(User)req.getUserPrincipal();
		if(!user.canAdd(catId)) {
			throw new PhotoException("User is not allowed to add to "
				+ category);
		}

		// Create a PhotoImage from the raw data.
		int contentLength=req.getContentLength();
		assert contentLength > 0 : "no content!";
		byte image[]=new byte[contentLength];
		int toRead=contentLength;
		InputStream is=req.getInputStream();
		while(toRead > 0) {
			int read=is.read(image, contentLength-toRead, toRead);
			if(read < 0) {
				throw new IOException("Ran out of stuff to read");
			}
			toRead -= read;
		}
		is.close();

		// Find the date
		Date taken=null;
		if(req.getParameter("taken") != null) {
			taken=getDateParameter(req, "taken");
		} else {
			taken=MetaDataExtractor.getInstance().getDateTaken(image);
		}
		assert taken != null : "Couldn't find taken date";

		// Get the new image ID
		SavablePhotoImage savable=new SavablePhotoImage(image);
		// Populate the fields.
		savable.setKeywords(keywords);
		savable.setDescr(info);
		savable.setCatId(catId);
		savable.setTaken(taken);
		savable.setAddedBy(user);

		PhotoImageFactory pidf=PhotoImageFactory.getInstance();
		pidf.store(savable, true, RECACHE_DELAY);

		// Log it.
		Persistent.getPipeline().addTransaction(new PhotoLogUploadEntry(
			user.getId(), savable.getId(), req.getRemoteAddr(),
			"REST Image Upload"), PhotoConfig.getInstance());

		// Return the new image ID
		res.setStatus(HttpServletResponse.SC_ACCEPTED);
		sendPlain(String.valueOf(savable.getId()), res);
	}

	
}
