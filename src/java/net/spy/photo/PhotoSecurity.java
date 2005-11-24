// Copyright (c) 1999 Dustin Sallings <dustin@spy.net>
// arch-tag: 3DDF1592-5D6D-11D9-806B-000A957659CC

package net.spy.photo;

import java.security.MessageDigest;
import java.util.Collection;

import net.spy.SpyObject;
import net.spy.util.Base64;

/**
 * Security dispatch type stuff happens here.
 */
public class PhotoSecurity extends SpyObject {

	private UserFactory userFactory=null;

	/**
	 * Get a PhotoSecurity object.
	 */
	public PhotoSecurity() {
		super();
		userFactory=UserFactory.getInstance();
	}

	/**
	 * Get a digest for a given string.
	 */
	public String getDigest(String input) {
		// We need the trim so we can ignore whitespace.
		byte dataB[]=input.trim().getBytes();
		PhotoConfig conf=PhotoConfig.getInstance();
		MessageDigest md = null;
		try {
			md=MessageDigest.getInstance(conf.get("cryptohash"));
		} catch(Exception e) {
			throw new RuntimeException("Could not instantiate "
				+ conf.get("cryptohash"));
		}
		md.update(dataB);
		Base64 base64=new Base64();
		String out = base64.encode(md.digest());
		out = out.replace('+', '/');
		out=out.trim();
		// Get rid of = signs.
		while(out.endsWith("=")) {
			out=out.substring(0,out.length()-1);
		}
		return(out.trim());
	}

	// Get the default user
	User getDefaultUser() {
		User ret=null;

		// Get the username from the config
		PhotoConfig conf=PhotoConfig.getInstance();
		String username=conf.get("default_user", "guest");

		try {
			ret=userFactory.getUser(username);
		} catch(PhotoUserException e) {
			// Ignore, return null
			getLogger().debug("Can't get default user", e);
		}

		return(ret);
	}

	/**
	 * Get a user by integer ID.
	 */
	public User getUser(int id) throws PhotoUserException {
		return(userFactory.getUser(id));
	}

	/** 
	 * Get a user by username or email address.
	 */
	public User getUser(String spec) throws PhotoUserException {
		return(userFactory.getUser(spec));
	}

	/** 
	 * Test to see if a user has access to an image (but don't throw an
	 * exception).
	 */
	public boolean testAccess(User user, int imageId) {
		boolean ok=false;
		try {
			PhotoImageDataFactory pidf=PhotoImageDataFactory.getInstance();
			PhotoImageData pid=pidf.getObject(imageId);
			ok=user.canView(pid.getCatId());

			if(!ok) {
				User u=PhotoUtil.getDefaultUser();
				ok=u.canView(pid.getCatId());
			}
		} catch(Exception e) {
			// Will return false
			getLogger().warn("Problem validating user " + user
				+ "'s access to image " + imageId, e);
		}
		return(ok);
	}

	/**
	 * The preferred way to check a user's access to an image.
	 */
	public void checkAccess(User user, int imageId) throws Exception {
		if(!testAccess(user, imageId)) {
			throw new Exception("Access to image " + imageId
				+ " is not allowed by user " + user);
		}
	}

	/**
	 * Check to see if the given uid has access to the given image ID.
	 *
	 * <b>Note</b>:  This is not generally the right way to do this.
	 */
	public void checkAccess(int uid, int imageId) throws Exception {
		PhotoSecurity sec=new PhotoSecurity();
		User u=sec.getUser(uid);

		checkAccess(u, imageId);
	}

	/** 
	 * Check the password against the given hash.
	 */
	public boolean checkPassword(String pass, String hash) {
		boolean rv=false;
		String tpw=getDigest(pass);
		rv=tpw.equals(hash);
		return(rv);
	}

	/**
	 * List all users in alphabetical order by username.
	 *
	 * @return an List of User objects
	 */
	public Collection<User> getAllUsers() throws Exception {
		return(userFactory.getAllUsers());
	}
}
