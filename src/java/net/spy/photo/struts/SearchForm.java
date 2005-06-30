// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 3A3460F3-5D6E-11D9-A1C0-000A957659CC

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

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

	public void setField(String to) {
		this.field=to;
	}

	public String getField() {
		return(field);
	}

	public void setKeyjoin(String to) {
		this.keyjoin=to;
	}

	public String getKeyjoin() {
		return(keyjoin);
	}

	public void setWhat(String to) {
		this.what=to;
	}

	public String getWhat() {
		return(what);
	}

	public void setTstart(String to) {
		this.tstart=to;
	}

	public String getTstart() {
		return(tstart);
	}

	public void setTend(String to) {
		this.tend=to;
	}

	public String getTend() {
		return(tend);
	}

	public void setStart(String to) {
		this.start=to;
	}

	public String getStart() {
		return(start);
	}

	public void setEnd(String to) {
		this.end=to;
	}

	public String getEnd() {
		return(end);
	}

	public void setOrder(String to) {
		this.order=to;
	}

	public String getOrder() {
		return(order);
	}

	public void setSdirection(String to) {
		this.sdirection=to;
	}

	public String getSdirection() {
		return(sdirection);
	}

	public void setMaxret(String to) {
		this.maxret=to;
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
	public void setFilter(String to) {
		this.filter=to;
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
	public void setAction(String to) {
		this.action=to;
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
			errors.add("field", new ActionMessage("error.search.field"));
		}
		tmp=keyjoin;
		if(tmp!=null && !(tmp.equals("or") || tmp.equals("and"))) {
			errors.add("keyjoin", new ActionMessage("error.search.keyjoin"));
		}
		tmp=order;
		if(tmp!=null && !(tmp.equals("a.taken") || tmp.equals("a.ts"))) {
			errors.add("order", new ActionMessage("error.search.order"));
		}
		tmp=sdirection;
		if(tmp!=null && !(tmp.equals("") || tmp.equals("desc"))) {
			errors.add("sdirection",
				new ActionMessage("error.search.sdirection"));
		}

		return(errors);
	}

}
