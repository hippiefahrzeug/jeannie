package com.sb.jeannie;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sb.jeannie.beans.Module;
import com.sb.jeannie.beans.Output;
import com.sb.jeannie.utils.Utils;

/**
 * moduleslocation may point to a module directly or a folder that
 * contains a collection of modules, or a jar that contains one
 * or more modules.
 * 
 * This handler will call the generator for each module.
 * 
 * TODO: don't parse the input files for each module!! 
 * 
 * @author alvi
 *
 */
public class ModulesHandler {
	private static final Logger LOG = LoggerFactory.getLogger(ModulesHandler.class);

	private File moduleslocation;
	private File outputlocation;
	private File inputlocation;
	private List<File> propertyfiles;

	public ModulesHandler(
			File moduleslocation,
			File inputlocation,
			File outputlocation,
			List<File> propertyfiles,
			Output output) {
		this.moduleslocation = moduleslocation;
		this.inputlocation = inputlocation;
		this.outputlocation = outputlocation;
		this.propertyfiles = propertyfiles;
		
		if (moduleslocation.getName().endsWith(".jar")) {
			Utils.extract(moduleslocation, output.getModule());
			moduleslocation = output.getModule();
		}
	}
	
	public void generateAll() {
		Module m = new Module(moduleslocation);
		if (m.isModule()) {
			LOG.info("{} points to a plain module", moduleslocation);
			Generator jeannie = new Generator(moduleslocation, inputlocation, outputlocation, propertyfiles);
			jeannie.generate();
			return;
		}
		File[] files = moduleslocation.listFiles();
		if (files == null) {
			return;
		}
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				m = new Module(files[i]);
				if (m.isModule()) {
					LOG.info("processing module {}", files[i].getName());
					Generator jeannie = new Generator(files[i], inputlocation, outputlocation, propertyfiles);
					jeannie.generate();
				}
			}
		}
	}

	public File getModuleslocation() {
		return moduleslocation;
	}

	public File getOutputlocation() {
		return outputlocation;
	}

	public File getInputlocation() {
		return inputlocation;
	}

	public List<File> getPropertyfiles() {
		return propertyfiles;
	}
}
