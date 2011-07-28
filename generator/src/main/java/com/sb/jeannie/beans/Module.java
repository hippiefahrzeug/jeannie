package com.sb.jeannie.beans;

import java.io.File;

/**
 * encapsulates the structure of a cartridge.
 * 
 * @author alvi
 */
public class Module {
	private static final String TEMPLATES = "templates";
	private static final String SCRIPTLETS = "scriptlets";
	private static final String README = "README";
	private static final String BANNER = "BANNER";
	private static final String REFLECTIONS = "module-reflections.xml";
	
	private File module;
	private File templates;
	private File scriptlets;
	private File readme;
	private File banner;
	private File reflections;

	/**
	 * @param cartridgePath File to the path of the cartridge
	 */
	public Module(File module) {
		this.module = module;
		templates = new File(module, TEMPLATES);
		scriptlets = new File(module, SCRIPTLETS);
		readme = new File(module, README);
		banner = new File(module, BANNER);
		reflections = new File(module, REFLECTIONS);
	}

	public File getModule() {
		return module;
	}

	public File getTemplates() {
		return templates;
	}

	public File getScriptlets() {
		return scriptlets;
	}

	public File getReadme() {
		return readme;
	}

	public File getBanner() {
		return banner;
	}

	public File getReflections() {
		return reflections;
	}
}
