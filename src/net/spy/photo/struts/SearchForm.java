// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 3A3460F3-5D6E-11D9-A1C0-000A957659CC

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

/**
 * Form for processing search requests.
 */
public class SearchForm extends PhotoForm {

	private String cats[]=null;
	private String field=null;
	private String keyjoin=null;
	private String what=null;

	private String tstart=null;
	private String tend=null;

	private String start=null;
	private String end=null;

	private String order=null;
	private String sdirection=null;
	private String maxret=null;

	private String filter=null;

	private String action=null;

	/**
	 * Get an instance of SearchForm.
	 */
	public SearchForm() {
		super();
	}

	/**
	 * Add a category.
	 */
	public void setCat(String cat[]) {
		cats=cat;
	}

	/**
	 * Get the categories.
	 */
	public String[] getCat() {
		return(cats);
	}

	public void setField(String field) {
		this.field=field;
	}

	public String getField() {
		return(field);
	}

	public void setKeyjoin(String keyjoin) {
		this.keyjoin=keyjoin;
	}

	public String getKeyjoin() {
		return(keyjoin);
	}

	public void setWhat(String what) {
		this.what=what;
	}

	public String getWhat() {
		return(what);
	}

	public void setTstart(String tstart) {
		this.tstart=tstart;
	}

	public String getTstart() {
		return(tstart);
	}

	public void setTend(String tend) {
		this.tend=tend;
	}

	public String getTend() {
		return(tend);
	}

	public void setStart(String start) {
		this.start=start;
	}

	public String getStart() {
		return(start);
	}

	public void setEnd(String end) {
		this.end=end;
	}

	public String getEnd() {
		return(end);
	}

	public void setOrder(String order) {
		this.order=order;
	}

	public String getOrder() {
		return(order);
	}

	public void setSdirection(String sdirection) {
		this.sdirection=sdirection;
	}

	public String getSdirection() {
		return(sdirection);
	}

	public void setMaxret(String maxret) {
		this.maxret=maxret;
	}

	public String getMaxret() {
		return(maxret);
	}

	/**
	 * Get the search result filter.
	 */
	public String getFilter() {
		return(filter);
	}

	/**
	 * Set the search result filter.
	 */
	public void setFilter(String filter) {
		this.filter=filter;
	}

	/** 
	 * Get the action.
	 */
	public String getAction() {
		return(action);
	}

	/** 
	 * Set the action.
	 */
	public void setAction(String action) {
		this.action=action;
	}

	/**
	 * Reset all properties to their default values.
	 *
	 * @param mapping The mapping used to select this instance
	 * @param request The servlet request we are processing
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		if(mapping.getPath().equals("/search")) {
			cats=null;
			keyjoin=null;
			field=null;
			what=null;
			tstart=null;
			tend=null;
			start=null;
			end=null;
			order=null;
			sdirection=null;
			maxret=null;
			filter=null;
			action=null;
		}
	}

	/**
	 * Validate the properties.
	 *
	 * @param mapping The mapping used to select this instance
	 * @param request The servlet request we are processing
	 */
	public ActionErrors validate(ActionMapping mapping,
		HttpServletRequest request) {

		ActionErrors errors = new ActionErrors();

		String tmp=field;
		if(tmp!=null && !(tmp.equals("keywords") || tmp.equals("descr"))) {
			errors.add("field", new ActionError("error.search.field"));
		}
		tmp=keyjoin;
		if(tmp!=null && !(tmp.equals("or") || tmp.equals("and"))) {
			errors.add("keyjoin", new ActionError("error.search.keyjoin"));
		}
		tmp=order;
		if(tmp!=null && !(tmp.equals("a.taken") || tmp.equals("a.ts"))) {
			errors.add("order", new ActionError("error.search.order"));
		}
		tmp=sdirection;
		if(tmp!=null && !(tmp.equals("") || tmp.equals("desc"))) {
			errors.add("sdirection",
				new ActionError("error.search.sdirection"));
		}

		return(errors);
	}

}
