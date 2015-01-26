package com.ervacon.springframework.web.servlet.mvc.webflow;

/*
 * (c) Copyright Ervacon 2004-2005.
 * All Rights Reserved.
 */

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>Parameter extractor that obtains a parameter value from the name of
 * a request parameter. For instance, it would obtain "search" as value for
 * parameter "_event" when the request contains a parameter named "_event_search".
 * 
 * <p><b>Exposed configuration properties:</b><br>
 * <table border="1">
 *  <tr>
 *      <td><b>name</b></td>
 *      <td><b>default</b></td>
 *      <td><b>description</b></td>
 *  </tr>
 *  <tr>
 *      <td>delimiter</td>
 *      <td>_</td>
 *      <td>The delimiter used to seperate the parameter name and value.</td>
 *  </tr>
 * </table>
 * 
 * @author Erwin Vervaet
 */
public class RequestParameterNameParameterExtractor implements ParameterExtractor {
	
	public static final String DEFAULT_DELIMITER="_";
	
	private String delimiter=DEFAULT_DELIMITER;
	
	/**
	 * <p>Get the delimiter used by this parameter extractor. Defaults to "_".
	 */
	public String getDelimiter() {
		return delimiter;
	}
	
	/**
	 * <p>Set the delimiter used by this parameter extractor. Defaults to "_".
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter=delimiter;
	}

	public String extractParameter(HttpServletRequest request, String paramName) {
		Enumeration names=request.getParameterNames();
		while (names.hasMoreElements()) {
			String name=(String)names.nextElement();
			if (name.startsWith(paramName)) {
				return extractParameter(name, paramName);
			}
		}
		return null;
	}
	
	/**
	 * <p>Extract a value from given parameter, for instance extracting "search" from
	 * "_event_search" when the paramName is "_event" and the delimiter is "_".
	 * 
	 * <p>This method is only called with a param value that starts with
	 * the paramName value!
	 * 
	 * <p>If no valid value can be extracted, null is returned.
	 */
	protected String extractParameter(String param, String paramName) {
		param=param.substring(paramName.length());
		if (param.startsWith(delimiter)) {
			return param.substring(delimiter.length());
		}
		else {
			return null;
		}
	}

}
