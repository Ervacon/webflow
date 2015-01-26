package com.ervacon.springframework.web.servlet.mvc.webflow;

/*
 * (c) Copyright Ervacon 2004-2005.
 * All Rights Reserved.
 */

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.ModelAndView;

/**
 * <p>Simple web flow implementation. This class reads a web flow definition
 * conforming to the web flow DTD from a specified resource. This implementation
 * uses JDOM to access the XML flow definition. Several extension hook methods
 * are provided in case a subclass wants to fine tune the behaviour of this class.
 * 
 * <p>A web flow is configured in the Spring application context as a normal
 * bean. Note that this flow implementation is thread safe: all state is
 * initialized when the bean is created by the Spring application context. Once
 * this is done, all state is read only. The modifiable state associated
 * with a flow is stored in a seperate web flow memento that is associated with
 * each active flow used by a client. The client (e.g. a controller implementation)
 * is responsible for correct management of this memento.
 * 
 * <p>Normally you should not use this class directly from within a web application.
 * Instead, you use the provided <code>WebFlowController</code> to use and drive
 * the flow.
 * 
 * <p><b>Exposed configuration properties</b><br>
 * <table border="1">
 *  <tr>
 *      <td><b>name</b></td>
 *      <td><b>default</b></td>
 *      <td><b>description</b></td>
 *  </tr>
 *  <tr>
 *      <td>webFlowResource</td>
 *      <td><i>null</i></td>
 *      <td>Specifies the resource from which the flow definition is loaded.</td>
 *  </tr>
 * </table>
 * 
 * @see com.ervacon.springframework.web.servlet.mvc.webflow.WebFlowMemento
 * @see com.ervacon.springframework.web.servlet.mvc.webflow.WebFlowController
 * 
 * @author Erwin Vervaet
 */
public class SimpleWebFlow implements WebFlow, InitializingBean, ApplicationContextAware {
    
    protected final Log log=LogFactory.getLog(SimpleWebFlow.class);

	/**
	 * <p>Resource from which the flow definition is loaded.
	 */
    private Resource webFlowResource=null;
    
    /**
     * <p>The actual flow definition: a JDOM tree.
     */
    private Document webFlowDef=null;
    
    /**
     * <p>A reference to the application context containing this flow.
     */
    private ApplicationContext appCtx=null;
    
    /**
     * <p>Get the resource from which the web flow is loaded.
     */
    public Resource getWebFlowResource() {
        return webFlowResource;
    }
    
    /**
     * <p>Set the resource from which the web flow is loaded.
     */
    public void setWebFlowResource(Resource webFlowResource) {
        this.webFlowResource=webFlowResource;
    }
    
    public void afterPropertiesSet() throws Exception {
        load();
    }
    
    /**
     * <p>Load the web flow definition from the resource specified by
     * the "webFlowResource" property.
     */
    public void load() throws WebFlowException {
        try {
            if (log.isInfoEnabled()) {
                log.info("Loading web flow from " + webFlowResource);
            }
            
            SAXBuilder builder=new SAXBuilder();
            builder.setValidation(true);
            builder.setEntityResolver(new WebFlowDtdResolver());
            webFlowDef=builder.build(webFlowResource.getInputStream());
        }
        catch (IOException e) {
        	throw new WebFlowException("cannot load web flow: " + e.getMessage(), e);
        }
        catch (JDOMException e) {
        	throw new WebFlowException("problem parsing web flow definition: " + e.getMessage(), e);
        }
    }
    
