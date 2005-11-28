// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: A484B354-5D6D-11D9-9EE7-000A957659CC

package net.spy.photo.rpc;

import java.util.Date;
import java.util.Hashtable;

import net.spy.photo.Category;
import net.spy.photo.CategoryFactory;
import net.spy.photo.Persistent;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoException;
import net.spy.photo.PhotoImage;
import net.spy.photo.PhotoImageDataFactory;
import net.spy.photo.User;
import net.spy.photo.impl.SavablePhotoImageData;
import net.spy.photo.log.PhotoLogUploadEntry;

/**
 * RPC method to add an image. 
 */
public class AddImage extends RPCMethod {

	private static final long RECACHE_DELAY=120000;

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
	public int addImage(Hashtable args) throws Exception {
		// Verify all the arguments.
		checkArg(args, "username", String.class);
		checkArg(args, "password", String.class);
		checkArg(args, "keywords", String.class);
		checkArg(args, "info", String.class);
		checkArg(args, "category", String.class);
		checkArg(args, "taken", Date.class);
		checkArg(args, "image", byte[].class);

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
			CategoryFactory cf=CategoryFactory.getInstance();
			Category cat=cf.getCategory(category);
			catId=cat.getId();
		} catch(Exception e) {
			throw new PhotoException("Error looking up category.", e);
		}

		// Verify the user is allowed to add to this category
		User user=getUser();
		if(!user.canAdd(catId)) {
			throw new PhotoException("User is not allowed to add to "
				+ category);
		}

		// Create a PhotoImage from the raw data.
		PhotoImage photoImage=new PhotoImage(image);

		// Get the new image ID
		SavablePhotoImageData savable=new SavablePhotoImageData(photoImage);
		// Populate the fields.
		savable.setKeywords(keywords);
		savable.setDescr(info);
		savable.setCatId(catId);
		savable.setTaken(taken);
		savable.setAddedBy(user);

		PhotoImageDataFactory pidf=PhotoImageDataFactory.getInstance();
		pidf.store(savable, true, RECACHE_DELAY);

		// Log it.
		// XXX  I really would like a way to get to the actual remote IP
		// address.
		Persistent.getPipeline().addTransaction(new PhotoLogUploadEntry(
			user.getId(), savable.getId(), "127.0.0.1", "XMLRPC Image Upload"),
			PhotoConfig.getInstance());

		// Return the new image ID
		return(savable.getId());
	}

}
