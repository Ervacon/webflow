package com.ervacon.springframework.web.servlet.mvc.webflow;

/*
 * (c) Copyright Ervacon 2004-2005.
 * All Rights Reserved.
 */

import java.util.Map;

/**
 * <p>Interface for model data mappers. These objects map model data from a
 * parent flow to a sub flow and back again in a <i>flow-state</i> of a web flow.
 * 
 * <p>Model mappers are configured as beans in a Spring application context.
 * A mapper that is configured as a singleton bean in the application context
 * should be thread safe! If you have a mapper that is not thread safe, make sure
 * you add the <i>singleton="false"</i> parameter to its bean definition.<br>
 * If a mapper needs access to the application context, it can implement an
 * interface like <code>ApplicationContextAware</code>.
 * 
 * <p>Note that mappers cannot generate checked exceptions since exceptions during
 * model mapping normally indicate an unrecoverable error (e.g. a programming bug).
 * 
 * @see com.ervacon.springframework.web.servlet.mvc.webflow.WebFlow
 * @see org.springframework.context.ApplicationContext
 * @see org.springframework.context.ApplicationContextAware
 * 
 * @author Erwin Vervaet
 */
public interface ModelMapper {

    /**
     * <p>Map model data from the parent flow to the sub flow. This is called
     * before the sub flow is started.
     * 
     * @param parentFlowModel parent flow model
     * @param subFlowModel sub flow model
     */
    public void mapToSubFlow(Map parentFlowModel, Map subFlowModel);

    /**
     * <p>Map model data from the sub flow back to the parent flow. This is called
     * after the sub flow completes and before the parent flow continues.
     * 
     * @param parentFlowModel parent flow model
     * @param subFlowModel sub flow model
     */
    public void mapFromSubFlow(Map parentFlowModel, Map subFlowModel);
    
}
