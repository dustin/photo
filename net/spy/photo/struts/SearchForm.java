// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: SearchForm.java,v 1.4 2002/05/12 09:01:01 dustin Exp $

package net.spy.photo.struts;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Form for processing search requests.
 */
public class SearchForm extends ActionForm {

	private String cats[]=null;
	private String fieldjoin=null;
	private String field=null;
	private String keyjoin=null;
	private String what=null;
	private String tstartjoin=null;
	private String tstart=null;
	private String tendjoin=null;
	private String tend=null;
	private String startjoin=null;
	private String start=null;
	private String endjoin=null;
	private String end=null;
	private String order=null;
	private String sdirection=null;
	private String maxret=null;

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

	public void setFieldjoin(String fieldjoin) {
		this.fieldjoin=fieldjoin;
	}

	public String getFieldjoin() {
		return(fieldjoin);
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

	public void setTstartjoin(String tstartjoin) {
		this.tstartjoin=tstartjoin;
	}

	public String getTstartjoin() {
		return(tstartjoin);
	}

	public void setTstart(String tstart) {
		this.tstart=tstart;
	}

	public String getTstart() {
		return(tstart);
	}

	public void setTendjoin(String tendjoin) {
		this.tendjoin=tendjoin;
	}

	public String getTendjoin() {
		return(tendjoin);
	}

	public void setTend(String tend) {
		this.tend=tend;
	}

	public String getTend() {
		return(tend);
	}

	public void setStartjoin(String startjoin) {
		this.startjoin=startjoin;
	}

	public String getStartjoin() {
		return(startjoin);
	}

	public void setStart(String start) {
		this.start=start;
	}

	public String getStart() {
		return(start);
	}

	public void setEndjoin(String endjoin) {
		this.endjoin=endjoin;
	}

	public String getEndjoin() {
		return(endjoin);
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
	 * Reset all properties to their default values.
	 *
	 * @param mapping The mapping used to select this instance
	 * @param request The servlet request we are processing
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		cats=null;
		fieldjoin=null;
		field=null;
		keyjoin=null;
		what=null;
		tstartjoin=null;
		tstart=null;
		tendjoin=null;
		tend=null;
		startjoin=null;
		start=null;
		endjoin=null;
		end=null;
		order=null;
		sdirection=null;
		maxret=null;
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

		String tmp=fieldjoin;
		if(tmp!=null && !(tmp.equals("and") || tmp.equals("or"))) {
			errors.add("fieldjoin", new ActionError("error.search.fieldjoin"));
		}
		tmp=field;
		if(tmp!=null && !(tmp.equals("keywords") || tmp.equals("descr"))) {
			errors.add("field", new ActionError("error.search.field"));
		}
		tmp=keyjoin;
		if(tmp!=null && !(tmp.equals("or") || tmp.equals("and"))) {
			errors.add("keyjoin", new ActionError("error.search.keyjoin"));
		}
		tmp=tstartjoin;
		if(tmp!=null && !(tmp.equals("or") || tmp.equals("and"))) {
			errors.add("tstartjoin",
				new ActionError("error.search.tstartjoin"));
		}
		tmp=tendjoin;
		if(tmp!=null && !(tmp.equals("or") || tmp.equals("and"))) {
			errors.add("tendjoin", new ActionError("error.search.tendjoin"));
		}
		tmp=startjoin;
		if(tmp!=null && !(tmp.equals("or") || tmp.equals("and"))) {
			errors.add("startjoin", new ActionError("error.search.startjoin"));
		}
		tmp=endjoin;
		if(tmp!=null && !(tmp.equals("or") || tmp.equals("and"))) {
			errors.add("endjoin", new ActionError("error.search.endjoin"));
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
