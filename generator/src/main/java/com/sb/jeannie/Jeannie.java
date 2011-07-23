package com.sb.jeannie;

import groovy.lang.GroovyClassLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.misc.ErrorBuffer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sb.jeannie.beans.Context;
import com.sb.jeannie.beans.Info;
import com.sb.jeannie.beans.JeannieProperties;
import com.sb.jeannie.beans.Module;
import com.sb.jeannie.beans.TemplateProperties;
import com.sb.jeannie.interfaces.Postprocessor;
import com.sb.jeannie.interfaces.Preprocessor;
import com.sb.jeannie.interfaces.ProcessorBase;
import com.sb.jeannie.parsers.ParserSupport;
import com.sb.jeannie.processors.DefaultPostprocessor;
import com.sb.jeannie.renderers.StringRenderer;
import com.sb.jeannie.utils.TimeTaker;

/**
 * entry class for generation. Look no further.
 * 
 * - init() can be called to reset the generator.
 * - generate() can be called repeatedly
 * - looper() remains in a loop of calling generate()
 *   whenever any files have changed (both input or
 *   module files.)
 * 
 * @author alvi
 */
public class Jeannie {
	private static final Logger LOG = LoggerFactory.getLogger(Jeannie.class);
	
	public static final String GROOVY_SUFFIX = ".groovy";
	private static final String STG_SUFFIX = "stg";
	private static final String CONTEXT = "context";
	
	private List<File> allfiles;
	private Map<File, String> fileTypes;
	private Map<String, ProcessorBase> scriptlets;
	private Map<String, Preprocessor> preprocessors;
	private Map<String, Postprocessor> postprocessors;
	private InvertibleMap<File, Object> allInputObjects;
	private Module module;
	private File modulelocation;
	private File outputlocation;
	private File inputlocation;
	private ClassScanner scanner;
	
	public Jeannie(
			String modulelocation, 
			String inputlocation,
			String outputlocation
	) {
		init(new File(modulelocation), new File(inputlocation), new File(outputlocation));
	}
	
	public Jeannie(
			File modulelocation, 
			File inputlocation,
			File outputlocation
	) {
		init(modulelocation, inputlocation, outputlocation);
	}
	
	public void init() {
		init(modulelocation, inputlocation, outputlocation);
	}
	
	public void init(
			File modulelocation, 
			File inputlocation,
			File outputlocation
	) {
		TimeTaker tt = new TimeTaker();
		try {
			this.modulelocation = modulelocation;
			this.inputlocation = inputlocation;
			this.outputlocation = outputlocation;
			this.module = new Module(modulelocation);
			this.allfiles = Utils.allfiles(inputlocation);
			this.scanner = new ClassScanner();
			JeannieProperties.log();
			handleParsers();
		}
		finally {
			LOG.info("init(): {}", tt);
		}
	}

	/**
	 * never stops and calls generator whenever it detects a change.
	 */
	public void looper() {
		ChangeChecker modulefiles = new ChangeChecker(module.getModule());
		ChangeChecker inputfiles = new ChangeChecker(inputlocation);
		inputfiles.hasChangedFiles(); // don't parse first time
		int n = 0;
		do {
			try {
				if (n % 4 == 0) { // expensive. don't do this all the time...
					modulefiles = detectChanges(module.getModule(), modulefiles);
					inputfiles = detectChanges(inputlocation, inputfiles);
				}
				
				if (inputfiles.hasChangedFiles()) {
					List<ParserSupport> parsers = scanner.getParsers();
					for (ParserSupport parser : parsers) {
						parser.init();
					}
					this.allfiles = Utils.allfiles(inputlocation);
					handleParsers();
					generate();
				}
				if (modulefiles.hasChangedFiles()) {
					generate();
				}
				Thread.sleep(500);
			}
			catch (Exception e) {
				LOG.error("exception caught", e);
			}
			n++;
		} while(true);
	}

	private static ChangeChecker detectChanges(File path, ChangeChecker cc) {
		if (cc.numberOfFiles() != Utils.allfiles(path).size()) {
			cc = new ChangeChecker(path);
		}
		return cc;
	}
	
	private boolean isUpToDate() {
		String skip = JeannieProperties.getGlobalSkipUptodateCheck();
		if (!Boolean.parseBoolean(skip)) {
			boolean u = false;
			u = u || ChangeChecker.newerThan(inputlocation, outputlocation);
			u = u || ChangeChecker.newerThan(modulelocation, outputlocation);
			return !u;
		}
		return true;
	}
	
