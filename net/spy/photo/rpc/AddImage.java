// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: AddImage.java,v 1.4 2002/07/10 03:38:08 dustin Exp $

package net.spy.photo.rpc;

import java.util.Date;
import java.util.Hashtable;

import net.spy.photo.Category;
import net.spy.photo.Persistent;
import net.spy.photo.PhotoException;
import net.spy.photo.PhotoImage;
import net.spy.photo.PhotoLogUploadEntry;
import net.spy.photo.PhotoSaver;
import net.spy.photo.PhotoUser;

/**
 * RPC method to add an image. 
 */
public class AddImage extends RPCMethod {

	/**
	 * Get an instance of AddImage.
	 */
	public AddImage() {
		super();
	}

	/**
	 * Add an image.  The following arguments are required:
	 *
	 * <ul>
	 *  <li>username - username of the user wanting to store the image</li>
	 *  <li>password - user's password</li>
	 *  <li>keywords - keywords for the image</li>
	 *  <li>info - description of the image</li>
	 *  <li>category - name of the category in which to store the image</li>
	 *  <li>taken - date the image was taken</li>
	 *  <li>image - the image itself</li>
	 * </ul>
	 */
	public int addImage(Hashtable args) throws PhotoException {
		// Verify all the arguments.
		checkArg(args, "username", String.class);
		checkArg(args, "password", String.class);
		checkArg(args, "keywords", String.class);
		checkArg(args, "info", String.class);
		checkArg(args, "category", String.class);
		checkArg(args, "taken", Date.class);
		checkArg(args, "image", byte[].class);

		int rv=-1;

		// OK, now go ahead and pull out all of the args
		String username=(String)args.get("username");
		String password=(String)args.get("password");
		String keywords=(String)args.get("keywords");
		String info=(String)args.get("info");
		String category=(String)args.get("category");
		Date taken=(Date)args.get("taken");
		byte image[]=(byte[])args.get("image");

		// Authenticate the user
		authenticate(username, password);

		// Look up the category.
		int catId=-1;
		try {
			Category cat=Category.lookupCategory(category);
			catId=cat.getId();
		} catch(Exception e) {
			throw new PhotoException("Error looking up category.", e);
		}

		// Verify the user is allowed to add to this category
		PhotoUser user=getUser();
		if(!user.canAdd(catId)) {
			throw new PhotoException("User is not allowed to add to "
				+ category);
		}

		// Create a PhotoImage from the raw data.
		PhotoImage photoImage=new PhotoImage(image);

		// Get the new image ID
		rv=PhotoSaver.getNewImageId();
		// Get the new saver
		PhotoSaver saver=new PhotoSaver();
		// Populate the fields.
		saver.setKeywords(keywords);
		saver.setInfo(info);
		saver.setCat(catId);
		saver.setTaken(taken);
		saver.setUser(user);
		saver.setPhotoImage(photoImage);
		saver.setId(rv);

		// Now, queue it up for saving.
		Persistent.getPhotoSaverThread().saveImage(saver);

		// Log it.
		// XXX  I really would like a way to get to the actual remote IP
		// address.
		Persistent.getLogger().log(new PhotoLogUploadEntry(
			user.getId(), rv, "127.0.0.1", "XMLRPC Image Upload"));

		// Return the new image ID
		return(rv);
	}

}
