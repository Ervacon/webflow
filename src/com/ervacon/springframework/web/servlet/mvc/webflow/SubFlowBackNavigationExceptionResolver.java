package com.ervacon.springframework.web.servlet.mvc.webflow;

/*
 * (c) Copyright Ervacon 2004-2005.
 * All Rights Reserved.
 */

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

/**
 * <p>Exception resolver that tries to resolve navigation problems
 * with sub flows resulting from the use of the browser <i>Back</i> button.
 * 
 * <p>The strategy employed by this exception resolver is to retry the
 * request after popping the top flow from the flow memento stack. This is
 * not a 100% full proof technique, but it does help in a lot of cases.
 * 
 * @see com.ervacon.springframework.web.servlet.mvc.webflow.NavigationException
 * 
 * @author Erwin Vervaet
 */
public class SubFlowBackNavigationExceptionResolver implements HandlerExceptionResolver, ApplicationContextAware {
	
	protected final Log log=LogFactory.getLog(SubFlowBackNavigationExceptionResolver.class);
	
	private ApplicationContext appCtx;
	
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.appCtx=applicationContext;
	}

	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		if (handler instanceof WebFlowController && ex instanceof NavigationException) {
			NavigationException nex=(NavigationException)ex;
			if (nex.getState()!=null && nex.getEvent()!=null && nex.getMementos()!=null) {
				if (log.isInfoEnabled()) {
					log.info("Trying to resolve navigation exception '" + nex + "'");
				}

				while (!nex.getMementos().empty()) {
					try {
						if (log.isDebugEnabled()) {
							log.debug("Trying to execute event '" + nex.getEvent() + "' in state '" + nex.getState() + "' of flow '" + nex.getMementos().getFlowName() + "'");
						}
						
						return nex.getMementos().getFlow(appCtx).execute(request, response, nex.getState(), nex.getEvent(), nex.getMementos());
					}
					catch (NavigationException e) {
						if (log.isDebugEnabled()) {
							log.debug("Navigation failed: '" + e + "'");
							log.debug("Popping sub flow '" + nex.getMementos().getFlowName() + "' and trying parent flow");
						}
						
						nex.getMementos().pop();
					}
				}
			}
			
			throw nex; //we couldn't fix the problem
		}
		
		return null; //default processing
	}

}
