package com.ervacon.springframework.samples.fileupload.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import com.ervacon.springframework.web.servlet.mvc.webflow.BindAndValidateCommandAction;

public class ProcessUploadAction extends BindAndValidateCommandAction {
	
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        // to actually be able to convert a multipart object to a byte[]
        // we have to register a custom editor (in this case the ByteArrayMultipartFileEditor)
        binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
        // now Spring knows how to handle multipart objects and convert them
    }

}
