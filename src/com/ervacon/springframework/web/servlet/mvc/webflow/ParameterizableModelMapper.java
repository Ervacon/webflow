package com.ervacon.springframework.web.servlet.mvc.webflow;

/*
 * (c) Copyright Ervacon 2004-2005.
 * All Rights Reserved.
 */

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/**
 * <p>Simple model mapper that allows mappings to be configured in the
 * Spring application context.
 * 
 * <p><b>Exposed configuration properties:</b><br>
 * <table border="1">
 *  <tr>
 *      <td><b>name</b></td>
 *      <td><b>default</b></td>
 *      <td><b>description</b></td>
 *  </tr>
 *  <tr>
 *      <td>toMappingsList</td>
 *      <td>empty</td>
 *      <td>
 * 			Mappings executed when mapping model data <i>to</i> the sub flow.
 * 			The given list contains model entry names. The same name is
 * 			used in both parent flow and sub flow model.
 * 		</td>
 *  </tr>
 *  <tr>
 *      <td>toMappingsMap</td>
 *      <td>empty</td>
 *      <td>
 * 			Mappings executed when mapping model data <i>to</i> the sub flow.
 * 			The	keys in given list are the names of entries in the parent model
 * 			that will be mapped. The value associated with a key is the name
 * 			of the target entry in the sub flow model.
 * 		</td>
 *  </tr>
 *  <tr>
 *      <td>fromMappingsList</td>
 *      <td>empty</td>
 *      <td>
 * 			Mappings executed when mapping model data <i>from</i> the sub flow.
 * 			The given list contains model entry names. The same name is
 * 			used in both parent flow and sub flow model.
 * 		</td>
 *  </tr>
 *  <tr>
 *      <td>fromMappingsMap</td>
 *      <td>empty</td>
 *      <td>
 * 			Mappings executed when mapping model data <i>from</i> the sub flow.
 * 			The	keys in given list are the names of entries in the sub flow model
 * 			that will be mapped. The value associated with a key is the name
 * 			of the target entry in the parent model.
 * 		</td>
 *  </tr>
 * </table>
 * 
 * <p>The mappings defined using the above configuration properties fully support bean
 * property access. So an entry name in a mapping can either be "beanName" or
 * "beanName.propName". Nested property values are also supported ("beanName.propName.propName").
 * 
 * @author Erwin Vervaet
 */
public class ParameterizableModelMapper implements ModelMapper {
	
	private Map toMappings=new HashMap();
	private Map fromMappings=new HashMap();
	
	/**
	 * <p>Set the mappings that will be executed when mapping model
	 * data <i>to</i> the sub flow. All keys in given list will be mapped.
	 */
	public void setToMappingsList(List toMappingsList) {
		Iterator it=toMappingsList.iterator();
		while (it.hasNext()) {
			String key=(String)it.next();
			toMappings.put(key, key);
		}
	}

	/**
	 * <p>Set the mappings that will be executed when mapping model
	 * data <i>to</i> the sub flow. The keys are the names in the parent
	 * flow model, the corresponding values are the names in the sub
	 * flow model.
	 */
	public void setToMappingsMap(Map toMappings) {
		this.toMappings=toMappings;
	}

	/**
	 * <p>Set the mappings that will be executed when mapping model
	 * data <i>from</i> the sub flow. All keys in given list will be mapped.
	 */
	public void setFromMappingsList(List fromMappingsList) {
		Iterator it=fromMappingsList.iterator();
		while (it.hasNext()) {
			String key=(String)it.next();
			fromMappings.put(key, key);
		}
	}
	
	/**
	 * <p>Set the mappings that will be executed when mapping model
	 * data <i>from</i> the sub flow. The keys are the names in the sub
	 * flow model, the corresponding values are the names in the parent
	 * flow model.
	 */
	public void setFromMappingsMap(Map fromMappings) {
		this.fromMappings=fromMappings;
	}

	public void mapToSubFlow(Map parentFlowModel, Map subFlowModel) {
		map(parentFlowModel, subFlowModel, toMappings);
	}

	public void mapFromSubFlow(Map parentFlowModel, Map subFlowModel) {
		map(subFlowModel, parentFlowModel, fromMappings);
	}
	
	/**
	 * <p>Map data from one map to another map using specified mappings.
	 */
	protected void map(Map from, Map to, Map mappings) {
		if (mappings!=null) {
			Iterator fromKeys=mappings.keySet().iterator();
			while (fromKeys.hasNext()) {
			    //get source value
				String fromKey=(String)fromKeys.next();
				Object fromValue=from.get(fromKey);
				int idx=fromKey.indexOf('.');
				if (idx!=-1) {
				    //fromKey is something like "beanName.propName"
				    String beanName=fromKey.substring(0, idx);
				    String propName=fromKey.substring(idx+1);
				    
				    BeanWrapper bw=createBeanWrapper(from.get(beanName));
				    fromValue=bw.getPropertyValue(propName);
				}
				
				//set target value
				String toKey=(String)mappings.get(fromKey);
				idx=toKey.indexOf('.');
				if (idx!=-1) {
				    //toKey is something like "beanName.propName"
				    String beanName=toKey.substring(0, idx);
				    String propName=toKey.substring(idx+1);
				    
				    BeanWrapper bw=createBeanWrapper(to.get(beanName));
				    bw.setPropertyValue(propName, fromValue);
				}
				else {
				    to.put(toKey, fromValue);
				}
			}
		}
	}
	
	/**
	 * <p>Create a new bean wrapper wrapping given object. Can be redefined
	 * in subclasses in case special property editors need to be registered or
	 * when other similar tuning is required.
	 */
	protected BeanWrapper createBeanWrapper(Object obj) {
	    return new BeanWrapperImpl(obj);
	}

}
