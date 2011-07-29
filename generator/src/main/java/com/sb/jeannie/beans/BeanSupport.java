package com.sb.jeannie.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;

import com.sb.jeannie.KeyValuePrettyPrinter;
import com.sb.jeannie.utils.Utils;

public abstract class BeanSupport {
	protected static final String INDEX = "index";

	public BeanSupport() {
		super();
	}
	
	abstract protected Map<String, String> getPropertyMap();
	abstract public Map<String, String> getProps();
	
    public static void log(Logger LOG, Map<String, String> propertyMap, Map<String, String> props) {
		List<String> lines = indexList(propertyMap, props);
		for (String line : lines) {
			LOG.info(line);
		}
    }

    public static String getIndex(Map<String, String> propertyMap, Map<String, String> props) {
    	StringBuilder sb = new StringBuilder();
        List<String> lines = indexList(propertyMap, props);
        for (String line : lines) {
			sb.append(line);
			sb.append(Utils.NL);
		}
        return sb.toString();
    }
    
    public static List<String> indexList(
    		Map<String, String> propertyMap, 
    		Map<String, String> props
    ) {
    	List<String> result = new ArrayList<String>();
    	KeyValuePrettyPrinter prettyPrinter = new KeyValuePrettyPrinter();
    	Set<String> keys = propertyMap.keySet();
		for (String key : keys) {
			String t = String.format("= %s (%s)", propertyMap.get(key), props.get(key));
			prettyPrinter.add(key, t);
		}
		List<String> prettyPrint = prettyPrinter.prettyPrint();
		for (String line : prettyPrint) {
			result.add(line);
		}
		return result;
    }

	/**
	 * takes a map of properties and uses the ones we're interested
	 * in and makes those available through getters.
	 */
	protected void handleProperties(Map<String, String> propertyMap, Map<String, Object> properties) {
		handleProperties(propertyMap, properties, getProps());
	}

	protected static void handleProperties(
			Map<String, String> propertyMap, 
			Map<String, Object> properties, 
			Map<String, String> props
	) {
		if (properties == null) {
			return;
		}
		Set<String> keys = properties.keySet();
		for (String key : keys) {
			if (propertyMap.containsKey(key)) {
				String val = "" + properties.get(key);
				props.put(key, val);
			}
		}
		props.put(INDEX, getIndex(propertyMap, props));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected static void handleProperties(
			Map<String, String> propertyMap, 
			Properties properties, 
			Map<String, String> props
	) {
        handleProperties(propertyMap, (Map)properties, props);
	}
}