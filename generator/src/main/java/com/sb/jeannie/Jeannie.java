package com.sb.jeannie;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import com.sb.jeannie.beans.Context;
import com.sb.jeannie.beans.Info;
import com.sb.jeannie.beans.JeannieProperties;
import com.sb.jeannie.beans.Module;
import com.sb.jeannie.beans.Output;
import com.sb.jeannie.beans.TemplateProperties;
import com.sb.jeannie.interfaces.Postprocessor;
import com.sb.jeannie.interfaces.Preprocessor;
import com.sb.jeannie.parsers.ParserSupport;
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
	
	private static final String STG_SUFFIX = "stg";
	private static final String CONTEXT = "context";
	
	private File modulelocation;
	private File outputlocation;
	private File inputlocation;
	private File [] propertyfiles;
	
	private List<File> allfiles;
	private Map<File, String> fileTypes;
	private InvertibleMap<File, Object> allInputObjects;
	private Module module;
	private Output output;
	private Properties properties;
	private ClassScanner scanner;
	private ProcessorHandler processorHandler;
	
	public Jeannie(
			String modulelocation, 
			String inputlocation,
			String outputlocation,
			String... propertyfiles
	) {
		File [] pf = new File[propertyfiles.length];
		for (int i = 0; i < propertyfiles.length; i++) {
			pf[i] = new File(propertyfiles[i]);
		}
		init(new File(modulelocation), new File(inputlocation), new File(outputlocation), pf);
	}
	
	// this one is for jpage
	public Jeannie(
			String modulelocation, 
			String inputlocation,
			String outputlocation,
			List<String> propertyfiles
	) {
		File [] pfiles = new File[propertyfiles.size()];
		int i = 0;
		for (String pf : propertyfiles) {
			pfiles[i] = new File(pf);
			i++;
		}
		init(new File(modulelocation), new File(inputlocation), new File(outputlocation), pfiles);
	}
	
	public Jeannie(
			File modulelocation, 
			File inputlocation,
			File outputlocation,
			File... propertyfiles
	) {
		init(modulelocation, inputlocation, outputlocation, propertyfiles);
	}
	
	public void init() {
		init(modulelocation, inputlocation, outputlocation, propertyfiles);
	}
	
	public void init(
			File modulelocation, 
			File inputlocation,
			File outputlocation,
			File... propertyfiles
	) {
		TimeTaker tt = new TimeTaker();
		try {
			this.modulelocation = modulelocation;
			this.inputlocation = inputlocation;
			this.outputlocation = outputlocation;
			this.propertyfiles = propertyfiles;
			
			this.output = new Output(outputlocation);
			if (modulelocation.getName().endsWith(".jar")) {
				Utils.extract(modulelocation, output.getModule());
				this.modulelocation = output.getModule();
			}
			
			this.module = new Module(this.modulelocation);
			this.output = new Output(outputlocation);
			this.allfiles = Utils.allfiles(inputlocation);
			this.scanner = new ClassScanner(module, output);
			JeannieProperties.log();
		}
		finally {
			LOG.debug("init(): {}", tt);
		}
	}
	
	/**
	 * never stops and calls generator whenever it detects a change.
	 */
	public void looper() {
		ChangeChecker inputfiles = new ChangeChecker(inputlocation);
		ChangeChecker modulefiles = new ChangeChecker(modulelocation);
		for (int i = 0; i < propertyfiles.length; i++) {
			modulefiles.add(propertyfiles[i]);
		}
		
		inputfiles.hasChangedFiles(); // don't parse first time
		int n = 0;
		int numInputfiles = Utils.allfiles(inputlocation).size();
		int numModulefiles = Utils.allfiles(modulelocation).size();
		do {
			try {
				if (n % 4 == 0) { // expensive. don't do this all the time...
					int num = Utils.allfiles(inputlocation).size();
					if (numInputfiles != num) {
						numInputfiles = num;
						inputfiles = new ChangeChecker(inputlocation);
					}
					num = Utils.allfiles(modulelocation).size();
					if (numModulefiles != num) {
						numModulefiles = num;
						modulefiles = new ChangeChecker(modulelocation);
					}
				}
				if (inputfiles.hasChangedFiles() ||
					modulefiles.hasChangedFiles()
				) {
					List<ParserSupport> parsers = scanner.getParsers();
					for (ParserSupport parser : parsers) {
						parser.init();
					}
					this.allfiles = Utils.allfiles(inputlocation);
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

	private Properties readProperties(File [] propertyfiles) {
		Properties p = new Properties();
		for (int i = 0; i < propertyfiles.length; i++) {
			try {
				p.load(new FileInputStream(propertyfiles[i]));
			}
			catch (Exception e) {
				LOG.error("couldn't read '{}'", propertyfiles[i]);
			}
		}
		
		return p;
	}

	private boolean isUpToDate() {
		String skip = JeannieProperties.getGlobalSkipUptodateCheck();
		if (!Boolean.parseBoolean(skip)) {
			boolean u = false;
			for (int i = 0; i < propertyfiles.length; i++) {
				u = u || ChangeChecker.newerThan(propertyfiles[i], outputlocation);
			}
			u = u || ChangeChecker.newerThan(inputlocation, outputlocation);
			u = u || ChangeChecker.newerThan(modulelocation, outputlocation);
			return !u;
		}
		return true;
	}
	
	public void generate() {
		TimeTaker tt = new TimeTaker();
		int generated = 0;
		try {
			if (isUpToDate()) {
				LOG.info("no files changed, generation skipped!");
				return;
			}
			properties = readProperties(propertyfiles);
			parseAll();

			processorHandler = new ProcessorHandler(module, output, scanner);
			processorHandler.handleProcessors();
			
			Map<String, Preprocessor> preprocessors = processorHandler.getPreprocessors();
			Map<String, Postprocessor> postprocessors = processorHandler.getPostprocessors();

			rebuildContext();
			Context.log(preprocessors, postprocessors);

			List<STGroup> groups = new ArrayList<STGroup>();
			List<File> templfiles = Utils.allfiles(module.getTemplates(), STG_SUFFIX);
			for (File template : templfiles) {
				STGroupFile stg = new STGroupFile(
						template.getAbsolutePath(), 
						JeannieProperties.getGlobalDelimiterStartChar().charAt(0), 
						JeannieProperties.getGlobalDelimiterEndChar().charAt(0)
				);
				
				stg.registerRenderer(String.class, new StringRenderer());
				stg.setListener(new STErrors());
				stg.defineDictionary(CONTEXT, Context.inst.getContext());

				groups.add(stg);
			}

			for (STGroup stg : groups) {
				ST st = stg.getInstanceOf(TemplateProperties.MAIN);
				if (st == null) {
					LOG.error("no 'main' template defined!");
					LOG.error("HINT: make sure that your template group contains");
					LOG.error("      a template named 'main'.");
					continue;
				}

				Context.put(Context.CURRENT_TEMPLATE, stg.getName());
				
				Map<String, Object> properties = stg.rawGetDictionary(Context.PROPERTIES);
				// no template rendering (first argument) necessary here,
				// we only need singleoutput
				TemplateProperties tp = new TemplateProperties(null, properties);
				boolean single = Boolean.parseBoolean(tp.getSingleoutput());
				
				for (File file : allfiles) {
					String fileType = fileTypes.get(file);
					if (fileType == null) { // not parsed!
						continue;
					}

					String extension = Utils.fileExtension(file);
					
					Object current = allInputObjects.get(file);
					Context.put(Context.CURRENT, current);
					Context.put(Context.CURRENT_FILE, file);
					Context.put(Context.INDEX, Context.index(preprocessors, postprocessors));

					HashSet<Object> generatefor = new HashSet<Object>();
					for (Preprocessor preprocessor : preprocessors.values()) {
						preprocessor.init(Context.inst);
						List<Object> gf = preprocessor.generatefor();
						if (gf != null) {
							generatefor.addAll(gf);
						}
					}
					
					// this should happen within the generatefor loop
					// so that ITERATOR and COUNTER could be used as
					// well. but this would be quite expensive.
					tp.handleTemplates(stg);
					boolean isType = (tp.getType() == null || fileType.equals(tp.getType()));
					boolean isExtension = (tp.getExtension() == null || extension.equals(tp.getExtension()));
					
					if (!isType || !isExtension) {
						continue;
					}
					
					// if there are no generatefor objects,
					// we fall back to the default (1-1 generation)
					if (generatefor.size() == 0) {
						generatefor.add(current);
					}

					int n = 0;
					for (Object iterator : generatefor) {
						Context.put(Context.ITERATOR, iterator);
						Context.put(Context.COUNTER, Integer.valueOf(n));
						String result = st.render();
						Context.put(Context.RESULT, result);
						handleWrite(tp, result);
						n++;
						if (single) {
							break;
						}
					}
					generated += n;
					if (single) {
						break;
					}
				}
				// rebuild the context for next file
				// uncommented: probably not necessary
				// (it may be as there may be some polution...)
				// rebuildContext();
			}
		}
		finally {
			LOG.info("generated {} files, {}", generated, tt);
		}
	}

	private void handleWrite(TemplateProperties tp, String result) {
		try {
			Postprocessor postprocessor = processorHandler.fetchPostprocessor(tp.getPostprocessor());
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
		Context.put(Context.PROPERTIES, properties);
		Context.put(Context.SCRIPTLETS, processorHandler.getScriptlets());
		Context.put(Context.SYSTEM_PROPERTIES, sysprops);
	}

	private void parseAll() {
		List<ParserSupport> parsers = scanner.getParsers();

		KeyValuePrettyPrinter pp = new KeyValuePrettyPrinter();
		for (File file : allfiles) {
			for (ParserSupport parser : parsers) {
				parser.addFile(pp, file);
			}
		}
		
		for (String line : pp.prettyPrint()) {
			LOG.debug(line);
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
}
