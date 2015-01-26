package com.ervacon.springframework.web.servlet.mvc.webflow;

/*
 * (c) Copyright Ervacon 2004-2005.
 * All Rights Reserved.
 */

import java.io.Serializable;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 * <p>Stack of flow mementos. This stack is necessary since a web flow can
 * start another web flow as a sub flow, so we need to maintain a stack of
 * flow mementos: one for each nested flow invokation.
 * 
 * <p>Objects of this class are serializable, so they can be safely stored
 * in the HTTP session.
 * 
 * <p>One way of looking at an object of this class is as a <i>call stack</i>:
 * a stack of <i>activation records</i> build up when one flow calls another.
 * 
 * <p>There should be little need to work directly with this class from inside
 * web application code. Take a look at the <tt>asList()</tt> method if you
 * want to expose the stack of mementos to the application (e.g. in a view).
 * 
 * @see com.ervacon.springframework.web.servlet.mvc.webflow.WebFlowMemento
 * 
 * @author Erwin Vervaet
 * @author Steven Bazyl
 */
public class WebFlowMementoStack implements Serializable {
    
    private Stack mementos=new Stack();
    private long lastAccessedTime=System.currentTimeMillis();
    
    /**
     * <p>Returns whether or not the stack is empty.
     */
    public boolean empty() {
        return mementos.empty();
    }
    
    /**
     * <p>Returns the size (depth) of the stack.
     */
    public int size() {
        return mementos.size();
    }

	/**
	 * <p>Push given memento onto the stack.
	 */
    public void push(WebFlowMemento memento) {
        mementos.push(memento);
    }
    
    /**
     * <p>Returns the memento at the top of the stack without removing it
     * from the stack.
     * 
     * @throws EmptyStackException if the stack is empty
     */
    public WebFlowMemento peek() throws EmptyStackException {
        return (WebFlowMemento)mementos.peek();
    }
    
    /**
     * <p>Pops the top memento from the stack. If the stack is empty, null
     * will be returned.
     */
    public WebFlowMemento pop() {
        try {
            return (WebFlowMemento)mementos.pop();
        }
        catch (EmptyStackException e) {
            return null;
        }
    }
    
    /**
	 * <p>Returns the last time a flow in this memento stack processed a
	 * client request.
	 */
	public long getLastAccessedTime() {
		return lastAccessedTime;
	}
	
	/**
	 * <p>Update the last accessed time of this memento stack.
	 */
	public void touch() {
		lastAccessedTime=System.currentTimeMillis();
	}
    
    /**
     * <p>Get the stack of mementos as a list (bottom of stack is at index 0),
     * suitable for export to the page model for use in breadcrumbs or other devices.
     */
    public List asList() {
        return Collections.unmodifiableList(mementos);
    }

    //convenience methods to access the top element of the stack
	
	/**
	 * <p>Get the flow name of the memento at the top of the stack.
	 */
    public String getFlowName() {
        return peek().getFlowName();
    }
    
	/**
	 * <p>Get the flow object of the memento at the top of the stack
	 * from given application context.
	 */
    public WebFlow getFlow(ApplicationContext appCtx) throws BeansException {
    	return peek().getFlow(appCtx);
    }
    
    /**
     * <p>Get the current state of the memento at the top of the stack.
     */
    public String getCurrentState() {
        return peek().getCurrentState();
    }
    
    /**
     * <p>Set the current state of the memento at the top of the stack.
     */
    public void setCurrentState(String state) {
        peek().setCurrentState(state);
    }
     
    /**
     * <p>Get the flow model of the memento at the top of the stack.
     */   
    public Map getModel() {
        return peek().getModel();
    }

}
