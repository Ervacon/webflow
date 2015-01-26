package com.ervacon.springframework.web.servlet.mvc.webflow;

/*
 * (c) Copyright Ervacon 2004-2005.
 * All Rights Reserved.
 */

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ervacon.springframework.web.servlet.mvc.webflow.Action;
import com.ervacon.springframework.web.servlet.mvc.webflow.WebFlowException;

/**
 * <p>Action implementation that bundles many action execution methods into
 * a single action implementation class. Action execution methods defined
 * by subclasses should follow the following signature:
 * <pre>
 * public String executeMethodName(HttpServletRequest request, HttpServletResponse response, Map model)
 * </pre>
 * By default, the executeMethodName will be the name of the <b>current state</b>
 * of the flow, so the follow state definition
 * <pre>
 * &lt;action-state id="search"&gt;
 * 	&lt;action name="searchAct" bean="myMultiAction"/&gt;
 * 	&lt;transition name="searchAct.ok" to="results"/&gt;
 * &lt;/action-state&gt;
 * </pre>
 * will execute the method
 * <pre>
 * public String search(HttpServletRequest request, HttpServletResponse response, Map model)
 * </pre>
 * Note that action execution methods should not throw checked exceptions! They should
 * signal an appropriate event or wrap the exception in a runtime exception.
 * 
 * <p>A typical use of the MultiAction is to combine all CRUD operations for
 * a certain domain object into a single controller. This action also allows you
 * to reduce the number of action beans you have to configure in the application
 * context
 * 
 * <p><b>Exposed configuration properties:</b><br>
 * <table border="1">
 *  <tr>
 *      <td><b>name</b></th>
 *      <td><b>default</b></td>
 *      <td><b>description</b></td>
 *  </tr>
 *  <tr>
 *      <td>delegate</th>
 *      <td>this</td>
 *      <td>The delegate object holding the execution methods.</td>
 *  </tr>
 * </table>
 * 
 * @author Erwin Vervaet
 */
public abstract class MultiAction implements Action {

	private Object delegate = this;

	/**
	 * <p>Returns the delegate object holding the execution methods. Defaults
	 * to this object.
	 */
	protected Object getDelegate() {
		return delegate;
	}

	/**
	 * <p>Set the delegate object holding the execution methods.
	 * @param delegate The delegate to set.
	 */
	protected void setDelegate(Object delegate) {
		this.delegate = delegate;
	}
	
	/**
	 * <p>Get the name of the current state of the flow executing this action.
	 * 
	 * <p>The default implementation uses the
	 * {@link WebFlowUtils#getWebFlowMementoStack(HttpServletRequest, Map)}
	 * method, and thus assumes that the web flow id is available under its
	 * default id {@link WebFlowController#FLOW_ID_MODEL_NAME} in the model.
	 * If you have used {@link WebFlowController#setFlowIdModelName(String)}
	 * to change the name of the flow id in the model, redefine this method
	 * and call {@link WebFlowUtils#getWebFlowMementoStack(HttpServletRequest, Map, String)}
	 * with the correct name.
	 * 
     * @param request current HTTP request
     * @param model model of the flow
	 * @return the name of the current state
	 */
	protected String getCurrentState(HttpServletRequest request, Map model) {
		return WebFlowUtils.getWebFlowMementoStack(request, model).getCurrentState();
	}

	/**
	 * <p>Derive the name of an execution method from the name of the current
	 * state of the web flow.
	 * 
	 * <p>The default implementation just uses the name of the current state
	 * as execution method name.
	 * 
	 * @param currentState the name of the current state of the flow
	 * @return the execution method name
	 */
	protected String getExecuteMethodName(String currentState) {
		return currentState;
	}
	
	/**
	 * <p>Get the action execution method with given name on the delegate object.
	 * Execution methods should be of the form:
	 * <pre>
	 * public String executeMethodName(HttpServletRequest request, HttpServletResponse response, Map model)
	 * </pre>
	 * 
	 * @param executeMethodName name of the execution method
	 * @return the execution method
	 * @throws NoSuchMethodException when the method cannot be found on the delegate
	 * @throws IllegalAccessException when the method is not accessible on the delegate
	 */
	protected Method getExecuteMethod(String executeMethodName) throws NoSuchMethodException, IllegalAccessException {
		return getDelegate().getClass().getMethod(executeMethodName, new Class[] { HttpServletRequest.class, HttpServletResponse.class, Map.class });
	}

	public String execute(HttpServletRequest request, HttpServletResponse response, Map model) {
		String executeMethodName=getExecuteMethodName(getCurrentState(request, model));
		try {
			Method executeMethod=getExecuteMethod(executeMethodName);
			return (String)executeMethod.invoke(getDelegate(), new Object[] { request, response, model });
		}
		catch (NoSuchMethodException e) {
			throw new WebFlowException("there is no execution method named '" + executeMethodName + "' on class '" + getDelegate().getClass() + "'", e);
		}
		catch (IllegalAccessException e) {
			throw new WebFlowException("cannot access execution method named '" + executeMethodName + "' on class '" + getDelegate().getClass() + "': make sure it is a public method", e);
		}
		catch (InvocationTargetException e) {
			Throwable t=e.getTargetException();
			if (t instanceof RuntimeException) {
				throw (RuntimeException)t;
			}
			else if (t instanceof Error) {
				throw (Error)t;
			}
			else {
				throw new WebFlowException("execution method '" + executeMethodName + "' on class '" + getDelegate().getClass() + "' generated an illegal checked exception", e);
			}
		}
	}

}
