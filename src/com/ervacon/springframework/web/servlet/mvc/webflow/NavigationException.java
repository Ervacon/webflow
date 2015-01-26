package com.ervacon.springframework.web.servlet.mvc.webflow;

/*
 * (c) Copyright Ervacon 2004-2005.
 * All Rights Reserved.
 */

/**
 * <p>Exception that is generated when a web flow encounters navigation problems,
 * for instance when a state or transition cannot be found. This is typically caused
 * by a user using the browser <i>Back</i> button.
 * 
 * <p>This exception tracks some flow state information that might be used
 * trying to resolve the problem. Note that this information is optional, so it can
 * be null!
 * 
 * @author Erwin Vervaet
 */
public class NavigationException extends WebFlowException {

	private String state;
	private String event;
	private WebFlowMementoStack mementos;

	/**
	 * <p>Create a new navigation exception.
	 * 
	 * @param msg the detail message
	 * @param state the target state of the flow when the exception was generated, can be null
	 * @param event the event being processed when the exception was generated, can be null
	 * @param mementos the flow memento stack at the time the exception was generated, can be null
	 */
	public NavigationException(String msg, String state, String event, WebFlowMementoStack mementos) {
		super(msg);
		this.state=state;
		this.event=event;
		this.mementos=mementos;
	}

	/**
	 * <p>Get the target state of the flow when the exception was generated.
	 * This property is optional so the value can be null.
	 */
	public String getState() {
		return state;
	}
	
	/**
	 * <p>Set the target state of the flow when the exception was generated.
	 * This property is optional so the value can be null.
	 */
	public void setState(String state) {
		this.state=state;
	}
	
	/**
	 * <p>Get the event being processed when the exception was generated.
	 * This property is optional so the value can be null.
	 */
	public String getEvent() {
		return event;
	}
	
	/**
	 * <p>Set the event being processed when the exception was generated.
	 * This property is optional so the value can be null.
	 */
	public void setEvent(String event) {
		this.event=event;
	}
	
	/**
	 * <p>Get the flow memento stack at the time the exception was generated.
	 * This property is optional so the value can be null.
	 */
	public WebFlowMementoStack getMementos() {
		return mementos;
	}
	
	/**
	 * <p>Set the flow memento stack at the time the exception was generated.
	 * This property is optional so the value can be null.
	 */
	public void setMementos(WebFlowMementoStack mementos) {
		this.mementos=mementos;
	}

}
