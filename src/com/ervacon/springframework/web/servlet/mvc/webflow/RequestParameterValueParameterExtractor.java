package com.ervacon.springframework.web.servlet.mvc.webflow;

/*
 * (c) Copyright Ervacon 2004-2005.
 * All Rights Reserved.
 */

import javax.servlet.http.HttpServletRequest;

/**
 * <p>Simple parameter extractor that just obtains the parmeter value from the request.
 * 
 * @author Erwin Vervaet
 */
public class RequestParameterValueParameterExtractor implements ParameterExtractor {

	public String extractParameter(HttpServletRequest request, String paramName) {
		return request.getParameter(paramName);
	}

}
