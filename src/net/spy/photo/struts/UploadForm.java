// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: UploadForm.java,v 1.8 2002/11/21 07:37:47 dustin Exp $

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;

import net.spy.photo.PhotoImage;
import net.spy.photo.PhotoUtil;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import org.apache.struts.upload.FormFile;

/**
 * Form to receive image uploads.
 */
public class UploadForm extends PhotoForm {

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

	public String getId() {
		return(id);
	}

	public void setId(String id) {
		this.id=id;
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

		getLogger().debug("Validating upload form.");

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
		} else {
			try {
				Integer.parseInt(category);
			} catch(NumberFormatException nfe) {
				getLogger().info("Could not parse category id", nfe);
				errors.add("category",
					new ActionError("error.upload.category.nfe"));
			}
		}

		// Get and verify the picture if this is an upload
		if(mapping.getType().equals(UploadAction.class.getName())) {

			// It's an upload, verify the picture

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
					getLogger().debug("Mime type is "
						+ picture.getContentType()
						+ " format is " + photoImage.getFormatString());
				} catch(Exception e) {
					getLogger().warn("Problem uploading picture", e);
					errors.add("picture",
						new ActionError("error.upload.picture.notread"));
				}
			}

		} else {

			// It's not an upload, so we need to verify the ID
			if(id==null || id.length() < 1) {
				errors.add("id", new ActionError("error.upload.id"));
			} else {
				try {
					Integer.parseInt(id);
				} catch(NumberFormatException e) {
					getLogger().info("Failed to parse image id", e);
					errors.add("id", new ActionError("error.upload.id.nfe"));
				}
			}

		}

		return(errors);
	}

	/**
	 * Null the file on reset as to keep the form serializable.
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
        if(picture!=null) {
            picture.destroy();
            picture=null;
        }
	}

}
