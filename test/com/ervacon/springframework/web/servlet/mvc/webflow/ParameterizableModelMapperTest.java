package com.ervacon.springframework.web.servlet.mvc.webflow;

/*
 * (c) Copyright Ervacon 2004-2005.
 * All Rights Reserved.
 */

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * <p>Test case for the ParameterizableModelMapper class.
 * 
 * @see com.ervacon.springframework.web.servlet.mvc.webflow.ParameterizableModelMapper
 * 
 * @author Erwin Vervaet
 */
public class ParameterizableModelMapperTest extends TestCase {

    private ParameterizableModelMapper mapper=new ParameterizableModelMapper();
    private Map from=new HashMap();
    private Map to=new HashMap();
    private Map mappings=new HashMap();

    protected void tearDown() throws Exception {
        from.clear();
        to.clear();
        mappings.clear();
    }
    
    public void testNullMapping() {
        mapper.map(from, to, null);
        assertEquals(0, from.size());
        assertEquals(0, to.size());
        
        mapper.map(from, to, mappings);
        assertEquals(0, from.size());
        assertEquals(0, to.size());
    }
    
    public void testSimpleBeanMapping() {
        TestBean bean=new TestBean("value");
        from.put("bean", bean);
        mappings.put("bean", "bean");
        mapper.map(from, to, mappings);
        assertEquals(1, from.size());
        assertEquals(1, to.size());
        assertTrue(from.containsKey("bean"));
        assertTrue(to.containsKey("bean"));
        assertSame(bean, to.get("bean"));
    }
    
    public void testNameChangeBeanMapping() {
        TestBean bean=new TestBean("value");
        from.put("bean", bean);
        mappings.put("bean", "otherBean");
        mapper.map(from, to, mappings);
        assertEquals(1, from.size());
        assertEquals(1, to.size());
        assertTrue(from.containsKey("bean"));
        assertTrue(to.containsKey("otherBean"));
        assertSame(bean, to.get("otherBean"));
    }
    
    public void testSimpleBeanPropertyMapping() {
        TestBean bean1=new TestBean("value1");
        TestBean bean2=new TestBean("value2");
        from.put("bean1", bean1);
        to.put("bean2", bean2);
        mappings.put("bean1.prop", "bean2.prop");
        mapper.map(from, to, mappings);
        assertEquals(1, from.size());
        assertEquals(1, to.size());
        assertTrue(from.containsKey("bean1"));
        assertTrue(to.containsKey("bean2"));
        assertSame(bean2, to.get("bean2"));
        assertNotSame(bean1, to.get("bean2"));
        assertEquals("value1", bean2.getProp());
    }
    
    public void testNestedBeanPropertyMapping() {
        TestBean bean1=new TestBean("value1");
        bean1.setOtherBean(new TestBean("otherBeanValue"));
        TestBean bean2=new TestBean("value2");
        from.put("bean1", bean1);
        to.put("bean2", bean2);
        mappings.put("bean1.otherBean.prop", "bean2.prop");
        mapper.map(from, to, mappings);
        assertEquals(1, from.size());
        assertEquals(1, to.size());
        assertTrue(from.containsKey("bean1"));
        assertTrue(to.containsKey("bean2"));
        assertSame(bean2, to.get("bean2"));
        assertNotSame(bean1, to.get("bean2"));
        assertEquals("otherBeanValue", bean2.getProp());
        assertNull(bean2.getOtherBean());
    }

}
