package com.ervacon.springframework.samples.itemlist.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ervacon.springframework.web.servlet.mvc.webflow.Action;
import com.ervacon.springframework.web.servlet.mvc.webflow.WebFlowUtils;

public class NewItemAction implements Action {

	public String execute(HttpServletRequest request, HttpServletResponse response, Map model) {
		//put token in model to enable transactional processing
		WebFlowUtils.saveToken(model, "token");
		
		return OK;
	}

}
