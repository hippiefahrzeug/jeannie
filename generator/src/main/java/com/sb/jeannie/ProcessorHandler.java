package com.sb.jeannie;

import groovy.lang.GroovyClassLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sb.jeannie.beans.Module;
import com.sb.jeannie.interfaces.Postprocessor;
import com.sb.jeannie.interfaces.Preprocessor;
import com.sb.jeannie.interfaces.ProcessorBase;
import com.sb.jeannie.processors.DefaultPostprocessor;

public class ProcessorHandler {
	private static final Logger LOG = LoggerFactory.getLogger(ProcessorHandler.class);

	public static final String GROOVY_SUFFIX = ".groovy";

	private Module module;

	private Map<String, ProcessorBase> scriptlets;
	private Map<String, Preprocessor> preprocessors;
	private Map<String, Postprocessor> postprocessors;
	private ClassScanner scanner;
	
	public ProcessorHandler(Module module, ClassScanner scanner) {
		this.module = module;
		this.scanner = scanner;
		resetProcessors();
	}

	private void resetProcessors() {
		scriptlets = new HashMap<String, ProcessorBase>();
		preprocessors = new HashMap<String, Preprocessor>();
		postprocessors = new HashMap<String, Postprocessor>();
	}
	
	public void handleProcessors() {
		resetProcessors();
		compileScriptlets();
		List<ProcessorBase> processors = scanner.getProcessors();
		for (ProcessorBase processor : processors) {
			identifyProcessor(processor.getName(), processor);
		}
	}

	/**
	 * This compiles scriptlets into java class files and extends
	 * the classpath to include those classes. This method can be
	 * called repeatedly, as it recreates a classloader each time,
	 * ensuring that new/changed classes are provided to the
	 * generator.
	 * 
	 * This method has the usual groovy overhead when a module is
	 * run for the first time. after that, if there are no scriptlet
	 * changes, it should be fairly fast,.
	 */
	private void compileScriptlets() {
		try {
			File target = new File("/tmp/ttt");

			Map<String, String> classMap = null;
			File classmapFile = new File(target, "classmap");
			if (classmapFile.exists()) {
				Gson gson = new Gson();
				FileReader fr = new FileReader(classmapFile);
		        classMap = gson.fromJson(fr, new TypeToken<Map<String, String>>() {}.getType());
				LOG.debug("processors found: {}", classMap.keySet());
			}
			
			if (classMap == null || 
				ChangeChecker.newerThan(module.getScriptlets(), target)) {
				LOG.debug("recompiling scriptlets...");
				Utils.deleteAll(target);
				CompilerConfiguration cc = new CompilerConfiguration();
				cc.setDebug(true);
				cc.setTargetDirectory(target);
				cc.setVerbose(true);
				
				ClassLoader parent = Jeannie.class.getClassLoader();
				GroovyClassLoader gcl = new GroovyClassLoader(parent);
								
				List<File> scriptlets = Utils.allfiles(module.getScriptlets(), GROOVY_SUFFIX);
				classMap = new HashMap<String, String>();
				for (File file : scriptlets) {
					LOG.debug("   * {}", file.getName());
					CompilationUnit cu = new CompilationUnit();
					cu.addSource(new File(module.getScriptlets(), file.getName()));
					cu.setConfiguration(cc);
					cu.configure(cc);
					cu.setClassLoader(gcl);
					cu.compile();
					if (cu.getClasses().size() != 1) {
						LOG.error("a scriptlet must only have one class!");
						LOG.error("will use first class only.");
					}
					String scriptletClassName = cu.getFirstClassNode().getName();
					LOG.debug("    ---> {}", scriptletClassName);
					classMap.put(file.getName(), scriptletClassName);
				}
				
				// we want to reference scriptlets by their script name
				// which ideally should be the same as the class name
				// but in case it isn't -> let's store a map which
				// contains the association.
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				String json = gson.toJson(classMap);
				FileWriter fw = new FileWriter(classmapFile);
				fw.append(json);
				fw.flush();
				fw.close();
			}
			
			URLClassLoader parent = (URLClassLoader)Jeannie.class.getClassLoader();
			URLClassLoader rl = new URLClassLoader(new URL[] {}, parent);

			ClassPathExtender.addURL(
					rl,
					target.toURI().toURL());
			Collection<String> keys = classMap.keySet();
			for (String scriptletName : keys) {
				String scriptletClassName = classMap.get(scriptletName);
				Class<?> scriptletClass = rl.loadClass(scriptletClassName);
				
				Object processor = scriptletClass.newInstance();
				identifyProcessor(scriptletName, processor);
			}
		}
		catch (Exception e) {
			LOG.error("exception caught: ", e);
		}
	}

	Postprocessor fetchPostprocessor(String processor) {
		for (Postprocessor postprocessor : postprocessors.values()) {
			if (postprocessor.getName().equals(processor)) {
				return postprocessor;
			}
		}
		return new DefaultPostprocessor();
	}
	
	/**
	 * takes a processor and puts it into the correspondng list.
	 * 
	 * @param scriptletName
	 * @param processor
	 */
	private void identifyProcessor(String scriptletName, Object processor) {
		if (processor instanceof Preprocessor) {
			Preprocessor p = (Preprocessor)processor;
			LOG.debug("'{}' is a {}", scriptletName, Preprocessor.class.getSimpleName());
			preprocessors.put(scriptletName, p);
			scriptlets.put(scriptletName, p);
		}
		else if (processor instanceof Postprocessor) {
			Postprocessor p = (Postprocessor)processor;
			LOG.debug("'{}' is a {}", scriptletName, Postprocessor.class.getSimpleName());
			postprocessors.put(scriptletName, p);
			scriptlets.put(scriptletName, p);
		}
		else {
			LOG.error("{} doesn't implement proper interface", scriptletName);
			LOG.error(" --> ignored!");
			LOG.error("HINT: make sure that your scriptlets implement");
			LOG.error("      one of the following interfaces:");
			LOG.error("         - " + Preprocessor.class.getName());
			LOG.error("         - " + Postprocessor.class.getName());
		}
	}

	public Map<String, ProcessorBase> getScriptlets() {
		return scriptlets;
	}

	public Map<String, Preprocessor> getPreprocessors() {
		return preprocessors;
	}

	public Map<String, Postprocessor> getPostprocessors() {
		return postprocessors;
	}
}
