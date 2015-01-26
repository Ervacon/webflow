package com.ervacon.springframework.web.servlet.mvc.webflow;

/*
 * (c) Copyright Ervacon 2004-2005.
 * All Rights Reserved.
 */

import java.util.Map;

import javax.naming.OperationNotSupportedException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * <p>Action implementation which creates an object (the command object) on
 * execution and attempts to populate this object with request parameters. 
 * If there are binding or validation errors, an "error" event will be
 * signaled. Otherwise "ok" is signaled.
 * 
 * <p>The bind & validate functionality of this class is similar to that offered by
 * the <code>AbstractCommandController</code> and <code>BaseCommandController</code>.
 * Check the JavaDoc of those classes for exact details on the exposed
 * configuration properties and hook methods. Check the <code>SimpleFormAction</code>
 * if you are looking to implement a flow with <code>SimpleFormController</code>-like
 * behaviour.
 * 
 * <p><b>Exposed configuration properties</b><br>
 * <table border="1">
 *  <tr>
 *      <td><b>name</b></td>
 *      <td><b>default</b></td>
 *      <td><b>description</b></td>
 *  </tr>
 *  <tr>
 *      <td>reuseCommand</td>
 *      <td>false</td>
 *      <td>
 * 			Indicates whether or not an existing command object in the flow model
 * 			should be reused. When set to false, a new command object will be
 * 			created and stored in the flow model for each request.
 * 		</td>
 *  </tr>
 * </table>
 * </p>
 * 
 * @see org.springframework.web.servlet.mvc.BaseCommandController
 * @see org.springframework.web.servlet.mvc.AbstractCommandController
 * @see com.ervacon.springframework.web.servlet.mvc.webflow.SimpleFormAction
 * @see org.springframework.web.servlet.mvc.SimpleFormController
 * 
 * @author Erwin Vervaet
 */
public class BindAndValidateCommandAction extends BaseCommandController implements Action {
	
	/*
	 * Implementation note: this class subclasses the BaseCommandController to
	 * inherit all its behaviour and avoid copy&paste. However, it is NOT a controller
	 * but an action!
	 */
	
	private boolean reuseCommand=false;
	
	/**
	 * <p>Indicates whether or not an existing command object in the flow model
	 * should be reused. Defaults to false, so each call will result in a new
	 * command object being created.
	 */
	public boolean isReuseCommand() {
		return reuseCommand;
	}
	
	/**
	 * <p>Set whether or not an existing command object in the flow model
	 * should be reused. 
	 */
	public void setReuseCommand(boolean reuseCommand) {
		this.reuseCommand=reuseCommand;
	}
	
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		throw new OperationNotSupportedException("BindAndValidateCommandAction does not support handleRequestInternal()");
	}

	public String execute(HttpServletRequest request, HttpServletResponse response, Map model) {
		try {
			Object command=getCommand(request, model);
			ServletRequestDataBinder binder=bindAndValidate(request, command);
			model.putAll(binder.getErrors().getModel());
			
			return binder.getErrors().hasErrors() ? ERROR : OK;
		}
		catch (Exception e) {
			//this should not normally happen and is likely due to a programming bug
			throw new WebFlowException("cannot get, bind or validate command object: " + e.getMessage(), e);
		}
	}

	/**
	 * <p>Retrieve a command object for the given request from given flow model.
	 * If "reuseCommand" is set, an existing command object in the flow model will
	 * be reused. If not set or when the command is not present in the model, a
	 * new command will be created using the <code>createCommand</code> method.
	 * 
	 * @param request current HTTP request
	 * @param model model of the flow
	 * @return object command to bind onto
	 * @see #createCommand
	 */
	protected Object getCommand(HttpServletRequest request, Map model) throws Exception {
		if (isReuseCommand() && model.containsKey(getCommandName())) {
			return model.get(getCommandName());
		}
		else {
			return createCommand();
		}
	}

}
