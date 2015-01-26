package com.ervacon.springframework.web.servlet.mvc.webflow;

/*
 * (c) Copyright Ervacon 2004-2005.
 * All Rights Reserved.
 */

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ervacon.springframework.web.servlet.mvc.webflow.Action;

/**
 * <p>Simple action that takes a String value from the flow model
 * and signals (returns) it as an event. This is usefull if you want to
 * implement conditional behaviour in your flow based on the value of
 * a certain "flag" in the flow model.
 * 
 * <p><b>Exposed configuration properties:</b><br>
 * <table border="1">
 *  <tr>
 *      <td><b>name</b></th>
 *      <td><b>default</b></td>
 *      <td><b>description</b></td>
 *  </tr>
 *  <tr>
 *      <td>eventModelName</th>
 *      <td>null</td>
 *      <td>The name of the event in the flow model.</td>
 *  </tr>
 * </table>
 * 
 * @author Erwin Vervaet
 */
public class SignalEventAction implements Action {
	
	private String eventModelName;
	
	/**
	 * <p>Set the name of the event in the flow model.
	 */
	public void setEventModelName(String eventModelName) {
		this.eventModelName = eventModelName;
	}
	
	public String execute(HttpServletRequest request, HttpServletResponse response, Map model) {
		return (String)model.get(eventModelName);
	}

}
