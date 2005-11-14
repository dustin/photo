// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>
// arch-tag: 02336988-112C-4656-B05E-BF58135FA2ED

package net.spy.photo.ajax;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
	 * If true, guest is allowed to make this request.
	 */
	boolean guest() default false;

	/** 
	 * Role required for this request.
	 */
	String role() default "admin";
}
