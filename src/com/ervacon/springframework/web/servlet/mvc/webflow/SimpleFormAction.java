package com.ervacon.springframework.web.servlet.mvc.webflow;

/*
 * (c) Copyright Ervacon 2004-2005.
 * All Rights Reserved.
 */

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.validation.BindException;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * <p>Multi-action that implements logic that is very similar to that of the
 * <code>SimpleFormController</code>. Two execution methods are provided:
 * <ul>
 * 	<li>
 * 		{@link #prepareNewForm(HttpServletRequest, HttpServletResponse, Map)} -
 * 		Prepares a command object for display in a new form. This will initialize
 * 		the binder so that all custom property editors are available for use in the
 * 		new form. This action method always returns (signals) the "ok" event.
 * 	</li>
 * 	<li>
 * 		{@link #bindAndValidate(HttpServletRequest, HttpServletResponse, Map)} -
 * 		Bind all incoming request parameters to the command object and validate
 * 		the command object using any registered validators. This action method
 * 		will return (signal) the "ok" event if there are no binding or validation
 * 		errors, otherwise it will return the "error" event.
 * 	</li>
 * </ul>
 * Since this is a multi-action, a subclass could add any number of additional
 * action execution methods, e.g. on "onSubmit()".
 * 
 * <p>Using this action, it becomes very easy to implement form preparation and
 * submission logic in your flow:
 * <ol>
 * 	<li>
 * 		Start of with an action state called "prepareNewForm". This will invoke
 * 		{@link #prepareNewForm(HttpServletRequest, HttpServletResponse, Map) prepareNewForm} to
 * 		prepare the new form for display.
 * 	</li>
 * 	<li>
 * 		Now show the form using a view state.
 * 	</li>
 * 	<li>
 * 		Go to an action state called "bindAndValidate" when the form is submitted.
 * 		This will invoke {@link #bindAndValidate(HttpServletRequest, HttpServletResponse, Map) bindAndValidate}
 * 		to bind incoming request data to the command object and validate the command
 * 		object. If there are binding or validation errors, go back to the previous
 * 		view state to redisplay the form with error messages.
 * 	</li>
 * 	<li>
 * 		If binding and validation was successful, go to an action state called "onSubmit"
 *      (or any other appropriate name). This will invoke an action method called "onSubmit"
 * 		you must provide on a subclass to process form submission, e.g. interacting with
 * 		the business logic.
 * 	</li>
 * 	<li>
 * 		If business processing is ok, contine to a view state to display the success view.
 * 	</li>
 * </ol>
 * Check the "BirthDate" sample application for a usage example of this action.
 * 
 * <p>The most important hook method provided by this class is the method
 * {@link #initBinder(HttpServletRequest, Map, ServletRequestDataBinder) initBinder}. This will be
 * called after a new data binder is created by both
 * {@link #prepareNewForm(HttpServletRequest, HttpServletResponse, Map) prepareNewForm} and
 * {@link #bindAndValidate(HttpServletRequest, HttpServletResponse, Map) bindAndValidate}. It allows you
 * to register any custom property editors required by the form and command object.
 * 
 * <p>Note that this class does not provide "suppressValidation" or "isFormChangeRequest"
 * hook methods like the SimpleFormController. This is not required because in a web flow you
 * have exact control over what happens when certain events are signaled. So a
 * <i>form change request</i> would just use a different event than the <i>submit</i> request. 
 * 
 * <p><b>Exposed configuration properties</b><br>
 * <table border="1">
 *  <tr>
 *      <td><b>name</b></td>
 *      <td><b>default</b></td>
 *      <td><b>description</b></td>
 *  </tr>
 *  <tr>
 *      <td>commandName</td>
 *      <td>"command"</td>
 *      <td>
 * 			The name of the command in the model. The command object will be included
 * 			in the model under this name.
 * 		</td>
 *  </tr>
 *  <tr>
 *      <td>commandClass</td>
 *      <td>null</td>
 *      <td>
 * 			The command class for this action. An instance of this class will get
 * 			populated and validated.
 * 		</td>
 *  </tr>
 *  <tr>
 *      <td>reuseCommand</td>
 *      <td>false</td>
 *      <td>
 * 			Indicates whether or not an existing command object in the flow model
 * 			should be reused.
 * 		</td>
 *  </tr>
 *  <tr>
 *      <td>requestErrors</td>
 *      <td>false</td>
 *      <td>
 * 			Indicates whether or not to expose the Errors object resulting from
 * 			validation in the request instead of the flow model. This is usefull
 * 			if you want to avoid redisplaying <i>old</i> errors. Defaults to false,
 * 			so the Errors will be exposed in the model.
 * 		</td>
 *  </tr>
 *  <tr>
 *      <td>validator(s)</td>
 *      <td>empty</td>
 *      <td>
 * 			The validators for this action. The validators must support the
 * 			specified command class.
 * 		</td>
 *  </tr>
 *  <tr>
 *      <td>bindOnNewForm</td>
 *      <td>false</td>
 *      <td>
 * 			Set if request parameters should be bound to the form object
 * 			during the {@link #prepareNewForm(HttpServletRequest, HttpServletResponse, Map) prepareNewForm}
 * 			action.
 * 		</td>
 *  </tr>
 *  <tr>
 *      <td>validateOnBinding</td>
 *      <td>true</td>
 *      <td>
 * 			Indicates if the validators should get applied when binding.
 * 		</td>
 *  </tr>
 *  <tr>
 *      <td>messageCodesResolver</td>
 *      <td>null</td>
 *      <td>
 * 			Set the strategy to use for resolving errors into message codes.
 * 		</td>
 *  </tr>
 * </table>
 * 
 * @see org.springframework.web.servlet.mvc.SimpleFormController
 * 
 * @author Erwin Vervaet
 */
public class SimpleFormAction extends MultiAction implements InitializingBean {
	
	private String commandName=BaseCommandController.DEFAULT_COMMAND_NAME;
	private Class commandClass;
	private boolean reuseCommand=false;
	private boolean requestErrors=false;
	private Validator[] validators;
	private boolean bindOnNewForm=false;
	private boolean validateOnBinding=true;
	private MessageCodesResolver messageCodesResolver;
	
	/**
	 * <p>Return the name of the command in the model.
	 */
	public String getCommandName() {
		return commandName;
	}
	
	/**
	 * <p>Set the name of the command in the model.
	 * The command object will be included in the model under this name.
	 */
	public void setCommandName(String commandName) {
		this.commandName=commandName;
	}
	
	/**
	 * <p>Return the command class for this action.
	 */
	public Class getCommandClass() {
		return commandClass;
	}
	
	/**
	 * <p>Set the command class for this action.
	 * An instance of this class will get populated and validated.
	 */
	public void setCommandClass(Class commandClass) {
		this.commandClass=commandClass;
	}
	
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
	
	/**
	 * <p>Returns whether or not to expose the Errors object resulting from
	 * validation in the request instead of the flow model. This is usefull
	 * if you want to avoid redisplaying <i>old</i> errors. Defaults to false,
	 * so the Errors will be exposed in the model.
	 */
	public boolean isRequestErrors() {
		return requestErrors;
	}

	/**
	 * <p>Set whether or not to expose the Errors object resulting from
	 * validation in the request instead of the flow model.
	 */
	public void setRequestErrors(boolean requestErrors) {
		this.requestErrors=requestErrors;
	}
	
	/**
	 * <p>Returns all the validators for this action.
	 */
	public Validator[] getValidators() {
		return validators;
	}

	/**
	 * <p>Set the validators for this action.
	 * The validators must support the specified command class.
	 */
	public void setValidators(Validator[] validators) {
		this.validators=validators;
	}

	/**
	 * <p>Returns the primary validator for this action.
	 */
	public Validator getValidator() {
		return (validators!=null && validators.length>0 ? validators[0] : null);
	}
	
	/**
	 * <p>Set the primary validator for this action. The validator
	 * must support the specified command class. If there are one
	 * or more existing validators set already when this method is
	 * called, only the specified validator will be kept. Use
	 * {@link #setValidators(Validator[])} to set multiple validators.
	 */
	public void setValidator(Validator validator) {
		this.validators=new Validator[] {validator};
	}
	
	/**
	 * <p>Returns if request parameters should be bound to the form object
	 * during the {@link #prepareNewForm(HttpServletRequest, HttpServletResponse, Map)} action.
	 * Defaults to false.
	 */
	public boolean isBindOnNewForm() {
		return bindOnNewForm;
	}
	
	/**
	 * <p>Set if request parameters should be bound to the form object
	 * during the {@link #prepareNewForm(HttpServletRequest, HttpServletResponse, Map)} action.
	 */
	public void setBindOnNewForm(boolean bindOnNewForm) {
		this.bindOnNewForm=bindOnNewForm;
	}
	
	/**
	 * <p>Return if the validators should get applied when binding. Defaults to true.
	 */
	public boolean isValidateOnBinding() {
		return validateOnBinding;
	}
	
	/**
	 * <p>Set if the validators should get applied when binding.
	 */
	public void setValidateOnBinding(boolean validateOnBinding) {
		this.validateOnBinding=validateOnBinding;
	}
	
	/**
	 * <p>Return the strategy to use for resolving errors into message codes.
	 */
	public MessageCodesResolver getMessageCodesResolver() {
		return messageCodesResolver;
	}
	
	/**
	 * <p>Set the strategy to use for resolving errors into message codes.
	 * Applies the given strategy to all data binders used by this action.
	 * 
	 * <p>Default is null, i.e. using the default strategy of the data binder.
	 * 
	 * @see #createBinder(HttpServletRequest, Map, Object)
	 * @see org.springframework.validation.DataBinder#setMessageCodesResolver(org.springframework.validation.MessageCodesResolver)
	 */
	public void setMessageCodesResolver(MessageCodesResolver messageCodesResolver) {
		this.messageCodesResolver=messageCodesResolver;
	}
	
	public void afterPropertiesSet() throws Exception {
		if (getValidators()!=null) {
			for (int i=0; i<getValidators().length; i++) {
				if (getCommandClass()!=null && !getValidators()[i].supports(getCommandClass())) {
					throw new IllegalArgumentException("Validator [" + getValidators()[i] +	"] does not support command class [" + getCommandClass() + "]");
				}
			}
		}
	}
	
	//execution methods
	
	/**
	 * <p>Prepares a command object for display in a new form. This will initialize
	 * the binder so that all custom property editors are available for use in the
	 * new form.
	 * 
	 * <p>If the "bindOnNewForm" property is set, a bind and validate step will be
	 * done to pre-populate the new form with incoming request parameters.
	 * 
	 * @param request current HTTP request
	 * @param response current HTTP response
     * @param model model of the flow
	 * @return "ok"
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#showNewForm(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public String prepareNewForm(HttpServletRequest request, HttpServletResponse response, Map model) {
		Object command=formBackingObject(request, model);
		ServletRequestDataBinder binder;
		if (isBindOnNewForm()) {
			binder=bindAndValidate(request, model, command);
		}
		else {
			binder=createBinder(request, model, command);
		}
		exposeCommandAndErrors(request, model, command, binder.getErrors());
		return OK;
	}

	/**
	 * <p>Bind all incoming request parameters to the command object and validate
	 * the command object using any registered validators.
	 * 
	 * @param request current HTTP request
	 * @param response current HTTP response
     * @param model model of the flow
	 * @return "ok" when binding and validation is successful, "error" otherwise
	 * 
	 * @see BaseCommandController#bindAndValidate(javax.servlet.http.HttpServletRequest, java.lang.Object)
	 */
	public String bindAndValidate(HttpServletRequest request, HttpServletResponse response, Map model) {
		Object command=formBackingObject(request, model);
		ServletRequestDataBinder binder=bindAndValidate(request, model, command);
		exposeCommandAndErrors(request, model, command, binder.getErrors());
		return binder.getErrors().hasErrors() ? ERROR : OK;
	}
	
	//internal methods
	
	/**
	 * <p>Retrieve a backing object for the current form from the given request or
	 * flow model.
	 * 
	 * <p>The properties of the form object will correspond to the form field values
	 * in your form view. This object will be exposed in the model under the specified
	 * command name, to be accessed under that name in the view: for example, with
	 * a "spring:bind" tag. The default command name is "command".
	 * 
	 * <p>Note that you need to activate "reuseCommand" mode to reuse the form-backing
	 * object across the entire form workflow. Else, a new instance of the command
	 * class will be created for each submission attempt, just using this backing
	 * object as template for the initial form.
	 * 
	 * <p>Default implementation calls <code>createCommand()</code>, creating a new
	 * empty instance of the command class. Subclasses can override this to provide
	 * a preinitialized backing object.
	 * 
	 * @param request current HTTP request
     * @param model model of the flow
	 * @return the backing object
	 * 
	 * @see #setCommandName(String)
	 * @see #setCommandClass(Class)
	 * @see #createCommand()
	 */
	protected Object formBackingObject(HttpServletRequest request, Map model) {
		if (isReuseCommand() && model.containsKey(getCommandName())) {
			return model.get(getCommandName());
		}
		else {
			return createCommand();
		}
	}

	/**
	 * <p>Create a new command instance for the command class of this action.
	 * 
	 * @return the new command instance
	 */
	protected Object createCommand() {
		if (getCommandClass()==null) {
			throw new IllegalStateException(
					"cannot create command without commandClass being set: " +
					"either set commandClass or override formBackingObject");
		}
		try {
			return getCommandClass().newInstance();
		}
		catch (InstantiationException e) {
			throw new WebFlowException("cannot instantiate command class '" + getCommandClass() + "'", e);
		}
		catch (IllegalAccessException e) {
			throw new WebFlowException("cannot access command class '" + getCommandClass() + "' constructor: make sure it is public", e);
		}
	}

	/**
	 * <p>Bind the parameters of the given request to the given command object.
	 * 
	 * @param request current HTTP request
     * @param model model of the flow
	 * @param command the command to bind onto
	 * @return the ServletRequestDataBinder instance for additional custom validation
	 */
	protected ServletRequestDataBinder bindAndValidate(HttpServletRequest request, Map model, Object command) {
		ServletRequestDataBinder binder=createBinder(request, model, command);
		binder.bind(request);
		onBind(request, model, command, binder.getErrors());
		if (getValidators()!=null && isValidateOnBinding()) {
			for (int i=0; i<getValidators().length; i++) {
				ValidationUtils.invokeValidator(getValidators()[i], command, binder.getErrors());
			}
		}
		onBindAndValidate(request, model, command, binder.getErrors());
		return binder;
	}

	/**
	 * <p>Create a new binder instance for the given command, request and model.
	 * Called by <code>bindAndValidate()</code>. Can be overridden to plug in custom
	 * ServletRequestDataBinder subclasses.
	 * 
	 * <p>Default implementation creates a standard ServletRequestDataBinder,
	 * sets the specified MessageCodesResolver (if any), and invokes initBinder().
	 * Note that initBinder() will not be invoked if you override this method!
	 * 
	 * @param request current HTTP request
     * @param model model of the flow
	 * @param command the command to bind onto
	 * @return the new binder instance
	 * 
	 * @see #bindAndValidate(HttpServletRequest, Map, Object)
	 * @see #initBinder(HttpServletRequest, Map, ServletRequestDataBinder)
	 * @see #setMessageCodesResolver(MessageCodesResolver)
	 */
	protected ServletRequestDataBinder createBinder(HttpServletRequest request, Map model, Object command) {
		ServletRequestDataBinder binder=new ServletRequestDataBinder(command, getCommandName());
		if (getMessageCodesResolver()!=null) {
			binder.setMessageCodesResolver(getMessageCodesResolver());
		}
		initBinder(request, model, binder);
		return binder;
	}
	
	/**
	 * <p>Expose the command object and related errors object to the view.
	 * The command object will always be put in the flow model. If the
	 * "requestErrors" property is set, the errors will be exposed in the
	 * request. If not set, they will also be exposed in the flow model.
	 * 
	 * @param request current HTTP request
     * @param model model of the flow
	 * @param command the command object
	 * @param errors the errors object
	 */
	protected void exposeCommandAndErrors(HttpServletRequest request, Map model, Object command, BindException errors) {
		if (isRequestErrors()) {
			model.put(getCommandName(), command);
			request.setAttribute(BindException.ERROR_KEY_PREFIX + getCommandName(), errors);
		}
		else {
			model.putAll(errors.getModel());
		}
	}
	
	//hook methods

	/**
	 * <p>Callback for custom post-processing in terms of binding.
	 * Called on each submit, after standard binding but before validation.
	 * 
	 * <p>Default implementation is empty.
	 * 
	 * @param request current HTTP request
     * @param model model of the flow
	 * @param command the command object to perform further binding on
	 * @param errors validation errors holder, allowing for additional custom registration of binding errors
	 * 
	 * @see #bindAndValidate(HttpServletRequest, Map, Object)
	 */
	protected void onBind(HttpServletRequest request, Map model, Object command, BindException errors) {
	}

	/**
	 * <p>Callback for custom post-processing in terms of binding and validation.
	 * Called on each submit, after standard binding and validation,
	 * but before error evaluation.
	 * 
	 * <p>Default implementation is empty.
	 * 
	 * @param request current HTTP request
     * @param model model of the flow
	 * @param command the command object, still allowing for further binding
	 * @param errors validation errors holder, allowing for additional custom validation
	 * @throws Exception in case of invalid state or arguments
	 * 
	 * @see #bindAndValidate(HttpServletRequest, Map, Object)
	 * @see org.springframework.validation.Errors
	 */
	protected void onBindAndValidate(HttpServletRequest request, Map model, Object command, BindException errors) {
	}

	/**
	 * <p>Initialize the given binder instance, for example with custom editors.
	 * Called by createBinder().
	 * 
	 * <p>This method allows you to register custom editors for certain fields of your
	 * command class. For instance, you will be able to transform Date objects into a
	 * String pattern and back, in order to allow your JavaBeans to have Date properties
	 * and still be able to set and display them in an HTML interface.
	 * 
	 * <p>Default implementation is empty.
	 * 
	 * @param request current HTTP request
     * @param model model of the flow
	 * @param binder new binder instance
	 * 
	 * @see #createBinder(HttpServletRequest, Map, Object)
	 * @see org.springframework.validation.DataBinder#registerCustomEditor(java.lang.Class, java.beans.PropertyEditor)
	 */
	protected void initBinder(HttpServletRequest request, Map model, ServletRequestDataBinder binder) {
	}

}
