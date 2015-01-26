package com.ervacon.springframework.samples.itemlist.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ervacon.springframework.web.servlet.mvc.webflow.Action;
import com.ervacon.springframework.web.servlet.mvc.webflow.WebFlowUtils;

public class AddItemAction implements Action {

	public String execute(HttpServletRequest request, HttpServletResponse response, Map model) {
		//check token in model
		if (!WebFlowUtils.isTokenValid(model, "token", request, "_token", true)) {
			//the token was not valid so we cannot continue normal processing
			return "tokenError";
		}
		
		List list=(List)model.get("list");
		if (list==null) {
			list=new ArrayList();
			model.put("list", list);
		}
		
		String data=request.getParameter("data");
		if (data!=null && data.length()>0) {
			list.add(data);
		}
		
		//add a bit of artificial think time
		try {
			Thread.sleep(2000);
		}
		catch (InterruptedException e) {
		}
		
		return OK;
	}

}
