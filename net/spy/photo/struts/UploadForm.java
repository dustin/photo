// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: UploadForm.java,v 1.4 2002/06/23 05:26:06 dustin Exp $

package net.spy.photo.struts;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.struts.upload.FormFile;
import org.apache.struts.action.*;

import net.spy.photo.*;

/**
 * Form to receive image uploads.
 */
public class UploadForm extends ActionForm {

	private FormFile picture=null;
	private String id=null;
	private String category=null;
	private String taken=null;
	private String keywords=null;
	private String info=null;
	private PhotoImage photoImage=null;

	/**
	 * Get an instance of UploadForm.
	 */
	public UploadForm() {
		super();

		taken=PhotoUtil.getToday();
	}

	public void setPicture(FormFile picture) {
		this.picture=picture;
	}

	public FormFile getPicture() {
		return(picture);
	}

	public void setCategory(String category) {
		this.category=category;
	}

	public String getCategory() {
		return(category);
	}

	public void setTaken(String taken) {
		this.taken=taken;
	}

	public String getTaken() {
		return(taken);
	}

	public void setKeywords(String keywords) {
		this.keywords=keywords;
	}

	public String getKeywords() {
		return(keywords);
	}

	public void setInfo(String info) {
		this.info=info;
	}

	public String getInfo() {
		return(info);
	}

	/**
	 * Get the PhotoImage that got uploaded.
	 */
	public PhotoImage getPhotoImage() {
		return(photoImage);
	}

	/**
	 * Validate the properties.
	 *
	 * @param mapping The mapping used to select this instance
	 * @param request The servlet request we are processing
	 */
	public ActionErrors validate(ActionMapping mapping,
		HttpServletRequest request) {

		System.err.println("Validating upload form.");

		ActionErrors errors = new ActionErrors();

		// Verify the keywords
		if( (keywords==null) || (keywords.length() < 1)) {
			errors.add("keywords", new ActionError("error.upload.keywords"));
		}

		// Verify the description
		if( (info==null) || (info.length() < 1) ) {
			errors.add("info", new ActionError("error.upload.info"));
		}

		// Verify the taken date
		if( (taken==null) || (taken.length() < 1) ) {
			errors.add("taken", new ActionError("error.upload.taken"));
		}

		// Verify the category format
		if( (category==null) || (category.length() < 1) ) {
			errors.add("category", new ActionError("error.upload.category"));

			try {
				Integer.parseInt(category);
			} catch(NumberFormatException nfe) {
				nfe.printStackTrace();
				errors.add("category",
					new ActionError("error.upload.category.nfe"));
			}
		}

		// Get and verify the picture if this is an upload
		if(mapping.getType().equals(UploadAction.class.getName())) {
			// Make sure the ID is provided.
			if(id==null || id.length() < 1) {
				errors.add("id", new ActionError("error.upload.id"));
			} else {
				try {
					Integer.parseInt(id);
				} catch(NumberFormatException e) {
					errors.add("id", new ActionError("error.upload.id.nfe"));
				}
			}
		} else {
			if( (picture==null) || (picture.getFileSize() == 0) ) {
				errors.add("picture", new ActionError("error.upload.picture"));
			} else {
				// Get the PhotoImage
				byte data[]=new byte[picture.getFileSize()];
				try {
					int length=picture.getInputStream().read(data);

					// verify we read enough data
					if(length != picture.getFileSize()) {
						errors.add("picture",
							new ActionError("error.upload.picture.notread"));
					}

					// Create a PhotoImage out of it.
					photoImage=new PhotoImage(data);
					System.out.println("Mime type is "
						+ picture.getContentType()
						+ " format is " + photoImage.getFormatString());
				} catch(Exception e) {
					e.printStackTrace();
					errors.add("picture",
						new ActionError("error.upload.picture.notread"));
				}
			}
		}

		return(errors);
	}

}
