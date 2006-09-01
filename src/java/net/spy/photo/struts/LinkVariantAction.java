// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>
// arch-tag: 35672B19-D09B-45E2-908B-E9D761B8B195

package net.spy.photo.struts;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import net.spy.photo.PhotoImageDataFactory;
import net.spy.photo.impl.SavablePhotoImageData;

public class LinkVariantAction extends PhotoAction {

	@Override
	protected ActionForward spyExecute(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		DynaActionForm df=(DynaActionForm)form;
		PhotoImageDataFactory pidf=PhotoImageDataFactory.getInstance();
		SavablePhotoImageData savable=new SavablePhotoImageData(
				pidf.getObject((Integer)df.get("origId")));
		savable.getVariants().add(
				pidf.getObject((Integer)df.get("variantId")));
		pidf.store(savable);
		ServletOutputStream os=res.getOutputStream();
		res.setContentType("text/plain");
		os.println("OK");
		return null;
	}

}
