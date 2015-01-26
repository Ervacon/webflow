package com.ervacon.springframework.web.servlet.mvc.webflow;

/*
 * (c) Copyright Ervacon 2004-2005.
 * All Rights Reserved.
 */

import org.springframework.core.NestedRuntimeException;

/**
 * <p>Exception that is generated when a web flow encounters an internal problem.
 * This kind of exception is typically caused by programming errors or configuration
 * problems.
 * 
 * @author Erwin Vervaet
 */
public class WebFlowException extends NestedRuntimeException {

	/**
	 * <p>Create a new web flow exception.
	 * 
	 * @param msg the detail message
	 */
	public WebFlowException(String msg) {
		super(msg);
	}

	/**
	 * <p>Create a new web flow exception caused by another problem.
	 * 
	 * @param msg the detail message
	 * @param ex the nested exception
	 */
	public WebFlowException(String msg, Throwable ex) {
		super(msg, ex);
	}

}
