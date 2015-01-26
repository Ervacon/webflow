package com.ervacon.springframework.web.servlet.mvc.webflow;

/*
 * (c) Copyright Ervacon 2004-2005.
 * All Rights Reserved.
 */

import java.io.IOException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * <p>EntityResolver implementation for the Spring web flow DTD, to load the DTD
 * from the classpath. The implementation is similar to that of the Spring
 * <code>org.springframework.beans.factory.xml.BeansDtdResolver</code>.
 * 
 * @author Erwin Vervaet
 */
public class WebFlowDtdResolver implements EntityResolver {

    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        if (systemId!=null && systemId.indexOf("web-flow")>systemId.lastIndexOf("/")) {
            String dtdFile = systemId.substring(systemId.indexOf("web-flow"));
            try {
                Resource resource=new ClassPathResource("/com/ervacon/springframework/web/servlet/mvc/webflow/" + dtdFile, getClass());
                InputSource source=new InputSource(resource.getInputStream());
                source.setPublicId(publicId);
                source.setSystemId(systemId);
                return source;
            }
            catch (IOException ex) {
                //fall trough below
            }
        }
        return null; //let the parser handle it
    }

}