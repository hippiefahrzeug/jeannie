package com.sb.jeannie.beans;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import com.sb.jeannie.KeyValuePrettyPrinter;

public abstract class BeanSupport {
	public BeanSupport() {
		super();
	}
	
	abstract protected Map<String, String> getPropertyMap();
	abstract protected Map<String, String> getProps();
	
    public static void log(Logger LOG, Map<String, String> propertyMap, Map<String, String> props) {
    	KeyValuePrettyPrinter prettyPrinter = new KeyValuePrettyPrinter();
    	Set<String> keys = propertyMap.keySet();
		for (String key : keys) {
			String t = String.format("= %s (%s)", propertyMap.get(key), props.get(key));
			prettyPrinter.add(key, t);
		}
		List<String> prettyPrint = prettyPrinter.prettyPrint();
		for (String line : prettyPrint) {
			LOG.debug(line);
		}
    }

	/**
	 * takes a map of properties and uses the ones we're interested
	 * in and makes those available through getters.
	 */
	public void handleProperties(Map<String, String> propertyMap, Map<String, Object> properties) {
		if (properties == null) {
			return;
		}
		Set<String> keys = properties.keySet();
		for (String key : keys) {
			if (propertyMap.containsKey(key)) {
				String val = (String)properties.get(key);
				getProps().put(key, val);
			}
		}
	}
}