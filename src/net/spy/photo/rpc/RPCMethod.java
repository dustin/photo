// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: RPCMethod.java,v 1.2 2002/07/10 03:38:08 dustin Exp $

package net.spy.photo.rpc;

import java.util.Hashtable;

import net.spy.rpc.services.Remote;

import net.spy.photo.Persistent;
import net.spy.photo.PhotoException;
import net.spy.photo.PhotoUser;

/**
 * Superclass for all RPC handlers.
 */
public abstract class RPCMethod extends Remote {

	private PhotoUser user=null;

	/**
	 * Get an instance of RPCMethod.
	 */
	public RPCMethod() {
		super();
	}

	/**
	 * Get the PhotoUser associated with this call.
	 *
	 * @return the user, or null if a user has not been authenticated
	 */
	protected PhotoUser getUser() {
		return(user);
	}

	/**
	 * Authenticate the user.
	 */
	protected void authenticate(String username, String password)
		throws PhotoException {

		// Get the user
		PhotoUser tmp=Persistent.getSecurity().getUser(username);
		if(tmp!=null && tmp.checkPassword(password)) {
			user=tmp;
		} else {
			throw new PhotoException("Invalid username or password.");
		}
	}

	/**
	 * Verify an argument in an xmlrpc struct.
	 */
	protected void checkArg(Hashtable args, String which, Class type)
		throws PhotoException {

		if(args==null) {
			throw new PhotoException("Null argument list sent in.");
		}

		if(which==null) {
			throw new PhotoException("Null parameter to check.");
		}

		if(type==null) {
			throw new PhotoException("Null class to verify.");
		}

		Object o=args.get(which);
		if(o==null) {
			throw new PhotoException("Required argument "
				+ which + " not provided");
		}

		if(! type.isInstance(o)) {
			throw new PhotoException("Argument " + which
				+ " must be a " + type.getName() + ", but is a "
				+ o.getClass().getName());
		}
	}

}
