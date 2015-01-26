package com.ervacon.springframework.web.servlet.mvc.webflow;

/*
 * (c) Copyright Ervacon 2004-2005.
 * All Rights Reserved.
 */

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import junit.framework.TestCase;

/**
 * <p>Test case for the SimpleWebFlow class.
 * 
 * @see com.ervacon.springframework.web.servlet.mvc.webflow.SimpleWebFlow
 * 
 * @author Erwin Vervaet
 */
public class SimpleWebFlowTest extends TestCase {
    
    private ConfigurableApplicationContext appCtx=null;

    protected void setUp() throws Exception {
        appCtx=new ClassPathXmlApplicationContext("com/ervacon/springframework/web/servlet/mvc/webflow/stocks.xml");
    }

    protected void tearDown() throws Exception {
        appCtx.close();
    }

    public void testWebFlow() {
    	String flowName="searchStockFlow";
    	WebFlow flow=(WebFlow)appCtx.getBean(flowName);
    	
        WebFlowMementoStack mementos=new WebFlowMementoStack();
        mementos.push(new WebFlowMemento(flowName));
        assertEquals(1, mementos.size());
        
        assertEquals("criteria", startFlow(mementos).getViewName());
        assertEquals("criteria", mementos.getCurrentState());
        assertSame(flow, mementos.getFlow(appCtx));
        assertEquals(1, mementos.size());
        assertEquals(0, mementos.getModel().size());
        
        assertEquals("results", executeFlow("search", mementos).getViewName());
        assertEquals("results", mementos.getCurrentState());
        assertSame(flow, mementos.getFlow(appCtx));
        assertEquals(1, mementos.size());
        assertEquals(1, actionCallCount("bindCriteriaAction"));
        assertEquals(1, actionCallCount("validateCriteriaAction"));
        assertEquals(1, actionCallCount("searchAction"));
        assertEquals("searchCriteria_value", mementos.getModel().get("searchCriteria"));
        assertEquals("searchResults_value", mementos.getModel().get("searchResults"));
        assertEquals(2, mementos.getModel().size());
        
        assertEquals("criteria", executeFlow("newSearch", mementos).getViewName());
        assertEquals("criteria", mementos.getCurrentState());
        assertSame(flow, mementos.getFlow(appCtx));
        assertEquals(1, mementos.size());
        assertEquals(2, mementos.getModel().size());
        
        assertEquals("results", executeFlow("search", mementos).getViewName());
        assertEquals("results", mementos.getCurrentState());
        assertSame(flow, mementos.getFlow(appCtx));
        assertEquals(1, mementos.size());
        assertEquals(2, actionCallCount("bindCriteriaAction"));
        assertEquals(2, actionCallCount("validateCriteriaAction"));
        assertEquals(2, actionCallCount("searchAction"));
        assertEquals("searchCriteria_value", mementos.getModel().get("searchCriteria"));
        assertEquals("searchResults_value", mementos.getModel().get("searchResults"));
        assertEquals(2, mementos.getModel().size());
        
        assertEquals("detail", executeFlow("detail", mementos).getViewName());
        assertEquals("detail", mementos.getCurrentState());
        assertNotSame(flow, mementos.getFlow(appCtx));
        assertEquals(2, mementos.size());
        assertEquals(1, actionCallCount("bindSelectionAction"));
        assertEquals(1, actionCallCount("getDetailAction"));
        assertEquals("detailData_value", mementos.getModel().get("detailData"));
        assertEquals("selectedStock_value", mementos.getModel().get("selectedStock"));
        assertEquals(2, mementos.getModel().size());
        
        assertEquals("results", executeFlow("back", mementos).getViewName());
        assertEquals("results", mementos.getCurrentState());
        assertSame(flow, mementos.getFlow(appCtx));
        assertEquals(1, mementos.size());
        assertEquals(3, mementos.getModel().size());

        assertNull(executeFlow("stop", mementos));
        assertEquals(0, mementos.size());
    }
    
    private ModelAndView startFlow(WebFlowMementoStack mementos) {
        return mementos.getFlow(appCtx).start(null, null, mementos);
    }
    
    private ModelAndView executeFlow(String event, WebFlowMementoStack mementos) {
        return mementos.getFlow(appCtx).execute(null, null, mementos.getCurrentState(), event, mementos);
    }
    
    private int actionCallCount(String name) {
        return ((TestAction)appCtx.getBean(name)).getCallCount();
    }
    
}
