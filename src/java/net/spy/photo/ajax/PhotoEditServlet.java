// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>
// arch-tag: C604AB09-270D-404E-ADF4-66E2F2E88C7C

package net.spy.photo.ajax;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.SpyObject;
import net.spy.db.Saver;
import net.spy.jwebkit.SAXAble;
import net.spy.photo.Category;
import net.spy.photo.CategoryFactory;
import net.spy.photo.Comment;
import net.spy.photo.Persistent;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoImageDataFactory;
import net.spy.photo.PhotoSessionData;
import net.spy.photo.User;
import net.spy.photo.impl.SavablePhotoImageData;

/**
 * Post a comment.
 */
public class PhotoEditServlet extends PhotoAjaxServlet {

	private Map<String, Handler> handlers=null;
	private Map<String, String> roles=null;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		handlers=new HashMap<String, Handler>();
		roles=new HashMap<String, String>();
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
				} catch(Exception e) {
					getLogger().warn("Error instantiating " + c, e);
				}
			}
		}
		getLogger().info("Mappings:  " + handlers);
	}

	protected void processRequest(
		HttpServletRequest request, HttpServletResponse response)
		throws Exception {

		String idString=request.getParameter("imgId");
		if(idString == null) {
			throw new NullPointerException("imgId");
		}
		int id=Integer.parseInt(idString);

		// Look up the handler
		String pathInfo=request.getPathInfo();
		Handler h=handlers.get(pathInfo);
		if(h == null) {
			throw new Exception("No handler for " + pathInfo);
		}

		PhotoSessionData ses=getSessionData(request);
		User user=ses.getUser();

		// Check the access
		Persistent.getSecurity().checkAccess(user, id);
		String role=roles.get(pathInfo);
		if(role != null) {
			if(!user.getRoles().contains(role)) {
				throw new Exception(user
					+ " does not meet the role requirement:  " + role);
			}
		}

		// Now invoke the handler
		Object rv=h.handle(id, user, request);
		if(rv instanceof SAXAble) {
			sendXml((SAXAble)rv, response);
		} else {
			sendPlain(String.valueOf(rv), response);
		}
	}

	public static abstract class Handler extends SpyObject {
		public abstract Object handle(int id, User user,
			HttpServletRequest request) throws Exception;
	}

	@AjaxHandler(path="/comment",role=User.AUTHENTICATED)
	public static class AddCommentHandler extends Handler {
		public Object handle(int id, User user, HttpServletRequest request)
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

	public static abstract class ValueEditor extends Handler {
		public Object handle(int id, User user, HttpServletRequest request)
			throws Exception {
			String value=request.getParameter("value");
			if(value == null || value.length()==0) {
				throw new NullPointerException("value");
			}

			PhotoImageDataFactory pidf=PhotoImageDataFactory.getInstance();
			SavablePhotoImageData savable=new SavablePhotoImageData(
				pidf.getObject(id));

			update(savable, value);
			getLogger().info("Updated " + savable + " with " + value);

			pidf.store(savable);

			return(value);
		}

		protected abstract void update(SavablePhotoImageData savable,
			String value) throws Exception;
	}

	@AjaxHandler(path="/descr")
	public static class DescrHandler extends ValueEditor {
		public void update(SavablePhotoImageData savable, String value)
			throws Exception {
			savable.setDescr(value);
		}
	}

	@AjaxHandler(path="/keywords")
	public static class KeywordsHandler extends ValueEditor {
		public void update(SavablePhotoImageData savable, String value)
			throws Exception {
			savable.setKeywords(value);
		}
	}

	@AjaxHandler(path="/taken")
	public static class TakenHandler extends ValueEditor {
		public void update(SavablePhotoImageData savable, String value)
			throws Exception {
			savable.setTaken(value);
		}
	}

	@AjaxHandler(path="/cat")
	public static class CatHandler extends Handler {
		public Object handle(int id, User user, HttpServletRequest request)
			throws Exception {
			String value=request.getParameter("value");
			if(value == null || value.length()==0) {
				throw new NullPointerException("value");
			}
			int catId=Integer.parseInt(value);

			if(!user.canAdd(catId)) {
				throw new Exception(user + " can't access cat " + catId);
			}

			CategoryFactory cf=CategoryFactory.getInstance();
			Category cat=cf.getObject(catId);

			PhotoImageDataFactory pidf=PhotoImageDataFactory.getInstance();
			SavablePhotoImageData savable=new SavablePhotoImageData(
				pidf.getObject(id));
			savable.setCatId(catId);
			pidf.store(savable);

			return(cat.getName());
		}
	}

}
