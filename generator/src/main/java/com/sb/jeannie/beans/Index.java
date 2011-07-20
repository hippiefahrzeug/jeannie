package com.sb.jeannie.beans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sb.jeannie.Jeannie;
import com.sb.jeannie.interfaces.Postprocessor;
import com.sb.jeannie.interfaces.Preprocessor;
import com.sb.jeannie.interfaces.ProcessorBase;

public class Index {
	private static final long serialVersionUID = -5206561164460867913L;
	
	private static final String NL = System.getProperty("line.separator");
	
	public static final String INDEX = "index";
	public static final String ALL = "all";
	public static final String ALL_FILES = "allfiles";
	public static final String CURRENT = "current";
	public static final String CURRENT_FILE = "currentfile";
	public static final String PARSERS = "parsers";
	public static final String SCRIPTLETS = "scriptlets";
	public static final String PROPERTIES = "properties";
	public static final String SYSTEM_PROPERTIES = "systemproperties";
	public static final String INFO = "info";
	public static final String CURRENT_TEMPLATE = "currenttemplate";
	public static final String OBJECTMAP = "objectmap";
	public static final String ENV = "env";
	public static final String MODULE = "module";
	public static final String RESULT = "result";

    private static Map<String, String> index;
    static {
    	index = new HashMap<String, String>();
    	index.put(INDEX, "this index");
    	index.put(ALL, "all parsed and non-parsed objects");
    	index.put(ALL_FILES, "all input files");
    	index.put(CURRENT, "the current object");
    	index.put(CURRENT_FILE, "the current file");
    	index.put(PARSERS, "list of all parsers");
    	index.put(SCRIPTLETS, "list of all scriptlets");
    	index.put(PROPERTIES, "map of properties");
    	index.put(SYSTEM_PROPERTIES, "system properties");
    	index.put(INFO, "generator information");
    	index.put(OBJECTMAP, "invertible map (file <-> object)");
    	index.put(CURRENT_TEMPLATE, "the current template");
    	index.put(ENV, "environment properties");
    	index.put(MODULE, "the module");
    	index.put(RESULT, "the output after phase 1");
    }

    public static String index(
    		Map<String, Object> context, 
    		Map<String, Preprocessor> preprocessors,
    		Map<String, Postprocessor> postprocessors
    ) {
    	StringBuilder sb = new StringBuilder();
    	Set<String> keySet = index.keySet();
    	List<String> list = new ArrayList<String>(keySet);
    	Collections.sort(list);
    	
    	List<String> l = new ArrayList<String>();
    	l.addAll(context.keySet());
    	l.addAll(preprocessors.keySet());
    	l.addAll(postprocessors.keySet());
    	int ml = 0;
    	for (String key : l) {
    		ml = key.length() > ml ? key.length() : ml;
		}

    	for (String key : list) {
    		String t = String.format("%-" + ml + "s = %s", key, index.get(key));
    		String a = additionalInfo(context, key);
			sb.append(t + " " + a + NL);
		}
    	
		sb.append(NL);
		sb.append("Preprocessors:" + NL);
    	handleProcessors(sb, context, preprocessors, ml);
		sb.append(NL);
		sb.append("Postprocessors:" + NL);
    	handleProcessors(sb, context, postprocessors, ml);
    	return sb.toString();
	}

	private static <T> void handleProcessors(
			StringBuilder sb, 
			Map<String, Object> context,
			Map<String, T> processors, 
			int ml
	) {
		Set<String> scriptletSet = processors.keySet();
    	for (String key : scriptletSet) {
    		String name = key;
    		if (key.endsWith(Jeannie.GROOVY_SUFFIX)) {
        		name = key.replaceAll(".groovy$", "");
        		String t = String.format("%-" + ml + "s is a %s", name, processors.get(key));
    			sb.append(t + " (groovy scriptlet: '" + key + "')" + NL);
    		}
    		else {
    			ProcessorBase p = (ProcessorBase)processors.get(key);
        		String t = String.format("%-" + ml + "s %s", name, p.getDescription());
    			sb.append(t + NL);
    		}
    		context.put(name, processors.get(key));
		}
	}
    
    private static String additionalInfo(Map<String, Object> context, String key) {
    	if (INDEX.equals(key)) {
    		return "";
    	}
    	
		if (!context.containsKey(key)) {
			return "(not available)";
		}
		
		if (context.get(key) == null) {
			return "";
		}
		
		Object object = context.get(key);
		if (object instanceof Map) {
			Map<?, ?> m = (Map<?, ?>)object;
			return String.format("(%s entries)", m.size());
		}
		
		if (object instanceof List) {
			List<?> l = (List<?>)object;
			return String.format("(%s entries)", l.size());
		}
		
		if (object instanceof Collection) {
			Collection<?> c = (Collection<?>)object;
			return String.format("(%s entries)", c.size());
		}
		
		if (object instanceof String) {
			return String.format("('%s')", object);
		}
		
		return String.format("(type: %s)", object.getClass().getName());
    }
}
