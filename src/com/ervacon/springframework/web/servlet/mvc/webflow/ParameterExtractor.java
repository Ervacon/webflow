package com.ervacon.springframework.web.servlet.mvc.webflow;

/*
 * (c) Copyright Ervacon 2004-2005.
 * All Rights Reserved.
 */

import javax.servlet.http.HttpServletRequest;

/**
 * <p>Interface for strategy objects used by the web flow controller to
 * extract parameter values from an incoming HTTP request.
 * 
 * @author Erwin Vervaet
 */
public interface ParameterExtractor {
	
	/**
	 * <p>Extract named parameter from given HTTP request.
	 * 
	 * @param request current HTTP request
	 * @param paramName name of the parameter to extract
	 * @return value of the extracted parameter or null if the parameter does not exist
	 */
	public String extractParameter(HttpServletRequest request, String paramName);

}
