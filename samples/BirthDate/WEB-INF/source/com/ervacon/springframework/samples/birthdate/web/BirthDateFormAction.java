package com.ervacon.springframework.samples.birthdate.web;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;

import com.ervacon.springframework.web.servlet.mvc.webflow.SimpleFormAction;

public class BirthDateFormAction extends SimpleFormAction {
	
	private static final String BIRTHDATE_COMMAND_NAME="birthDate";
	
	public BirthDateFormAction() {
		//tell the superclass about the command object and validator we want to use
		//you could also do this in the application context XML ofcourse 
		setCommandName(BIRTHDATE_COMMAND_NAME);
		setCommandClass(BirthDate.class);
		setValidator(new BirthDateValidator());
	}
	
	protected void initBinder(HttpServletRequest request, Map model, ServletRequestDataBinder binder) {
		//register a custom property editor to handle the date input
		SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
	}

	public String calculateAge(HttpServletRequest request, HttpServletResponse response, Map model) {
		//pull the date from the model
		BirthDate birthDate=(BirthDate)model.get(BIRTHDATE_COMMAND_NAME);
		
		//calculate the age (quick & dirty, probably has bugs :-)
		//in a real application you would delegate to the business layer for this
		//kind of logic
		Calendar calBirthDate=new GregorianCalendar();
		calBirthDate.setTime(birthDate.getDate());
		Calendar calNow=new GregorianCalendar();
		
		int ageYears=calNow.get(Calendar.YEAR) - calBirthDate.get(Calendar.YEAR);
		long ageMonths=calNow.get(Calendar.MONTH) - calBirthDate.get(Calendar.MONTH);
		long ageDays=calNow.get(Calendar.DAY_OF_MONTH) - calBirthDate.get(Calendar.DAY_OF_MONTH);
		if (ageDays<0) {
			ageMonths--;
			ageDays+=calBirthDate.getActualMaximum(Calendar.DAY_OF_MONTH);
		}
		if (ageMonths<0) {
			ageYears--;
			ageMonths+=12;
		}
		
		//create a nice age string and put it in the model for display by the view
		StringBuffer ageStr=new StringBuffer();
		ageStr.append(ageYears).append(" years, ");
		ageStr.append(ageMonths).append(" months and ");
		ageStr.append(ageDays).append(" days");
		model.put("age", ageStr);
		
		return OK;
	}

}
