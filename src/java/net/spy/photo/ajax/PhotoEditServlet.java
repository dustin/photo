// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>

package net.spy.photo.ajax;

import java.security.Principal;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import net.spy.db.Saver;
import net.spy.factory.CacheRefresher;
import net.spy.jwebkit.AjaxHandler;
import net.spy.jwebkit.AjaxInPlaceEditServlet;
import net.spy.photo.Category;
import net.spy.photo.CategoryFactory;
import net.spy.photo.Comment;
import net.spy.photo.Persistent;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoDimensions;
import net.spy.photo.PhotoImage;
import net.spy.photo.PhotoImageFactory;
import net.spy.photo.PhotoRegion;
import net.spy.photo.PhotoUtil;
import net.spy.photo.Place;
import net.spy.photo.PlaceFactory;
import net.spy.photo.User;
import net.spy.photo.Vote;
import net.spy.photo.impl.PhotoDimensionsImpl;
import net.spy.photo.impl.PhotoRegionImpl;
import net.spy.photo.impl.SavablePhotoImage;

/**
 * Post a comment.
 */
public class PhotoEditServlet extends AjaxInPlaceEditServlet {

	private static final long RECACHE_DELAY=30000;

	// Convenience method to get the image ID, or throw an NPE.
	static int getImageId(HttpServletRequest req) {
		String idString=req.getParameter("imgId");
		if(idString == null) {
			throw new NullPointerException("imgId");
		}
		return Integer.parseInt(idString);
	}

	/**
	 * Validate the user has access to see this image.
	 */
	@Override
	protected void checkRequest(Principal u, HttpServletRequest req)
		throws ServletException {
		// Check the access
		try {
			Persistent.getSecurity().checkAccess((User)u, getImageId(req));
		} catch(Exception e) {
			throw new ServletException("Permission denied", e);
		}
	}

	/**
	 * Default role is User.ADMIN.
	 */
	@Override
	protected String getDefaultRole() {
		return User.ADMIN;
	}

	/**
	 * Base handler for all of the AJAXy things that work with images..
	 */
	public abstract static class BaseHandler extends Handler {
		@Override
		public Object handle(Principal u, HttpServletRequest req)
			throws Exception {
			return handle(getImageId(req), (User)u, req);
		}
		public abstract Object handle(int id, User user, HttpServletRequest req)
			throws Exception;
	}

