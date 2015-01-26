package com.ervacon.springframework.web.servlet.mvc.webflow;

/*
 * (c) Copyright Ervacon 2004-2005.
 * All Rights Reserved.
 */

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>Simple action used during web flow unit tests.
 * 
 * @author Erwin Vervaet
 */
public class TestAction implements Action {
    
    private String event=null;
    private String modelDataName=null;
    
    private int callCount=0;
    
    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event=event;
    }
    
    public String getModelDataName() {
        return modelDataName;
    }
    
    public void setModelDataName(String modelDataName) {
        this.modelDataName=modelDataName;
    }
    
    public int getCallCount() {
        return callCount;
    }
    
    public String execute(HttpServletRequest request, HttpServletResponse response, Map model) {
        callCount++;
        if (modelDataName!=null) {
            model.put(modelDataName, modelDataName + "_value");
        }
        return getEvent();
    }

}