    public ApplicationContext getApplicationContext() {
        return appCtx;
    }
    
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.appCtx=applicationContext;
    }
    
    /**
     * <p>Return the name of this web flow. This is configured in the flow definition.
     */
    public String getName() {
        return webFlowDef.getRootElement().getAttributeValue("name");
    }
    
    public ModelAndView start(HttpServletRequest request, HttpServletResponse response, WebFlowMementoStack mementos) throws WebFlowException {
        if (log.isInfoEnabled()) {
            log.info("Starting web flow '" + getName() + "'");
        }
        
        //find start-state and execute it
        Element startState=webFlowDef.getRootElement().getChild("start-state");
        return executeState(request, response, startState.getAttributeValue("state"), mementos);
    }
    
    public ModelAndView execute(HttpServletRequest request, HttpServletResponse response, String state, String event, WebFlowMementoStack mementos) throws WebFlowException {
        if (log.isInfoEnabled()) {
            log.info("Executing event '" + event + "' in state '" + state + "' of web flow '" + getName() + "'");
        }
        
        //find state
        Element stateDef=findStateDef(state);
        if (stateDef==null) {
            throw new NavigationException("cannot find state '" + state + "' in web flow '" + getName() + "'", state, event, mementos);
        }
        
        //trigger event
        Element triggeredTransitionDef=findTransitionDef(stateDef, event);
        if (triggeredTransitionDef==null) {
            throw new NavigationException("cannot find transition for event '" + event + "' in state '" + state + "' of flow '" + getName() + "'", state, event, mementos);
        }
        
        return executeState(request, response, triggeredTransitionDef.getAttributeValue("to"), mementos);
    }
    
    /**
     * <p>Find the state definition for specified state. Returns null if
     * the state cannot be found.
     */
    protected Element findStateDef(String state) {
        List stateDefs=webFlowDef.getRootElement().getChildren();
        for (int i=0; i<stateDefs.size(); i++) {
            Element stateDef=(Element)stateDefs.get(i);
            if (state.equals(stateDef.getAttributeValue("id"))) {
                return stateDef;
            }
        }
        return null;
    }
    
    /**
     * <p>Find the transition definition linked with specified event in
     * given state definition. Returns null if there is no such transition.
     */
    protected Element findTransitionDef(Element stateDef, String event) {
        List transitionDefs=stateDef.getChildren("transition");
        for (int i=0; i<transitionDefs.size(); i++) {
            Element transitionDef=(Element)transitionDefs.get(i);
            if (event.equals(transitionDef.getAttributeValue("name"))) {
                return transitionDef;
            }
        }
        return null;
    }

    /**
     * <p>Execute specified state.
     * 
     * @param request current HTTP request
     * @param response current HTTP response
     * @param state the state to execute
     * @param mementos flow call stack
     * @throws WebFlowException in case of error
     */
    protected ModelAndView executeState(HttpServletRequest request, HttpServletResponse response, String state, WebFlowMementoStack mementos) throws WebFlowException {
        Element stateDef=findStateDef(state);
        if (stateDef==null) {
            throw new NavigationException("cannot find state '" + state + "' in web flow '" + getName() + "'", state, null, mementos);
        }
        
    	return executeState(request, response, stateDef, mementos);
    }
    
    /**
     * <p>Execute given state.
     * 
     * @param request current HTTP request
     * @param response current HTTP response
     * @param stateDef definition of the state to execute
     * @param mementos flow call stack
     * @throws WebFlowException in case of error
     */
    protected ModelAndView executeState(HttpServletRequest request, HttpServletResponse response, Element stateDef, WebFlowMementoStack mementos) throws WebFlowException {
    	String state=stateDef.getAttributeValue("id");
        mementos.setCurrentState(state);

        if ("view-state".equals(stateDef.getName())) {
        	return executeViewState(request, response, stateDef, mementos);
        }
        else if ("action-state".equals(stateDef.getName())) {
        	return executeActionState(request, response, stateDef, mementos);
        }
        else if ("flow-state".equals(stateDef.getName())) {
        	return executeFlowState(request, response, stateDef, mementos);
        }
        else if ("end-state".equals(stateDef.getName())) {
        	return executeEndState(request, response, stateDef, mementos);
        }
        else {
            throw new WebFlowException("unknown state type: " + stateDef.getName());
        }
    }

    /**
     * <p>Execute given view state. This will return the model and view to render.
     * 
     * @param request current HTTP request
     * @param response current HTTP response
     * @param stateDef definition of the state to execute
     * @param mementos flow call stack
     * @throws WebFlowException in case of error
     */
    protected ModelAndView executeViewState(HttpServletRequest request, HttpServletResponse response, Element stateDef, WebFlowMementoStack mementos) throws WebFlowException {
        String viewStateView=stateDef.getAttributeValue("view");

        if (log.isInfoEnabled()) {
            log.info("Executing view state '" + stateDef.getAttributeValue("id") + "' of flow '" + getName() + "': displaying view '" + viewStateView + "'");
        }
        
        if (viewStateView!=null) {
        	ModelAndView mav=new ModelAndView(viewStateView);
        	mav.addAllObjects(mementos.getModel());
        	return mav;
        }
        else {
        	return null;
        }
    }

    /**
     * <p>Execute given action state. This will execute all actions defined for
     * the state untill a transition is triggered. Once that happens, a transition
     * to the target state is done.
     * 
     * @param request current HTTP request
     * @param response current HTTP response
     * @param stateDef definition of the state to execute
     * @param mementos flow call stack
     * @throws WebFlowException in case of error
     */
    protected ModelAndView executeActionState(HttpServletRequest request, HttpServletResponse response, Element stateDef, WebFlowMementoStack mementos) throws WebFlowException {
        Element triggeredTransitionDef=null;
        
        String state=stateDef.getAttributeValue("id");

        if (log.isInfoEnabled()) {
            log.info("Executing action state '" + state + "' of flow '" + getName() + "'");
        }

        //execute all actions untill a transition is triggered
        List actionDefs=stateDef.getChildren("action");
        for (int i=0; i<actionDefs.size(); i++) {
            Element actionDef=(Element)actionDefs.get(i);
            String actionName=actionDef.getAttributeValue("name");
            Action action=(Action)appCtx.getBean(actionDef.getAttributeValue("bean"));
            
            if (log.isDebugEnabled()) {
                log.debug("Executing action '" + action + "'");
            }
            
            String event=action.execute(request, response, mementos.peek().getModel());
            
            if (log.isDebugEnabled()) {
                log.debug("Action '" + action + "' signaled event '" + event + "'");
            }
            
            if (event==null) {
            	throw new WebFlowException("action '" + action + "' signaled an invalid null event");
            }
            
            String realEvent=new StringBuffer(actionName).append('.').append(event).toString();
            triggeredTransitionDef=findTransitionDef(stateDef, realEvent);
            if (triggeredTransitionDef!=null) {
                if (log.isInfoEnabled()) {
                    log.info(
                            "Action '" + action + "' triggered transition '" + triggeredTransitionDef.getAttributeValue("name") +
                            "', switching to state '" + triggeredTransitionDef.getAttributeValue("to") + "'");
                }
                
                break;
            }
        }
        
        if (triggeredTransitionDef==null) {
            throw new WebFlowException("no transition was triggered during execution of action state " + state);
        }
        
        //transition to the next state
        return executeState(request, response, triggeredTransitionDef.getAttributeValue("to"), mementos);
    }

    /**
     * <p>Execute given flow state. This will load and execute a sub flow and do the
     * necessary mapping to the sub flow model.
     * 
     * @param request current HTTP request
     * @param response current HTTP response
     * @param stateDef definition of the state to execute
     * @param mementos flow call stack
     * @throws WebFlowException in case of error
     */
    protected ModelAndView executeFlowState(HttpServletRequest request, HttpServletResponse response, Element stateDef, WebFlowMementoStack mementos) throws WebFlowException {
        //get the indicated sub flow
        String subFlowName=stateDef.getAttributeValue("flow");
        SimpleWebFlow subFlow=(SimpleWebFlow)appCtx.getBean(subFlowName);
        
        if (log.isInfoEnabled()) {
            log.info("Executing flow state '" + stateDef.getAttributeValue("id") + "' of flow '" + getName() + "': starting sub flow '" + subFlow.getName() + "'");
        }
        
        //setup a memento for the subflow & map data (if necessary)
        
        //do the necessary input mapping
        WebFlowMemento subFlowMemento=new WebFlowMemento(subFlowName);
        if (stateDef.getAttributeValue("model-mapper")!=null) {
            ModelMapper mapper=(ModelMapper)appCtx.getBean(stateDef.getAttributeValue("model-mapper"));
            mapper.mapToSubFlow(mementos.getModel(), subFlowMemento.getModel());
        }

        //start the subflow
        mementos.push(subFlowMemento);
        return subFlow.start(request, response, mementos);
    }

    /**
     * <p>Execute given end state. This terminates the flow and does the necessary
     * mapping to the parent flow if there is one.
     * 
     * @param request current HTTP request
     * @param response current HTTP response
     * @param stateDef definition of the state to execute
     * @param mementos flow call stack
     * @throws WebFlowException in case of error
     */
    protected ModelAndView executeEndState(HttpServletRequest request, HttpServletResponse response, Element stateDef, WebFlowMementoStack mementos) throws WebFlowException {
    	ModelAndView mav=null;

        if (log.isInfoEnabled()) {
            log.info("Executing end state '" + stateDef.getAttributeValue("id") + "' of flow '" + getName() + "'");
        }
    	
        String endStateView=stateDef.getAttributeValue("view");
        if (endStateView!=null) {
            mav=new ModelAndView(endStateView);
            mav.addAllObjects(mementos.getModel());
        }
        
        //this flow is finished, remove its memento
        WebFlowMemento poppedMemento=mementos.pop();
        
        //check if there is a parent flow
        if (!mementos.empty()) {
            if (log.isInfoEnabled()) {
                log.info(
                        "Continuing parent flow '" + mementos.getFlowName() + "' in state '" + mementos.getCurrentState() +
                        "' using event '" + poppedMemento.getCurrentState() + "'");
            }
            
            //do output mapping if necessary
            Element parentStateDef=((SimpleWebFlow)mementos.getFlow(getApplicationContext())).findStateDef(mementos.getCurrentState());
            if (parentStateDef.getAttributeValue("model-mapper")!=null) {
                ModelMapper mapper=(ModelMapper)appCtx.getBean(parentStateDef.getAttributeValue("model-mapper"));
                mapper.mapFromSubFlow(mementos.getModel(), poppedMemento.getModel());
            }
            
            mav=mementos.getFlow(getApplicationContext()).execute(request, response, mementos.getCurrentState(), poppedMemento.getCurrentState(), mementos);
        }
        
        return mav;
    }

    public String toString() {
        StringBuffer res=new StringBuffer();
        res.append("WebFlow '").append(getName()).append("' (").append(getWebFlowResource()).append(")");
        return res.toString();
    }

}