	// 1-n
	// n->n
	// n-1
	public void generate() {
		TimeTaker tt = new TimeTaker();
		try {
			if (isUpToDate()) {
				LOG.info("no files changed, generation skipped!");
				return;
			}
			handleProcessors();
			rebuildContext();
			LOG.info("\n{}", Context.index(preprocessors, postprocessors));

			List<STGroup> groups = new ArrayList<STGroup>();
			List<File> templfiles = Utils.allfiles(module.getTemplates(), STG_SUFFIX);
			for (File template : templfiles) {
				STGroupFile stgf = new STGroupFile(
						template.getAbsolutePath(), 
						JeannieProperties.getGlobalDelimiterStartChar().charAt(0), 
						JeannieProperties.getGlobalDelimiterEndChar().charAt(0)
				);
				stgf.setListener(new ErrorBuffer());
				groups.add(stgf);
			}

			for (File file : allfiles) {
				rebuildContext();
				String fileType = fileTypes.get(file);
				String extension = Utils.fileExtension(file);
				Context.put(Context.CURRENT, allInputObjects.get(file));
				Context.put(Context.CURRENT_FILE, file);
				for (STGroup stg : groups) {
					Context.put(Context.CURRENT_TEMPLATE, stg.getName());
					Context.put(Context.INDEX, Context.index(preprocessors, postprocessors));
					
					stg.defineDictionary(CONTEXT, Context.inst.getContext());
					stg.registerRenderer(String.class, new StringRenderer());
					
					ST st = stg.getInstanceOf(TemplateProperties.MAIN);
					if (st == null) {
						LOG.error("no 'main' template defined!");
						LOG.error("HINT: make sure that your template group contains");
						LOG.error("      a template named 'main'.");
						continue;
					}

					for (Preprocessor preprocessor : preprocessors.values()) {
						preprocessor.init(Context.inst);
					}
					
					Map<String, Object> properties = stg.rawGetDictionary(Context.PROPERTIES);
					TemplateProperties tp = new TemplateProperties(stg, properties);
					
					if (
						(tp.getType() == null || fileType.equals(tp.getType())) &&
						(tp.getExtension() == null || extension.equals(tp.getExtension()))
					) {
						String result = st.render();
						LOG.error(stg.getListener().toString());
						Context.put(Context.RESULT, result);
						handleWrite(tp, result);
					}
				}
			}
		}
		finally {
			LOG.info("generate(): {}", tt);
		}
	}

	private void handleWrite(TemplateProperties tp, String result) {
		try {
			Postprocessor postprocessor = fetchPostprocessor(tp.getPostprocessor());
			String outputdir = tp.getOutputdir();
			String outputname = tp.getOutputname();
			Boolean dontgenerate = tp.isDontgenerate();
			
			postprocessor.init(Context.inst);
			
			// the templateproperties always win!
			outputdir = Utils.nvl(outputdir, postprocessor.getOutputdir());
			outputname = Utils.nvl(outputname, postprocessor.getOutputname());
			dontgenerate = Utils.nvl(dontgenerate, postprocessor.getDongenerate());
			
			if (dontgenerate == false) {
				String outfile = outputdir + File.separator + outputname;
				File outputFile = new File(outputlocation, outfile);
				File outputdirFile = outputFile.getParentFile();
				if (!outputdirFile.exists()) {
					boolean mkdirs = outputdirFile.mkdirs();
					if (mkdirs == false) {
						LOG.error("couldn't create directory '{}'", outputdirFile);
					}
				}
				
				FileWriter fw = new FileWriter(outputFile);
				fw.append(result);
				fw.flush();
				fw.close();
			}
		}
		catch (IOException e) {
			LOG.error("couldn't write!", e);
		}
	}
	
	private void rebuildContext() {
		Map<String, String> env = System.getenv();
		Properties sysprops = System.getProperties();
		Context.init();
		Context.put(Context.ALL, allInputObjects.values());
		Context.put(Context.ALL_FILES, allfiles);
		Context.put(Context.MODULE, module);
		Context.put(Context.CURRENT, null);
		Context.put(Context.CURRENT_FILE, null);
		Context.put(Context.CURRENT_TEMPLATE, null);
		Context.put(Context.ENV, env);
		Context.put(Context.INFO, new Info(inputlocation, outputlocation));
		Context.put(Context.OBJECTMAP, allInputObjects);
		Context.put(Context.PARSERS, scanner.getParsers());
		//Context.put(Context.PROPERTIES, null);
		Context.put(Context.SCRIPTLETS, scriptlets);
		Context.put(Context.SYSTEM_PROPERTIES, sysprops);
	}

	private void handleProcessors() {
		resetProcessors();
		compileScriptlets();
		List<ProcessorBase> processors = scanner.getProcessors();
		for (ProcessorBase processor : processors) {
			identifyProcessor(processor.getName(), processor);
		}
	}

	private void handleParsers() {
		List<ParserSupport> parsers = scanner.getParsers();

		for (File file : allfiles) {
			for (ParserSupport parser : parsers) {
				parser.addFile(file);
			}
		}
		
		allInputObjects = new InvertibleMap<File, Object>();
		fileTypes = new HashMap<File, String>();
		int dl = 0;
		for (ParserSupport parser : parsers) {
			int t = parser.getType().length();
			dl = t > dl ? t : dl;
		}
		for (ParserSupport parser : parsers) {
			parser.parseFiles(allInputObjects, fileTypes, dl);
		}
	}

	private void resetProcessors() {
		scriptlets = new HashMap<String, ProcessorBase>();
		preprocessors = new HashMap<String, Preprocessor>();
		postprocessors = new HashMap<String, Postprocessor>();
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
				LOG.info("processors found: {}", classMap.keySet());
			}
			
			if (classMap == null || 
				ChangeChecker.newerThan(module.getScriptlets(), target)) {
				LOG.info("recompiling scriptlets...");
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
					LOG.info("   * {}", file.getName());
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
					LOG.info("    ---> {}", scriptletClassName);
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

	private Postprocessor fetchPostprocessor(String processor) {
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
			LOG.debug("script '{}' is a {}", scriptletName, Preprocessor.class.getSimpleName());
			preprocessors.put(scriptletName, p);
			scriptlets.put(scriptletName, p);
		}
		else if (processor instanceof Postprocessor) {
			Postprocessor p = (Postprocessor)processor;
			LOG.debug("script '{}' is a {}", scriptletName, Postprocessor.class.getSimpleName());
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
}