	@AjaxHandler(path="/comment",role=User.AUTHENTICATED)
	public static class AddCommentHandler extends BaseHandler {
		@Override
		public Object handle(int id, User user, HttpServletRequest request)
			throws Exception {

			String commentString=getStringParam(request, "comment", 1);

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

	@AjaxHandler(path="/vote",role=User.AUTHENTICATED)
	public static class AddVoteHandler extends BaseHandler {
		@Override
		public Object handle(int id, User user, HttpServletRequest request)
			throws Exception {

			int voteValue=getIntParam(request, "vote");

			PhotoImageFactory pidf=PhotoImageFactory.getInstance();
			PhotoImage img=pidf.getObject(id);

			// First, try to find a current vote to update
			Vote vote=img.getVotes().getVote(user.getId());
			// If we don't have one, make a new one.
			if(vote == null) {
				vote=new Vote();
			}
			
			vote.setUser(user);
			vote.setPhotoId(id);
			vote.setRemoteAddr(request.getRemoteAddr());
			vote.setVote(voteValue);

			Saver s=new Saver(PhotoConfig.getInstance());
			s.save(vote);

			// Recache to get the votes up-to-date
			CacheRefresher.getInstance().recache(pidf,
					System.currentTimeMillis(),
					PhotoImageFactory.RECACHE_DELAY);

			// Get the image again and check out the newly calculated averages
			img=pidf.getObject(id);

			getLogger().info(user + " Posting vote for image " + id
				+ ":  " + vote + " new votes:  " + img.getVotes());
			// JSON response
			return("{\"avg\": " + img.getVotes().getAverage()
				+ ", \"size\": " + img.getVotes().getSize() + "}");
		}
	}

	@AjaxHandler(path="/annotation",role=User.AUTHENTICATED)
	public static class AddAnnotationHandler extends BaseHandler {
		@Override
		public Object handle(int id, User user, HttpServletRequest request)
			throws Exception {

			String title=getStringParam(request, "title", 1);
			String keywords=getStringParam(request, "keywords", 0);

			PhotoImageFactory pidf=PhotoImageFactory.getInstance();
			SavablePhotoImage savable=new SavablePhotoImage(
				pidf.getObject(id));

			PhotoDimensions displaySize=
				new PhotoDimensionsImpl(getStringParam(request, "imgDims", 1));
			float scaleFactor=PhotoUtil.getScaleFactor(displaySize,
				savable.getDimensions());

			PhotoRegion specifiedRegion=new PhotoRegionImpl(
				getIntParam(request, "x"), getIntParam(request, "y"),
				getIntParam(request, "w"), getIntParam(request, "h"));

			// The scaled region
			PhotoRegion newRegion=PhotoUtil.scaleRegion(specifiedRegion,
				scaleFactor);

			// Add it and save it.
			savable.addAnnotation(
				newRegion.getX(), newRegion.getY(),
				newRegion.getWidth(), newRegion.getHeight(),
				keywords, title, user);

			pidf.store(savable);

			return(null);
		}
	}

	/**
	 * Base class for all image value editors.
	 */
	public static abstract class ValueEditor extends BaseHandler {
		@Override
		public Object handle(int id, User user, HttpServletRequest request)
			throws Exception {
			String value=request.getParameter("value");
			if(value == null || value.length()==0) {
				throw new NullPointerException("value");
			}

			PhotoImageFactory pidf=PhotoImageFactory.getInstance();
			SavablePhotoImage savable=new SavablePhotoImage(
				pidf.getObject(id));

			update(savable, value);
			getLogger().info("Updated " + savable + " with " + value);

			pidf.store(savable, true, RECACHE_DELAY);

			return(value);
		}

		protected abstract void update(SavablePhotoImage savable,
			String value) throws Exception;
	}

	@AjaxHandler(path="/descr")
	public static class DescrHandler extends ValueEditor {
		@Override
		public void update(SavablePhotoImage savable, String value)
			throws Exception {
			savable.setDescr(value);
		}
	}

	@AjaxHandler(path="/keywords")
	public static class KeywordsHandler extends ValueEditor {
		@Override
		public void update(SavablePhotoImage savable, String value)
			throws Exception {
			savable.setKeywords(value);
		}
	}

	@AjaxHandler(path="/taken")
	public static class TakenHandler extends ValueEditor {
		@Override
		public void update(SavablePhotoImage savable, String value)
			throws Exception {
			savable.setTaken(value);
		}
	}

	@AjaxHandler(path="/cat")
	public static class CatHandler extends BaseHandler {
		@Override
		public Object handle(int id, User user, HttpServletRequest request)
			throws Exception {
			// This one is a bit more complicated because it receives a cat ID
			// and needs to return the name of that cat.

			String value=getStringParam(request, "value", 1);
			int catId=Integer.parseInt(value);

			if(!user.canAdd(catId)) {
				throw new Exception(user + " can't access cat " + catId);
			}

			CategoryFactory cf=CategoryFactory.getInstance();
			Category cat=cf.getObject(catId);

			PhotoImageFactory pidf=PhotoImageFactory.getInstance();
			SavablePhotoImage savable=new SavablePhotoImage(
				pidf.getObject(id));
			savable.setCatId(catId);
			pidf.store(savable, true, RECACHE_DELAY);

			getLogger().info("%s changed the category of %s to %s",
					user, id, cat);

			return(cat.getName());
		}
	}

	@AjaxHandler(path="/place")
	public static class PlaceHandler extends BaseHandler {
		@Override
		public Object handle(int id, User user, HttpServletRequest request)
			throws Exception {
			// This one is a bit more complicated because it receives a place ID
			// and needs to return the name of that place.

			String value=getStringParam(request, "value", 1);
			int placeId=Integer.parseInt(value);
			Place place=null;

			if(placeId > 0) {
				PlaceFactory cf=PlaceFactory.getInstance();
				place=cf.getObject(placeId);
			}

			PhotoImageFactory pidf=PhotoImageFactory.getInstance();
			SavablePhotoImage savable=new SavablePhotoImage(
				pidf.getObject(id));
			savable.setPlace(place);
			pidf.store(savable, true, RECACHE_DELAY);

			getLogger().info("%s changed the place of %s to %s",
					user, id, place);

			return(place == null ? "Unknown" : place.getName());
		}
	}

}
