package com.ervacon.springframework.web.servlet.mvc.webflow;

/*
 * (c) Copyright Ervacon 2004-2005.
 * All Rights Reserved.
 */

import junit.framework.TestCase;

/**
 * <p>Test case for the RequestParameterNameParameterExtractor class.
 * 
 * @see com.ervacon.springframework.web.servlet.mvc.webflow.RequestParameterNameParameterExtractor
 * 
 * @author Erwin Vervaet
 */
public class RequestParameterNameParameterExtractorTest extends TestCase {

	public void testExtractParameterStringString() {
		RequestParameterNameParameterExtractor pe=new RequestParameterNameParameterExtractor();
		
		assertEquals("search", pe.extractParameter("_event_search", "_event"));
		assertEquals("", pe.extractParameter("_event_", "_event"));
		assertNull(pe.extractParameter("_event/search", "_event"));
		assertNull(pe.extractParameter("_event", "_event"));
		
		pe.setDelimiter(":");

		assertEquals("search", pe.extractParameter("_event:search", "_event"));
		assertEquals("", pe.extractParameter("_event:", "_event"));
		assertNull(pe.extractParameter("_event/search", "_event"));
		assertNull(pe.extractParameter("_event", "_event"));
	}

}
