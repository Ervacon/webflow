package com.ervacon.springframework.web.servlet.mvc.webflow;

/*
 * (c) Copyright Ervacon 2004-2005.
 * All Rights Reserved.
 */

/**
 * <p>Bean used in unit tests.
 * 
 * @author Erwin Vervaet
 */
public class TestBean {
    
    private String prop;
    private TestBean otherBean;
    
    public TestBean() {
    }
    
    public TestBean(String propValue) {
        this.prop=propValue;
    }
    
    public String getProp() {
        return prop;
    }
    
    public void setProp(String value) {
        this.prop=value;
    }
    
    public TestBean getOtherBean() {
        return otherBean;
    }
    
    public void setOtherBean(TestBean bean) {
        this.otherBean=bean;
    }

}
