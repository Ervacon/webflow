package com.ervacon.springframework.web.servlet.mvc.webflow;

/*
 * (c) Copyright Ervacon 2004-2005.
 * All Rights Reserved.
 */

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 * <p>Externalized state of a web flow. A web flow memento holds the state
 * associated with a certain web flow on behalf of a particular client. This
 * state consist of the <b>flow name</b>, the <b>current state</b> of the flow
 * and the <b>model data</b> associated with the flow instance.
 * 
 * <p>Objects of this class are serializable, so they can be safely stored
 * in the HTTP session.
 * 
 * <p>One way of looking at objects of this class is as <i>activation frames</i>:
 * they hold state associated with a flow invokation.
 * 
 * <p>There should be little need to work directly with this class from inside
 * web application code.
 * 
 * @author Erwin Vervaet
 */
public class WebFlowMemento implements Serializable {

	private String flowName=null;
    private String currentState=null;
    private Map model=new HashMap();
    
    /**
     * <p>Create a new memento for named web flow.
     */
    public WebFlowMemento(String flowName) {
        this.flowName=flowName;
    }
    
    /**
     * <p>Get the name of the flow associated with this memento. This
     * name can be used to obtain the flow object from an application
     * context.
     */
    public String getFlowName() {
        return this.flowName;
    }
    
    /**
     * <p>Convenience method to get the flow object associated with
     * this memento from given application context.
     */
    public WebFlow getFlow(ApplicationContext appCtx) throws BeansException {
    	return (WebFlow)appCtx.getBean(flowName);
    }

	/**
	 * <p>Get the current state of the flow associated with this memento.
	 */
    public String getCurrentState() {
        return this.currentState;
    }
    
    /**
     * <p>Set the current state of the flow associated with this memento.
     */
    public void setCurrentState(String state) {
        this.currentState=state;
    }
    
    /**
     * <p>Get the flow model data maintained by this memento.
     */
    public Map getModel() {
        return this.model;
    }

}
