package com.ervacon.springframework.web.servlet.mvc.webflow;

/*
 * (c) Copyright Ervacon 2004-2005.
 * All Rights Reserved.
 */

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * <p>Concrete controller implementation that uses a web flow to handle client
 * requests. For more details on how to define and use web flows, consult the
 * package documentation and the web flow DTD.
 * 
 * <p>This controller requires an HTTP session to keep track of flow state. So it will
 * force the "requireSession" attribute defined by the <code>AbstractController</code>
 * to true.
 * 
 * <p>This controller recognizes the following request parameters:
 * <table border="1">
 * 	<tr>
 *      <td><b>name</b></th>
 *      <td><b>value description</b></td>
 * 	</tr>
 * 	<tr>
 *      <td>_flowId</th>
 *      <td>
 * 			The id of a previously started flow. When not present in the request,
 * 			a new flow will be started.
 *		</td>
 * 	</tr>
 * 	<tr>
 *      <td>_flowName</th>
 *      <td>
 * 			The name of the flow that should be started, which should be the name
 *          of a bean defined in the application context associated with this
 *          controller. When not present in the request, the value of the
 *          "webFlowName" property of the controller will be used.
 *		</td>
 * 	</tr>
 * 	<tr>
 *      <td>_currentState</th>
 *      <td>
 * 			State of the flow in which the event should be executed. This is not 
 *			used when starting a new flow. For an existing flow, it is optional.
 * 			This parameter can be used to freely <i>jump</i> around in a flow: you
 * 			are not restricted to the actual current state. Using it can also
 * 			make flow navigation more robust when dealing with the browser
 * 			<i>Back</i> button.
 * 		</td>
 * 	</tr>
 * 	<tr>
 *      <td>_event</th>
 *      <td>
 * 			Event that will be triggered in the current state of the flow. This is
 * 			required when accessing an existing flow and not used when starting a
 * 			new flow.
 * 		</td>
 * 	</tr>
 * </table>
 * 
 * <p>The following values will be exposed to the view via the model:
 * <table border="1">
 * 	<tr>
 *      <td><b>key</b></th>
 *      <td><b>value description</b></td>
 * 	</tr>
 * 	<tr>
 *      <td>flowId</th>
 *      <td>
 * 			The id of the executing flow. This can be used by the view to create links
 * 			that point to this same flow.
 *          <br>
 *          Actions can also use this value, e.g. to obtain the flow memento stack from
 *          the HTTP session (see {@link com.ervacon.springframework.web.servlet.mvc.webflow.WebFlowMementoStack}).
 *          However, make note that the controller can only expose the flow id in the model
 *          of the top-level flow, that is, the flow started by the controller. If you
 *          need flow id in an action executed by a sub flow, you need to include the
 *          appropriate mapping specifications to map the flow id to that sub flow!
 *		</td>
 * 	</tr>
 * 	<tr>
 *      <td>currentState</th>
 *      <td>The current state of the flow.</td>
 * 	</tr>
 * </table>
 * Note that the keys used in the model can be configured using the "flowIdModelName" and
 * "currentStateModelName" properties.
 * 
 * <p><b>Workflow:</b><br>
 * <ol>
 *  <li>
 * 		When a request comes in, the controller attempts to obtain a flow id from
 * 		the request.
 *	</li>
 * 	<li>
 * 		If no flow id is found, a new flow will be started which results in a model
 * 		and view to display. The name of the flow to start is obtained from the
 *      "_flowName" request parameter or the "webFlowName" property as a fallback
 *      value.
 * 	</li>
 * 	<li>
 * 		If a flow id is found, the controller aquires the state (memento) of that
 * 		flow from the HTTP session. It then continues this flow from its current state
 * 		(or the state specified in the "_currentState" request parameter) using the
 * 		event specified in the "_event" request parameter. This results in a model and
 * 		view to display.
 * 	</li>
 * 	<li>
 * 		The "flowId" and "currentState" values are made available to the view in the model
 * 		and both are returned to the <code>DispatcherServlet</code> for rendering.
 * 	</li>
 * </ol>
 * 
 * <p><b>Exposed configuration properties:</b><br>
 * <table border="1">
 *  <tr>
 *      <td><b>name</b></th>
 *      <td><b>default</b></td>
 *      <td><b>description</b></td>
 *  </tr>
 *  <tr>
 *      <td>webFlowName</td>
 *      <td>null</td>
 *      <td>
 * 			Name of the web flow that is used by this controller, which should
 * 			be the name of a bean defined in the application context associated
 * 			with this controller. We need the name of the flow and not the
 * 			actual flow object since we must be able to reaquire a reference
 * 			to the flow object at any moment, e.g. when returning from a sub flow.
 *          This flow name can possibly be overruled by a "_flowName" request
 *          parameter.
 *		</td>
 *  </tr>
 *  <tr>
 *      <td>parameterExtractor</td>
 *      <td>{@link RequestParameterValueParameterExtractor RPVPR}</td>
 *      <td>
 * 			Parameter extractor used to extract the value of the "_flowId", "_flowName",
 *          "_event" and "_currentState" parameters from the incoming HTTP request.
 *		</td>
 *  </tr>
 *  <tr>
 *      <td>flowIdModelName</td>
 *      <td>flowId</td>
 *      <td>The flow id will be exposed to the view using this key in the model.</td>
 *  </tr>
 *  <tr>
 *      <td>currentStateModelName</td>
 *      <td>currentState</td>
 *      <td>The current state will be exposed to the view using this key in the model.</td>
 *  </tr>
 * </table>
 * 
 * <p>This class provides several extension hook methods to fine tune its
 * behaviour in a subclass.
 * 
 * @see com.ervacon.springframework.web.servlet.mvc.webflow.WebFlow
 * @see com.ervacon.springframework.web.servlet.mvc.webflow.ParameterExtractor
 * 
 * @author Erwin Vervaet
 */
