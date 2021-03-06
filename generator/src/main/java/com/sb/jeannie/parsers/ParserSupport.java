package com.sb.jeannie.parsers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sb.jeannie.annotations.Parser;
import com.sb.jeannie.beans.JeannieProperties;
import com.sb.jeannie.utils.KeyValuePrettyPrinter;
import com.sb.jeannie.utils.Stopwatch;
import com.sb.jeannie.utils.Utils;

public abstract class ParserSupport {
    private final static Logger LOG = LoggerFactory.getLogger(ParserSupport.class);
    private String type;
    private Set<String> extensions;
    private int prio;
    private List<File> files;
    private int totalFiles = 0;
    
	/**
	 * @param inputObjects will contain the parsed object, identified by the file
	 * @param file the file to parse
	 */
	public abstract Object parse(File file);
	
    public ParserSupport() {
    	init();
	}
    
    public void init() {
    	Parser ann = getClass().getAnnotation(Parser.class);
    	type = ann.type();
    	List<String> exList = Arrays.asList(ann.extensions());
    	extensions = new HashSet<String>(exList);
    	prio = ann.prio();
    	files = new ArrayList<File>();
    }
    
    public boolean isA(File file) {
    	return files.contains(file);
    }
    
    public void addFile(KeyValuePrettyPrinter pp, File file) {
    	totalFiles++;
    	String types = JeannieProperties.getGlobalTypes();
    	if (types != null) {
        	if (!(types.contains(type) || types.equals("all"))) {
        		return;
        	}
    	}
    	
    	// default behavior: always add the file!
    	if (extensions.size() == 0) {
        	files.add(file);
        	return;
    	}
    	
    	if (extensions.contains(Utils.fileExtension(file))) {
    		// LOG.debug("'{}' adding '{}'", type, file.getParentFile().getName() + "/" + file.getName());
    		String f = String.format("adding '%s'", file.getParentFile().getName() + "/" + file.getName());
    		pp.add("'" + type + "'", f);
        	files.add(file);
    	}
    }
    
    public void parseFiles(
    		Map<File, Object> inputObjects, 
    		Map<File, String> fileTypes, 
    		int dl
    ) {
		Stopwatch tt = new Stopwatch();
    	for (File file : files) {
    		inputObjects.put(file, parse(file));
    		fileTypes.put(file, type);
    	}
    	int ml = ("" + totalFiles).length();
    	String msg = String.format("%-" + dl + "s: %" + ml + "s files parsed, %s", type, files.size(), tt);
		LOG.info(msg);
    }
    
    public int getPrio() {
    	return prio;
    }
    
    public String getType() {
    	return type;
    }
}
