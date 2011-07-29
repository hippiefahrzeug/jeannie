package com.sb.jeannie.beans;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private static final Logger LOG = LoggerFactory.getLogger(TemplateProperties.class);
	
	public static final String MAIN = "main";
	private static final String TYPE = "type";
	private static final String EXTENSION = "extension";
	private static final String SINGLEOUTPUT = "singleoutput";
	private static final String OUTPUTNAME = "outputname";
	private static final String OUTPUTDIR = "outputdir";
	// private static final String PREPROCESSOR = "preprocessor";
	private static final String POSTPROCESSOR = "postprocessor";
	private static final String DONTGENERATE = "dontgenerate";

    protected static Map<String, String> PROPERTY_MAP;
	protected static Map<String, String> props;

	static {
		PROPERTY_MAP = new HashMap<String, String>();
		PROPERTY_MAP.put(MAIN, "the main template");
		PROPERTY_MAP.put(TYPE, "type of the parser of the current object");
		PROPERTY_MAP.put(EXTENSION, "extension of a file");
		PROPERTY_MAP.put(SINGLEOUTPUT, "if true, only one file is generated");
		PROPERTY_MAP.put(OUTPUTNAME, "name of the generated file");
		PROPERTY_MAP.put(OUTPUTDIR, "directory of the generated file");
		// PROPERTY_MAP.put(PREPROCESSOR, "desired pre processor");
		PROPERTY_MAP.put(POSTPROCESSOR, "desired post processor");
		PROPERTY_MAP.put(DONTGENERATE, "set to true if file shouldn't be written");
	};
	
	public TemplateProperties(
			STGroup stg, 
			Map<String, Object> properties
	) {
		props = new HashMap<String, String>();
		handleTemplates(stg);
		handleProperties(properties);
	}
	
	public void handleTemplates(STGroup stg) {
		handleTemplates(PROPERTY_MAP, stg);
	}
	
	public void handleProperties(Map<String, Object> properties) {
		handleProperties(PROPERTY_MAP, properties);
	}

	/**
	 * queries the template group for a set of templates and 
	 * renders those available. These templates's values will then
	 * replace the property value. The key is the template name.
	 */
	private void handleTemplates(
			Map<String, String> propertyMap, 
			STGroup stg
	) {
		if (stg == null) {
			return;
		}
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

	public Map<String, String> getProps() {
		return props;
	}
	
	public void log() {
		log(LOG, PROPERTY_MAP, props);
	}
	
    public static String getIndex() {
        return props.get(INDEX);
    }
    
/*
	public String getPreprocessor() {
		return props.get(PREPROCESSOR);
	}
*/
	public String getPostprocessor() {
		return props.get(POSTPROCESSOR);
	}

	public String getOutputdir() {
		return props.get(OUTPUTDIR);
	}

	public String getOutputname() {
		return props.get(OUTPUTNAME);
	}

	public String getType() {
		return props.get(TYPE);
	}

	public String getExtension() {
		return props.get(EXTENSION);
	}
	
	public String getSingleoutput() {
		return props.get(SINGLEOUTPUT);
	}

	public boolean isDontgenerate() {
		return Boolean.parseBoolean(props.get(DONTGENERATE));
	}
}
