// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: ConditionalTag.java,v 1.1 2002/05/23 06:54:51 dustin Exp $

package net.spy.photo.taglib;

/**
 * Tags that process a body based on a negatable condition.
 */
public class ConditionalTag extends PhotoTag {

	private boolean negate=false;

	/**
	 * Get an instance of ConditionalTag.
	 */
	public ConditionalTag() {
		super();
	}

	/**
	 * Get the value of the negate field.
	 */
	public boolean getNegate() {
		return(negate);
	}

	/**
	 * True if the value is negative.
	 */
	public boolean isNegative() {
		return(negate);
	}

	/**
	 * Set the negate value.
	 */
	public void setNegate(boolean negate) {
		this.negate=negate;
	}

	/**
	 * Set the negate value.
	 */
	public void setNegate(String negate) {
		Boolean b=new Boolean(negate);
		this.negate=b.booleanValue();
	}

	/**
	 * Get the return value for doStartTag().
	 *
	 * This will return different values under different conditions:
	 *
	 * <ul>
	 *  <li>If processValue is true and negate is false, EVAL_BODY_INCLUDE</li>
	 *  <li>If processValue is false and negate is true, SKIP_BODY</li>
	 *  <li>If processValue is true and negate is true, SKIP_BODY</li>
	 *  <li>If processValue is false and negate is false, EVAL_BODY_INCLUDE</li>
	 * </ul>
	 */
	protected int getReturnValue(boolean processValue) {
		int rv=0;
		if(processValue) {
			rv=negate?SKIP_BODY:EVAL_BODY_INCLUDE;
		} else {
			rv=negate?EVAL_BODY_INCLUDE:SKIP_BODY;
		}
		return(rv);
	}

}
