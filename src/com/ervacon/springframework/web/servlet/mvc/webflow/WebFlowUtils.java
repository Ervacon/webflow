package com.ervacon.springframework.web.servlet.mvc.webflow;

/*
 * (c) Copyright Ervacon 2004-2005.
 * All Rights Reserved.
 */

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>Static utility methods used by the Spring web flow system and
 * applications that use it.
 * 
 * <p>This class provides web transaction token handling methods
 * similar to those available in the Struts framework. In essense
 * an implementation of the <a href="http://www.javajunkies.org/index.pl?lastnode_id=3361&node_id=3355">synchronizer token</a> pattern.
 * You can use this to prevent double submits in the following way:
 * <ul>
 * 	<li>
 * 		Create an action that will mark the beginning of the transactional
 * 		part of your flow. In this action you do <code>WebFlowUtils.saveToken(model, "token")</code>
 * 		to put a unique transaction token in the flow model.
 * 	</li>
 * 	<li>
 * 		On a page inside the transactional part of the flow, add the
 * 		token to the request that will be send to the controller. When
 * 		you're using an HTML form, you can use a hidden field to do this:
 * 		<code>&lt;INPUT type="hidden" name="_token" value="&lt;%=request.getAttribute("token") %&gt;"&gt;</code>
 * 	</li>
 * 	<li>
 * 		Finally, check the token using <code>WebFlowUtils.isTokenValid(model, "token", request, "_token", true)</code>
 * 		in the action that processes the transactional data. If the token
 * 		is valid you do real processing, otherwise you return some event
 * 		to indicate an alternative outcome (e.g. an error page).
 * 	</li>
 * </ul>
 * 
 * @author Erwin Vervaet
 */
public class WebFlowUtils {

	/**
	 * <p>No need to instantiate this class. 
	 */
	private WebFlowUtils() {
	}
	
	/**
	 * <p>Generate a pseudo unique id. This implementation uses the
	 * system time and a random number to generate an id. The generated
	 * id should be unique enough for flow ids or tokens, but it is
	 * certainly not a globally unique id.
	 */
	public static String generateUniqueId() {
        StringBuffer id=new StringBuffer();
        id.append(System.currentTimeMillis());
        id.append((int)(Math.random() * 100000000d));
        while (id.length()<20) {
            id.append('0');
        }
        return id.toString();
	}

	/**
	 * <p>Retreive the web flow memento stack for the currently executing flow.
	 * This method will first obtain the id of the currently executing flow from
	 * given model using the {@link WebFlowController#FLOW_ID_MODEL_NAME} name.
	 * With this id, it will get the flow memento stack from the session
	 * associated with given request.
	 * 
     * @param request current HTTP request
     * @param model model of the flow
	 * @return the memento stack of the currently executing flow
	 */
	public static WebFlowMementoStack getWebFlowMementoStack(HttpServletRequest request, Map model) {
		return getWebFlowMementoStack(request, model, WebFlowController.FLOW_ID_MODEL_NAME);
	}

	/**
	 * <p>Retreive the web flow memento stack for the currently executing flow.
	 * This method will first obtain the id of the currently executing flow from
	 * given model using the given name. With this id, it will get the flow memento
	 * stack from the session associated with given request.
	 * 
	 * <p>Use this method if you changed the name of the flow id in the model
	 * using the {@link WebFlowController#setFlowIdModelName(String)} method.
	 * 
     * @param request current HTTP request
     * @param model model of the flow
	 * @param flowIdModelName name of the flow id in the model
	 * @return the memento stack of the currently executing flow
	 */
	public static WebFlowMementoStack getWebFlowMementoStack(HttpServletRequest request, Map model, String flowIdModelName) {
		String flowId=(String)model.get(flowIdModelName);
		return (WebFlowMementoStack)request.getSession(false).getAttribute(flowId);
	}
	
	//token related functionality like in Struts

    /**
     * <p>Save a new transaction token in given model.
     * 
     * @param model the model map where the generated token should be saved
     * @param tokenName the key used to save the token in the model map
     */
    public static void saveToken(Map model, String tokenName) {
		String token=generateUniqueId();
    	synchronized (model) {
    		model.put(tokenName, token);
    	}
    }

    /**
     * <p>Reset the saved transaction token in given model. This
     * indicates that transactional token checking will not be needed
     * on the next request that is submitted.
     * 
     * @param model the model map where the generated token should be saved
     * @param tokenName the key used to save the token in the model map
     */
    public static void resetToken(Map model, String tokenName) {
    	synchronized (model) {
    		model.remove(tokenName);
    	}
    }

    /**
     * <p>Return <code>true</code> if there is a transaction token stored in
     * given model, and the value submitted as a request
     * parameter with this action matches it. Returns <code>false</code> when
     * <ul>
     * 	<li>there is no transaction token saved in the model</li>
     * 	<li>there is no transaction token included as a request parameter</li>
     * 	<li>
     * 		the included transaction token value does not match the
     *  	transaction token in the model
     *	</li>
     * </ul>
     *
     * @param model the model map where the token is stored
     * @param tokenName the key used to save the token in the model map
     * @param request current HTTP request
     * @param requestTokenName name of the request parameter holding the token
     * @param reset indicates whether or not the token should be reset after checking it
     * @return true when the token is valid, false otherwise
     */
    public static boolean isTokenValid(Map model, String tokenName, HttpServletRequest request, String requestTokenName, boolean reset) {
    	synchronized (model) {
	    	String modelToken=(String)model.get(tokenName);
	    	if (modelToken==null) {
	    		return false;
	    	}
	
	        if (reset) {
	            resetToken(model, tokenName);
	        }
	
	        String requestToken=request.getParameter(requestTokenName);
	        if (requestToken==null) {
	            return false;
	        }
	
	        return modelToken.equals(requestToken);
    	}
    }

}