public class WebFlowController extends AbstractController implements InitializingBean {
	
	//request parameters
    
    /**
     * <p>Name of the request parameter that holds the id of a previously
     * started flow. When not present in the request, a new flow will be
     * started.
     */
    public static final String PARAM_FLOW_ID="_flowId";
    
    /**
     * <p>Name of the request parameter that holds the name of the flow
     * that should be started. When not present in the request, the value
     * of the "webFlowName" property of the controller itself will be used.
     */
    public static final String PARAM_FLOW_NAME="_flowName";
    
    /**
     * <p>Name of the request parameter that holds the current state of the
     * flow. This is optional. When not specified, the current state will
     * be obtained from the flow memento.
     */
    public static final String PARAM_CURRENT_STATE="_currentState";
    
    /**
     * <p>Name of the request parameter that specifies the event to trigger.
     */
    public static final String PARAM_EVENT="_event";
    
    
    //model keys
    
    /**
     * <p>Key of the flow id in the model exposed to the view.
     */
    public static final String FLOW_ID_MODEL_NAME="flowId";
    
    /**
     * <p>Key of the current state in the model exposed to the view.
     */
    public static final String CURRENT_STATE_MODEL_NAME="currentState";
    
    
    private String webFlowName=null;
    private ParameterExtractor parameterExtractor=new RequestParameterValueParameterExtractor();
    private String flowIdModelName=FLOW_ID_MODEL_NAME;
    private String currentStateModelName=CURRENT_STATE_MODEL_NAME;
    
    /**
     * Create a new web flow controller.
     * 
     * <p>The "cacheSeconds" property will default to 0, so no
     * caching will be done.
     */
    public WebFlowController() {
    	setCacheSeconds(0);
    }

    /**
     * <p>Get the name of the web flow executed by this controller. This flow name
     * can possibly be overruled by a {@link #PARAM_FLOW_NAME} request parameter.
     * This is the name of a bean in the application context associated with this
     * controller.
     */
    public String getWebFlowName() {
        return webFlowName;
    }
    
    /**
     * <p>Set the name of the web flow executed by this controller. This flow name
     * can possibly be overruled by a {@link #PARAM_FLOW_NAME} request parameter.
     * This is the name of a bean in the application context associated with this
     * controller.
     */
    public void setWebFlowName(String webFlowName) {
        this.webFlowName=webFlowName;
    }

    /**
     * <p>Get the parameter extractor used by the controller. Defaults
     * to <code>RequestParameterValueParameterExtractor</code>.
     */
    public ParameterExtractor getParameterExtractor() {
    	return parameterExtractor;
    }
    
    /**
     * <p>Get the parameter extractor used by the controller. Defaults
     * to <code>RequestParameterValueParameterExtractor</code>.
     */
    public void setParameterExtractor(ParameterExtractor parameterExtractor) {
    	this.parameterExtractor=parameterExtractor;
    }

	/**
	 * <p>Get the key of the flow id value in the model. Defaults to
	 * "flowId".
	 */
	public String getFlowIdModelName() {
		return flowIdModelName;
	}

	/**
	 * <p>Set the key of the flow id value in the model. Defaults to
	 * "flowId".
	 */
	public void setFlowIdModelName(String flowIdModelName) {
		this.flowIdModelName=flowIdModelName;
	}

	/**
	 * <p>Get the key of the current state value in the model. Defaults to
	 * "currentState".
	 */
	public String getCurrentStateModelName() {
		return currentStateModelName;
	}

	/**
	 * <p>Set the key of the current state value in the model. Defaults to
	 * "currentState".
	 */
	public void setCurrentStateModelName(String currentStateModelName) {
		this.currentStateModelName=currentStateModelName;
	}
        
    public void afterPropertiesSet() throws Exception {
    	//we need a session to keep track of flow state
    	setRequireSession(true);
    	
    	//verify that our flow actually exists in the application context,
    	//if it's specified
    	if (getWebFlowName()!=null) {
    		if (!getApplicationContext().containsBean(getWebFlowName()) ||
    				!(getApplicationContext().getBean(getWebFlowName()) instanceof WebFlow)) {
    			throw new IllegalArgumentException("webFlowName cannot be '" + getWebFlowName() + "', this is not the id of a web flow bean in the application context");
    		}
    	}
    }
    
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav=null;

