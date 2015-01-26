package com.ervacon.springframework.web.servlet.mvc.webflow;

/*
 * (c) Copyright Ervacon 2004-2005.
 * All Rights Reserved.
 */

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>Interface for actions executed by a web flow. Actions are executed in
 * an <i>action-state</i> of the web flow. They signal an event that allows
 * the flow to continue.
 * 
 * <p>Actions are configured as beans in a Spring application context. An action
 * that is configured as a singleton bean in the application context should be
 * thread safe! If you have an action that is not thread safe, make sure you
 * add the <i>singleton="false"</i> parameter to its bean definition.<br>
 * If an action needs access to the application context, it can implement an
 * interface like <code>ApplicationContextAware</code>.
 * 
 * @see com.ervacon.springframework.web.servlet.mvc.webflow.WebFlow
 * @see org.springframework.context.ApplicationContext
 * @see org.springframework.context.ApplicationContextAware
 * 
 * @author Erwin Vervaet
 */
public interface Action {
	
	/**
	 * Name of the event used to indicate that action processing was ok.
	 * You are not required to use this, this is just the conventional
	 * name used for such an event. 
	 */
	public static final String OK = "ok";
	
	/**
	 * Name of the event used to indicate that action processing encountered
	 * an error. You are not required to use this, this is just the conventional
	 * name used for such an event. 
	 */
	public static final String ERROR = "error";

    /**
     * <p>Execute the action and return and signal an event that allows the containing
     * flow to continue. A null return value is <i>not</i> allowed: an action must return
     * a valid event!
     * 
     * <p>Note that actions cannot throw checked exceptions. They should signal an appropriate
     * event or wrap the exception in a runtime exception.
     * 
     * @param request current HTTP request
     * @param response current HTTP response
     * @param model model of the flow
     * @return event that is signaled by the action, cannot be null
     */
    public String execute(HttpServletRequest request, HttpServletResponse response, Map model);

}
