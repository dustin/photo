// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>

package net.spy.photo.struts;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.PhotoImageFactory;
import net.spy.photo.impl.SavablePhotoImage;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

public class LinkVariantAction extends PhotoAction {

	@Override
	protected ActionForward spyExecute(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		DynaActionForm df=(DynaActionForm)form;
		PhotoImageFactory pidf=PhotoImageFactory.getInstance();
		SavablePhotoImage savable=new SavablePhotoImage(
				pidf.getObject((Integer)df.get("origId")));
		savable.addVariant(
				pidf.getObject((Integer)df.get("variantId")));
		pidf.store(savable);
		ServletOutputStream os=res.getOutputStream();
		res.setContentType("text/plain");
		os.println("OK");
		return null;
	}

}
