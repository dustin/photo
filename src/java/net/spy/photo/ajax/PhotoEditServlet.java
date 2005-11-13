// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>
// arch-tag: C604AB09-270D-404E-ADF4-66E2F2E88C7C

package net.spy.photo.ajax;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.spy.jwebkit.SAXAble;
import net.spy.jwebkit.struts.AjaxAction;

import net.spy.SpyObject;
import net.spy.db.Saver;
import net.spy.photo.Comment;
import net.spy.photo.Persistent;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoSessionData;
import net.spy.photo.User;

/**
 * Post a comment.
 */
public class PhotoEditServlet extends PhotoAjaxServlet {

	private Map<String, Handler> handlers=null;
	private Map<String, String> roles=null;
	private Map<String, Boolean> guestAllowed=null;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		handlers=new HashMap<String, Handler>();
		roles=new HashMap<String, String>();
		guestAllowed=new HashMap<String, Boolean>();
		Class inner[]=getClass().getClasses();
		getLogger().info("Found " + inner.length + " inner classes.");
		for(Class c : inner) {
			AjaxHandler ah=(AjaxHandler)c.getAnnotation(AjaxHandler.class);
			getLogger().info("Inner " + c + " has annotation " + ah);
			if(ah != null) {
				try {
					handlers.put(ah.path(), (Handler)c.newInstance());
					if(!ah.role().equals("")) {
						roles.put(ah.path(), ah.role());
					}
					guestAllowed.put(ah.path(), ah.guest());
				} catch(Exception e) {
					getLogger().warn("Error instantiating " + c, e);
				}
			}
		}
		getLogger().info("Mappings:  " + handlers);
	}

	protected SAXAble getResults(HttpServletRequest request) throws Exception {
		String idString=request.getParameter("imgId");
		if(idString == null) {
			throw new NullPointerException("imgId");
		}
		int id=Integer.parseInt(idString);

		PhotoSessionData ses=getSessionData(request);
		User user=ses.getUser();

		// Check the access
		Persistent.getSecurity().checkAccess(user, id);
		String pathInfo=request.getPathInfo();
		String role=roles.get(pathInfo);
		if(role != null) {
			if(!user.getRoles().contains(role)) {
				throw new Exception(user
					+ " does not meet the role requirement:  " + role);
			}
		}
		if(!guestAllowed.get(pathInfo)) {
			if(user.getRoles().contains("guest")) {
				throw new Exception("Guest access not allowed");
			}
		}

		// Look up and invoke the handler
		Handler h=handlers.get(pathInfo);
		return(h.handle(id, user, request));
	}

	/** 
	 * Interface for PhotoEditing handlers.
	 */
	public static abstract class Handler extends SpyObject {
		public abstract SAXAble handle(int id, User user,
			HttpServletRequest request) throws Exception;
	}

	@AjaxHandler(path="/comment",role="")
	public static class AddCommentHandler extends Handler {
		public SAXAble handle(int id, User user, HttpServletRequest request)
			throws Exception {

			String commentString=request.getParameter("comment");
			if(commentString == null || commentString.length()==0) {
				throw new NullPointerException("comment");
			}

			// Construct the comment.
			Comment comment=new Comment();
			comment.setUser(user);
			comment.setPhotoId(id);
			comment.setRemoteAddr(request.getRemoteAddr());
			comment.setNote(commentString);

			Saver s=new Saver(PhotoConfig.getInstance());
			s.save(comment);

			getLogger().info(user + " Posting comment for image "
				+ id + ":  " + comment);
			return(null);
		}
	}

}
