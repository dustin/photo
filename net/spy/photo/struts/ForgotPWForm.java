// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: ForgotPWForm.java,v 1.3 2003/01/07 09:38:53 dustin Exp $

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;

import net.spy.photo.Persistent;
import net.spy.photo.PhotoUser;
import net.spy.photo.PhotoUserException;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Form used when a user forgets his password.
 */
public class ForgotPWForm extends ActionForm {

	private String username=null;

	/**
	 * Get an instance of ForgotPWForm.
	 */
	public ForgotPWForm() {
		super();
	}

	public String getUsername() {
		return(username);
	}

	public void setUsername(String username) {
		this.username=username;
	}

	/**
	 * Reset all properties.
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		username=null;
	}

	/**
	 * Validate the input.
	 */
	public ActionErrors validate(ActionMapping mapping,
		HttpServletRequest request) {

		ActionErrors errors = new ActionErrors();

		if(username==null || username.length() < 1) {
			errors.add("username", new ActionError("error.forgotpw.username"));
		} else {

			// Check for guest
			PhotoUser user=null;
			try {
				user=Persistent.getSecurity().getUser(username);
			} catch(PhotoUserException e) {
				// Don't error here.  Let them try it before telling them
				// the user doesn't exist.
				e.printStackTrace();
			}

			// Make sure it's not guest.
			if(user!=null) {
				if(user.getUsername().equals("guest")) {
					errors.add("username",
						new ActionError("error.forgotpw.guest"));
				}
			}
		}

		return(errors);
	}

}
