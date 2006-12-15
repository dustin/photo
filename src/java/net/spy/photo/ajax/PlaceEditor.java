// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>

package net.spy.photo.ajax;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import net.spy.jwebkit.AjaxHandler;
import net.spy.jwebkit.AjaxInPlaceEditServlet;
import net.spy.photo.MutablePlace;
import net.spy.photo.PlaceFactory;
import net.spy.photo.User;

public class PlaceEditor extends AjaxInPlaceEditServlet {

	/**
	 * Default role is User.ADMIN.
	 */
	@Override
	protected String getDefaultRole() {
		return User.ADMIN;
	}

	/**
	 * Base class for all image value editors.
	 */
	static abstract class ValueEditor extends Handler {
		@Override
		public Object handle(Principal u, HttpServletRequest req)
			throws Exception {
			
			String value=getStringParam(req, "value", 1);

			PlaceFactory pf=PlaceFactory.getInstance();
			MutablePlace mp=pf.getMutable(getIntParam(req, "id"));
			update(mp, value);
			pf.persist(mp);

			getLogger().info("%s changed %s", u, mp);

			return(value);
		}

		protected abstract void update(MutablePlace savable,
			String value) throws Exception;
	}

	@AjaxHandler(path="/new")
	public static class NewHandler extends Handler {
		@Override
		public Object handle(Principal u, HttpServletRequest req)
			throws Exception {

			PlaceFactory pf=PlaceFactory.getInstance();
			MutablePlace mp=pf.create();

			mp.setName(getStringParam(req, "name", 1));
			mp.setLongitude(Double.parseDouble(getStringParam(req, "lon", 1)));
			mp.setLatitude(Double.parseDouble(getStringParam(req, "lat", 1)));

			pf.persist(mp);

			getLogger().info("Created new place id=%s for %s", mp.getId(), u);
			return String.valueOf(mp.getId());
		}
	}

	@AjaxHandler(path="/name")
	public static class NameEditor extends ValueEditor {
		@Override
		protected void update(MutablePlace mp, String value)
			throws Exception {
			mp.setName(value);
		}
	}

	@AjaxHandler(path="/lon")
	public static class LonEditor extends ValueEditor {
		@Override
		protected void update(MutablePlace mp, String value)
			throws Exception {
			mp.setLongitude(Double.parseDouble(value));
		}
	}

	@AjaxHandler(path="/lat")
	public static class LatEditor extends ValueEditor {
		@Override
		protected void update(MutablePlace mp, String value)
			throws Exception {
			mp.setLatitude(Double.parseDouble(value));
		}
	}
}
