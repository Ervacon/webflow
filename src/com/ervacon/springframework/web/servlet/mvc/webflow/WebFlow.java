package com.ervacon.springframework.web.servlet.mvc.webflow;

/*
 * (c) Copyright Ervacon 2004-2005.
 * All Rights Reserved.
 */

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

/**
 * <p>Interface implemented by all web flow implementations. A web flow
 * will normally be defined in an XML document conforming to the web flow
 * DTD. For more details on how to define and use web flows, consult the
 * package documentation and the web flow DTD.
 * 
 * <p>A basic implementation is provided by the <code>SimpleWebFlow</code>.
 * 
 * @see com.ervacon.springframework.web.servlet.mvc.webflow.SimpleWebFlow
 * 
 * @author Erwin Vervaet
 */
public interface WebFlow {

    /**
     * <p>Start the web flow.
     * 
     * @param request current HTTP request
     * @param response current HTTP response
     * @param mementos flow call stack
     * @throws WebFlowException in case of error
     */
    public ModelAndView start(HttpServletRequest request, HttpServletResponse response, WebFlowMementoStack mementos) throws WebFlowException;

    /**
     * <p>Execute an event in a state of the web flow.
     * 
     * @param request current HTTP request
     * @param response current HTTP response
     * @param state state in which the event should be triggered
     * @param event event that should be signaled
     * @param mementos flow call stack
     * @throws WebFlowException in case of error
     */
    public ModelAndView execute(HttpServletRequest request, HttpServletResponse response, String state, String event, WebFlowMementoStack mementos) throws WebFlowException;

}
