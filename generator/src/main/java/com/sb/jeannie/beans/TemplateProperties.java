package com.sb.jeannie.beans;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * Handles all properties which concern handling of a template file,
 * e.g. which type of files are affected by a tempate, etc.
 * 
 * Note that a template property may appear as a dictionary within
 * or as a template. Here's an example:
 * 
 * ---------------------------------------------------
 * properties ::= [
 * 	"outputname" : "huep.txt"
 * ]
 * 
 * outputname() ::= "huhu.txt"
 * ---------------------------------------------------
 * 
 * If the template group contains both, then the dictionary wins.
 * 
 * use dictionary if: the value for a template property is a constant
 * use template if: the value for a template property is dynamic
 * 
 * @author alvi
 */
public class TemplateProperties extends BeanSupport {
	private static final String MAIN = "main";
	private static final String EXTENSION = "extension";
	private static final String TYPE = "type";
	private static final String OUTPUTNAME = "outputname";
	private static final String OUTPUTDIR = "outputdir";
	private static final String POSTPROCESSOR = "postprocessor";
	private static final String DONTGENERATE = "dontgenerate";

    protected static Map<String, String> PROPERTY_MAP;
	protected static Map<String, String> props;

	static {
		PROPERTY_MAP = new HashMap<String, String>();
		PROPERTY_MAP.put(MAIN, "the main template");
		PROPERTY_MAP.put(EXTENSION, "extension of a file");
		PROPERTY_MAP.put(TYPE, "type of the parser of the current object");
		PROPERTY_MAP.put(OUTPUTNAME, "name of the generated file");
		PROPERTY_MAP.put(OUTPUTDIR, "directory of the generated file");
		PROPERTY_MAP.put(POSTPROCESSOR, "desired post processor(s)");
		PROPERTY_MAP.put(DONTGENERATE, "set to true if file shouldn't be written");
	};
	
	public TemplateProperties(
			STGroup stg, 
			Map<String, Object> properties
	) {
		props = new HashMap<String, String>();
		handleTemplates(PROPERTY_MAP, stg);
		handleProperties(PROPERTY_MAP, properties);
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
			// note that 'main' must be rendered last, and whether
			// it is rendered at all depends on type, extension
			// and dontgenerate
			if (st != null && !key.equals(MAIN)) {
				String val = st.render();
				getProps().put(key, val);
			}
		}
	}
	
	protected Map<String, String> getPropertyMap() {
		return PROPERTY_MAP;
	}

	protected Map<String, String> getProps() {
		return props;
	}
	
	public void log() {
		log(PROPERTY_MAP, props);
	}

	public String getPostprocessor() {
		return props.get(POSTPROCESSOR);
	}

	public String getOutputdir() {
		return props.get(OUTPUTDIR);
	}

	public String getOutputname() {
		return props.get(OUTPUTNAME);
	}

	public String getExtension() {
		return props.get(EXTENSION);
	}
	
	public String getType() {
		return props.get(TYPE);
	}

	public boolean isDontgenerate() {
		return Boolean.parseBoolean(props.get(DONTGENERATE));
	}
}
