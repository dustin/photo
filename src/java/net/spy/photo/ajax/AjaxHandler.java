// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>
// arch-tag: 02336988-112C-4656-B05E-BF58135FA2ED

package net.spy.photo.ajax;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.spy.photo.User;

/**
 * Annotation for defining sub-servlet access.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AjaxHandler {

	/** 
	 * The subpath.
	 */
	String path();

	/** 
	 * Role required for this request.
	 */
	String role() default User.ADMIN;
}
