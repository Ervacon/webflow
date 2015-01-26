package com.ervacon.springframework.samples.phonebook.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ervacon.springframework.samples.phonebook.domain.Person;
import com.ervacon.springframework.samples.phonebook.domain.PhoneBook;
import com.ervacon.springframework.samples.phonebook.domain.UserId;
import com.ervacon.springframework.web.servlet.mvc.webflow.Action;

public class GetPersonAction implements Action {

	private PhoneBook phoneBook;
	
	public void setPhoneBook(PhoneBook phoneBook) {
		this.phoneBook=phoneBook;
	}

	public String execute(HttpServletRequest request, HttpServletResponse response, Map model) {
		UserId userId=(UserId)model.get("id");
		Person person=phoneBook.getPerson(userId);
		if (person!=null) {
			model.put("person", person);
			return OK;
		}
		else {
			return ERROR;
		}
	}

}
