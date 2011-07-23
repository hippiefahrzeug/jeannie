package com.sb.jeannie.beans;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

public abstract class BeanSupport {
	private static final Logger LOG = LoggerFactory.getLogger(BeanSupport.class);
	
	public BeanSupport() {
		super();
	}
	
	abstract protected Map<String, String> getPropertyMap();
	abstract protected Map<String, String> getProps();
	
    public static void log(Map<String, String> propertyMap, Map<String, String> props) {
		Set<String> keys = propertyMap.keySet();
		for (String key : keys) {
			String t = String.format("%s %s (%s)", key, propertyMap.get(key), props.get(key));
			LOG.info("{}", t);
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

	/**
	 * queries the template group for a set of templates and 
	 * renders those available. These templates's values will then
	 * replace the property value. The key is the template name.
	 */
	public void handleTemplates(
			Map<String, String> propertyMap, 
			STGroup stg
	) {
		Set<String> keys = propertyMap.keySet();
		for (String key : keys) {
			ST st = stg.getInstanceOf(key);
			if (st != null) {
				String val = st.render();
				getProps().put(key, val);
			}
		}
	}
}