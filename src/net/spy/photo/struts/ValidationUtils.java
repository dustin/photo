// Copyright (c) 2003  Dustin Sallings <dustin@spy.net>
// arch-tag: 3FF1E695-5D6E-11D9-909B-000A957659CC

package net.spy.photo.struts;

import org.apache.commons.validator.ValidatorAction;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.ValidatorUtil;
import org.apache.commons.validator.GenericValidator;
import org.apache.struts.validator.Resources;
import org.apache.struts.action.ActionErrors;

import javax.servlet.http.HttpServletRequest;

import net.spy.photo.PhotoDimensionsImpl;

/**
 * Validators and stuff.
 */
public class ValidationUtils extends Object {

	// Static methods only
	private ValidationUtils() {
		super();
	}

	/** 
	 * Validate the current field is a dimension.
	 */
	public static boolean validateDimension(Object bean, ValidatorAction va,
												Field field,
												ActionErrors errors,
												HttpServletRequest request) {
		boolean rv=true;
		String value =
			ValidatorUtil.getValueAsString(bean, field.getProperty());

		// Missing values are OK
		if (!GenericValidator.isBlankOrNull(value)) {
			try {
				new PhotoDimensionsImpl(value);
			} catch(IllegalArgumentException e) {
				rv=false;
				errors.add(field.getKey(),
					Resources.getActionError(request, va, field));
			}
		}

		return(rv);
	}

}
