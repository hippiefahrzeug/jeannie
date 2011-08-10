package com.sb.jeannie;

import java.io.File;
import java.util.List;

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
		this.outputlocation = outputlocation;
		this.inputlocation = inputlocation;
		this.propertyfiles = propertyfiles;
		
		if (moduleslocation.getName().endsWith(".jar")) {
			Utils.extract(moduleslocation, output.getModule());
			moduleslocation = output.getModule();
		}
	}
	
	public void generateAll() {
		Module m = new Module(moduleslocation);
		if (m.isModule()) {
			Jeannie jeannie = new Jeannie(moduleslocation, inputlocation, outputlocation, propertyfiles);
			jeannie.generate();
			return;
		}
		File[] files = moduleslocation.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				m = new Module(files[i]);
				if (m.isModule()) {
					Jeannie jeannie = new Jeannie(files[i], inputlocation, outputlocation, propertyfiles);
					jeannie.generate();
				}
			}
		}
	}
}
