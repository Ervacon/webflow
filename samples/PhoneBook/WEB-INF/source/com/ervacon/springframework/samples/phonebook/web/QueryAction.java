package com.ervacon.springframework.samples.phonebook.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ervacon.springframework.samples.phonebook.domain.PhoneBook;
import com.ervacon.springframework.samples.phonebook.domain.PhoneBookQuery;
import com.ervacon.springframework.web.servlet.mvc.webflow.Action;

public class QueryAction implements Action {
	
	private PhoneBook phoneBook;
	
	public void setPhoneBook(PhoneBook phoneBook) {
		this.phoneBook=phoneBook;
	}

	public String execute(HttpServletRequest request, HttpServletResponse response, Map model) {
		PhoneBookQuery query=(PhoneBookQuery)model.get("query");
		model.put("persons", phoneBook.query(query));
		return OK;
	}

}
