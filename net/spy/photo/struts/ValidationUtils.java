// Copyright (c) 2003  Dustin Sallings <dustin@spy.net>
//
// $Id: ValidationUtils.java,v 1.1 2003/05/26 08:02:52 dustin Exp $

package net.spy.photo.struts;

import org.apache.commons.validator.ValidatorAction;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.ValidatorUtil;
import org.apache.commons.validator.GenericValidator;
import org.apache.struts.validator.Resources;
import org.apache.struts.action.ActionErrors;

import javax.servlet.http.HttpServletRequest;

/**
 * Validators and stuff.
 */
public class ValidationUtils extends Object {

	// Static methods only
	private ValidationUtils() {
		super();
	}

	/** 
	 * Validate the current field matches another field.
	 */
	public static boolean validateTwoFields(Object bean, ValidatorAction va,
												Field field,
												ActionErrors errors,
												HttpServletRequest request) {

		// True until proven false
		boolean rv=true;
		String value =
			ValidatorUtil.getValueAsString(bean, field.getProperty());
		String sProperty2 = field.getVarValue("secondproperty");
		System.err.println("Getting " + sProperty2
			+ " (from " + field + ") with " + bean);
		String value2 = ValidatorUtil.getValueAsString(bean, sProperty2);

		if (!GenericValidator.isBlankOrNull(value)) {
			try {
				if (!value.equals(value2)) {
					errors.add(field.getKey(),
						Resources.getActionError(request, va, field));
					rv=false;
				}
			} catch(Exception e) {
				errors.add(field.getKey(), Resources.getActionError(request,
					va, field));
				rv=false;
			}
		}

		return(rv);
	}
}
