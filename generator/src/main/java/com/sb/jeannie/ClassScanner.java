package com.sb.jeannie;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.sb.jeannie.annotations.Parser;
import com.sb.jeannie.beans.Module;
import com.sb.jeannie.beans.Output;
import com.sb.jeannie.interfaces.Postprocessor;
import com.sb.jeannie.interfaces.Preprocessor;
import com.sb.jeannie.interfaces.ProcessorBase;
import com.sb.jeannie.parsers.ParserSupport;
import com.sb.jeannie.processors.DefaultProcessor;

/**
 * 
 * @author alvi
 */
public class ClassScanner {
	private static final Logger LOG = LoggerFactory.getLogger(ClassScanner.class);
	
	private static final String PARSER_ANNOTATION = Parser.class.getName();
	private static final String PARSERS_PKG = ParserSupport.class.getPackage().getName() + ".*";
	private static final String PROCESSORS_PKG = DefaultProcessor.class.getPackage().getName() + ".*";
	
    private static final String EXTERNAL_PACKAGE = "external_package";
    
    private Module module;
    private Output output;
	private List<ParserSupport> parsers = new ArrayList<ParserSupport>();
	private List<ProcessorBase> processors = new ArrayList<ProcessorBase>();

	public ClassScanner(Module module, Output output) {
		this.module = module;
		this.output = output;
		init();
	}
	
    public void init() {
    	Reflections reflections = fetchReflections();
		
		Set<String> parserset = reflections.getStore().getTypesAnnotatedWith(PARSER_ANNOTATION);
		for (String parserName : parserset) {
			Class<?> parserClass = loadClass(parserName);
			Parser annotation = parserClass.getAnnotation(Parser.class);
			String description = annotation.type();
			String [] extensions = annotation.extensions();
			ParserSupport parser = (ParserSupport)instantiate(parserName, parserClass);
			if (extensions.length > 0) {
				LOG.debug("registering: {} (extensions: {}, prio: " + parser.getPrio() + ")", description, extensions);
			}
			else {
				LOG.debug("registering: {} (prio: " + parser.getPrio() + ")", description);
			}
			parsers.add(parser);
		}
		
		ParserComparator pc = new ClassScanner.ParserComparator();
		Collections.sort(parsers, pc);
		
		Set<String> preprocessorset = reflections.getStore().getSubTypesOf(Preprocessor.class.getName());
		Set<String> postprocessorset = reflections.getStore().getSubTypesOf(Postprocessor.class.getName());
		List<String> processorlist = new ArrayList<String>();
		processorlist.addAll(preprocessorset);
		processorlist.addAll(postprocessorset);

		for (String processorName : processorlist) {
			Class<?> processorClass = loadClass(processorName);
			ProcessorBase processor = (ProcessorBase)instantiate(processorName, processorClass);
			LOG.debug("registering: {} (Processor)", processor.getName());
			processors.add(processor);
		}
    }
    
    private Reflections fetchReflections() {
    	if (module.getReflections().exists()) {
    		return Reflections.collect().collect(module.getReflections());
    	}
    	
		Predicate<String> filters = null;
		if (System.getProperty(EXTERNAL_PACKAGE) != null) {
			String userFilter = System.getProperty(EXTERNAL_PACKAGE);
			filters = new FilterBuilder().include(userFilter).include(PARSERS_PKG).include(PROCESSORS_PKG);
		}
		else {
			filters = new FilterBuilder().include(PARSERS_PKG).include(PROCESSORS_PKG);
		}
		
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.filterInputsBy(filters);

		List<URL> urls = new ArrayList<URL>();
		ClassLoader [] classloaders = {
				ClassScanner.class.getClassLoader(),
				Thread.currentThread().getContextClassLoader()
		};
		
		for (int i = 0; i < classloaders.length; i++) {
			if (classloaders[i] instanceof URLClassLoader) {
				urls.addAll(Arrays.asList(((URLClassLoader)classloaders[i]).getURLs()));
			}
			else {
				throw new RuntimeException("classLoader is not an instanceof URLClassLoader");
			}
		}
		
		/* we should only scan those urls that may contain interesting classes
		Set<URL> urls2 = new HashSet<URL>();
		for (URL url : urls) {
			if (url.toString().contains("jeannie")) {
				LOG.info("url: {}", url);
				urls2.add(url);
			}
		}
		*/
		
		configurationBuilder.setUrls(urls);
		Reflections reflections = new Reflections(configurationBuilder);
		reflections.save(output.getReflections().getAbsolutePath());
		return reflections;
    }

	private Object instantiate(String clazzName, Class<?> clazz) {
		Object o = null;
		try {
			o = clazz.newInstance();
		}
		catch (Exception e) {
			LOG.error("exception caught:", e);
			LOG.error("{} is not working!", clazzName);
		}
		return o;
	}
    
	private Class<?> loadClass(String parserName) {
		Class<?> clazz = null;
		try {
			clazz = Class.forName(parserName, true, Thread.currentThread().getContextClassLoader() );
		}
		catch (ClassNotFoundException e) {
			try {
				clazz = Class.forName(parserName);
			}
			catch (ClassNotFoundException f) {
				LOG.error("exception caught:", e);
				LOG.error("{} is not working properly - ignored!", parserName);
				return null;
			}
		}
		return clazz;
	}
	
	private static class ParserComparator implements Comparator<ParserSupport> {
		public int compare(ParserSupport a, ParserSupport b) {
			return a.getPrio() - b.getPrio();
		}
	}

	public List<ParserSupport> getParsers() {
		return parsers;
	}
	
	public List<ProcessorBase> getProcessors() {
		return processors;
	}
}