        String flowId=getParameter(request, PARAM_FLOW_ID);
        String currentState=null;
        String event=null;
        WebFlowMementoStack mementos=null;
        
        try {
	        if (flowId==null) {
	            //start a new web flow
	            flowId=generateId();
	        
	            //determine the name of the flow to start
	            String flowName=getParameter(request, PARAM_FLOW_NAME);
	            if (flowName==null) {
	            	//fallback on the top-level flow configured for this controller
	            	flowName=getWebFlowName();
	            }
	            if (flowName==null) {
	            	throw new IllegalArgumentException(
	            			"unable to determine the name of the flow to start -- " +
	            			"either set the 'webFlowName' property of the controller or specify a '_flowName' request parameter");
	            }
	            
	            //create a flow memento stack and save it in the session
	            mementos=createMementos(flowName, flowId);
	            mementos.getModel().putAll(getModelInputData(request));
	            saveMementos(request, flowId, mementos);
	            
	            mav=getWebFlow(flowName).start(request, response, mementos);
	        }
	        else {
	            //use an existing web flow
	            mementos=loadMementos(request, flowId);
	            
	            if (mementos==null) {
	            	throw new IllegalArgumentException("unable to recover web flow memento for flow '" + flowId + "'");
	            }
	
	            currentState=getParameter(request, PARAM_CURRENT_STATE);
	            if (currentState==null) {
	            	currentState=mementos.getCurrentState();
	            }
	            
	            event=getParameter(request, PARAM_EVENT);
	            if (event==null) {
	                throw new IllegalArgumentException("make sure you provide a valid '" + PARAM_EVENT + "' parameter when submiting a request to an existing flow");
	            }
	            
	            mav=getWebFlow(mementos.getFlowName()).execute(request, response, currentState, event, mementos);
	        }
        }
        catch (NavigationException e) {
        	//enhance the navigation exception with all available information
        	if (currentState!=null) e.setState(currentState);
        	if (event!=null) e.setEvent(event);
        	if (mementos!=null) e.setMementos(mementos);
        	
        	throw e;
        }
        finally {
        	if (mementos!=null) {
        		//update last accessed time for the flow
        		mementos.touch();
        	}
        }
        
        if (mementos.empty()) {
        	//the flow ended, clean up its state
            deleteMementos(request, flowId);
        }
                
        if (mav!=null && !mementos.empty()) {
        	//expose flow id and current state to the view
            mav.addObject(getFlowIdModelName(), flowId);
            mav.addObject(getCurrentStateModelName(), mementos.getCurrentState());
        }
        
        return mav;
    }
    
    /**
     * <p>Helper method to get an actual web flow object using a name.
     */
    protected WebFlow getWebFlow(String flowName) {
    	return (WebFlow)getWebApplicationContext().getBean(flowName);
    }
    
    /**
     * <p>Get a named parameter from the request. Delegates to the configured parameter
     * extractor.
     */
    protected String getParameter(HttpServletRequest request, String paramName) {
    	return parameterExtractor.extractParameter(request, paramName);
    }
    
    /**
     * <p>Generate a pseudo unique id for a flow. This id will be used to store
     * flow state in the HTTP session, so the generated id should be unique for
     * an entire HTTP session.
     */
    protected String generateId() {
    	return WebFlowUtils.generateUniqueId();
    }
    
    /**
     * <p>Create a new memento stack for given web flow with given id.
     */
    protected WebFlowMementoStack createMementos(String flowName, String id) {
        WebFlowMementoStack mementos=new WebFlowMementoStack();
        mementos.push(new WebFlowMemento(flowName));
        
        //immediately make the flow id available in the model for possible use by actions
        mementos.getModel().put(getFlowIdModelName(), id);
        
        return mementos;
    }
    
	/**
	 * <p>Create a map of input data for a flow model. The data in the map
	 * returned by this method is put in the flow model of each top-level
	 * flow started by this controller.
	 * 
	 * <p>The default implementation just returns an empty map. Subclasses
	 * can override this if needed, e.g. picking input data from given
	 * request.
	 */
	protected Map getModelInputData(HttpServletRequest request) {
		return new HashMap();
	}
    
    /**
     * <p>Load the mementos stored in the HTTP session associated with given request
     * using given id.
     */
    protected WebFlowMementoStack loadMementos(HttpServletRequest request, String id) {
        return (WebFlowMementoStack)request.getSession().getAttribute(id);
    }

	/**
	 * <p>Save given web flow mementos in the HTTP session associated with
	 * given request using given id.
	 */
    protected void saveMementos(HttpServletRequest request, String id, WebFlowMementoStack mementos) {
        request.getSession().setAttribute(id, mementos);
    }
    
    /**
     * <p>Delete the web flow mementos stored in the HTTP session associated
     * with given request using given id.
     */
    protected void deleteMementos(HttpServletRequest request, String id) {
        request.getSession().removeAttribute(id);
    }

}
