package com.ervacon.springframework.web.servlet.mvc.webflow;

/*
 * (c) Copyright Ervacon 2004-2005.
 * All Rights Reserved.
 */

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * <p>Servlet 2.3 filter that cleans up expired web flows in the HTTP
 * session associated with the request being filtered. A flow has expired
 * when it has not handled any requests for more that a specified timeout
 * period.
 * 
 * <p>This filter can be configured in the <tt>web.xml</tt> deployment
 * descriptor of your web application. Here's an example:
 * <pre>
 * &lt;!DOCTYPE web-app PUBLIC "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
 * 	"http://java.sun.com/dtd/web-app_2_3.dtd"&gt;
 * &lt;web-app&gt;
 * 	&lt;filter&gt;
 * 		&lt;filter-name&gt;webFlowCleanup&lt;/filter-name&gt;
 * 		&lt;filter-class&gt;com.ervacon.springframework.web.servlet.mvc.webflow.WebFlowCleanupFilter&lt;/filter-class&gt;
 * 	&lt;/filter&gt;
 * 	&lt;filter-mapping&gt;
 * 		&lt;filter-name&gt;webFlowCleanup&lt;/filter-name&gt;
 * 		&lt;url-pattern&gt;/*&lt;/url-pattern&gt;
 * 	&lt;/filter-mapping&gt;
 * 
 * 	...
 * </pre>
 * 
 * <p><b>Exposed configuration properties:</b><br>
 * <table border="1">
 *  <tr>
 *      <td><b>name</b></th>
 *      <td><b>default</b></td>
 *      <td><b>description</b></td>
 *  </tr>
 *  <tr>
 *      <td>timeout</td>
 *      <td>10</td>
 *      <td>
 * 			Specifies the flow timeout in <b>minutes</b>. If the flow
 * 			is inactive for more that this period of time it will expire
 * 			and be removed from the HTTP session.
 *		</td>
 *  </tr>
 * </table>
 * These parameters can be configured using <tt>init-param</tt> values
 * in the deployment descriptor.
 * 
 * @author Erwin Vervaet
 */
public class WebFlowCleanupFilter extends OncePerRequestFilter {
	
	/**
	 * <p>Default web flow timout: 10 minutes.
	 */
	public static final int DEFAULT_TIMEOUT=10;
	
	private int timeout=DEFAULT_TIMEOUT; //in minutes
	
	/**
	 * <p>Get the flow timout (expiry), expressed in minutes.
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * <p>Set the flow timout (expiry), expressed in minutes.
	 */
	public void setTimeout(int timeout) {
		this.timeout=timeout;
	}
	
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		doCleanup(request);
		filterChain.doFilter(request, response);
	}
	
	/**
	 * <p>Remove expired flows from the HTTP session associated with given request.
	 */
	protected void doCleanup(HttpServletRequest request) {
		//get the session if there is one
		HttpSession session=request.getSession(false);
		if (session==null) {
			return;
		}
		
		//execute the cleanup process
		Set namesToBeDeleted=new HashSet();
		
		Enumeration names=session.getAttributeNames();
		while (names.hasMoreElements()) {
			String name=(String)names.nextElement();
			Object value=session.getAttribute(name);
			if (value instanceof WebFlowMementoStack) {
				WebFlowMementoStack mementos=(WebFlowMementoStack)value;
				if (hasExpired(request, mementos)) {
					namesToBeDeleted.add(name);
					if (logger.isInfoEnabled()) {
						logger.info("Flow '" + mementos.getFlowName() + "' with id '" + name + "' has expired and will be removed from the HTTP session '" + session.getId() + "'");
					}
				}
			}
		}
		
		Iterator it=namesToBeDeleted.iterator();
		while (it.hasNext()) {
			session.removeAttribute((String)it.next());
		}
	}
	
	/**
	 * <p>Check if given web flow memento stack, found in the session associated with
	 * given request, has expired.
	 * 
	 * <p>Subclasses can override this method if they want to change the expiry logic,
	 * e.g. to keep flows alive in certain situations.
	 *  
	 * @param request current HTTP request
	 * @param mementos the web flow memento stack that needs to be checked for expiry
	 */
	protected boolean hasExpired(HttpServletRequest request, WebFlowMementoStack mementos) {
		return (System.currentTimeMillis()-mementos.getLastAccessedTime()) > (getTimeout()*60000);
	}

}
