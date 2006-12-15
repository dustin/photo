// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;

import net.spy.photo.PhotoImage;
import net.spy.photo.PhotoUtil;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
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

	public void setPicture(FormFile to) {
		this.picture=to;
	}

	public FormFile getPicture() {
		return(picture);
	}

	public void setCategory(String to) {
		this.category=to;
	}

	public String getCategory() {
		return(category);
	}

	public void setTaken(String to) {
		this.taken=to;
	}

	public String getTaken() {
		return(taken);
	}

	public void setKeywords(String to) {
		this.keywords=to;
	}

	public String getKeywords() {
		return(keywords);
	}

	public void setInfo(String to) {
		this.info=to;
	}

	public String getInfo() {
		return(info);
	}

	public String getId() {
		return(id);
	}

	public void setId(String to) {
		this.id=to;
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
	@Override
	public ActionErrors validate(ActionMapping mapping,
		HttpServletRequest request) {

		getLogger().debug("Validating upload form.");

		ActionErrors errors = new ActionErrors();

		// Verify the keywords
		if( (keywords==null) || (keywords.length() < 1)) {
			errors.add("keywords", new ActionMessage("error.upload.keywords"));
		}

		// Verify the description
		if( (info==null) || (info.length() < 1) ) {
			errors.add("info", new ActionMessage("error.upload.info"));
		}

		// Verify the taken date
		if( (taken==null) || (taken.length() < 1) ) {
			errors.add("taken", new ActionMessage("error.upload.taken"));
		}

		// Verify the category format
		if( (category==null) || (category.length() < 1) ) {
			errors.add("category", new ActionMessage("error.upload.category"));
		} else {
			try {
				Integer.parseInt(category);
			} catch(NumberFormatException nfe) {
				getLogger().info("Could not parse category id", nfe);
				errors.add("category",
					new ActionMessage("error.upload.category.nfe"));
			}
		}

		// Get and verify the picture if this is an upload
		if(mapping.getType().equals(UploadAction.class.getName())) {

			// It's an upload, verify the picture

			if( (picture==null) || (picture.getFileSize() == 0) ) {
				errors.add("picture", new ActionMessage("error.upload.picture"));
			} else {
				// Get the PhotoImage
				byte data[]=new byte[picture.getFileSize()];
				try {
					int length=picture.getInputStream().read(data);
					// verify we read enough data
					if(length != picture.getFileSize()) {
						errors.add("picture",
							new ActionMessage("error.upload.picture.notread"));
					}
					// Create a PhotoImage out of it.
					photoImage=new PhotoImage(data);
					getLogger().debug("Mime type is "
						+ picture.getContentType()
						+ " format is " + photoImage.getFormat().getMime());
				} catch(Exception e) {
					getLogger().warn("Problem uploading picture", e);
					errors.add("picture",
						new ActionMessage("error.upload.picture.notread"));
				}
			}

		} else {

			// It's not an upload, so we need to verify the ID
			if(id==null || id.length() < 1) {
				errors.add("id", new ActionMessage("error.upload.id"));
			} else {
				try {
					Integer.parseInt(id);
				} catch(NumberFormatException e) {
					getLogger().info("Failed to parse image id", e);
					errors.add("id", new ActionMessage("error.upload.id.nfe"));
				}
			}

		}

		return(errors);
	}

	/**
	 * Null the file on reset as to keep the form serializable.
	 */
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
        if(picture!=null) {
            picture.destroy();
            picture=null;
        }
	}

}
