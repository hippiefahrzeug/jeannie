package com.sb.jeannie.beans;

import java.io.File;

public class Output {
	public static final String WORKINGDIR = ".jeannie-workingdir";
	private static final String SCRIPTS = "scripts";
	private static final String MODULE = "module";
	private static final String REFLECTIONS = "module-reflections.xml";

	private File outputlocation;
	private File workingdir;
	private File scriptdir;
	private File module;
	private File reflections;

	public Output(File outputlocation) {
		this.outputlocation = outputlocation;
		this.workingdir = new File(outputlocation, WORKINGDIR);
		this.scriptdir = new File(workingdir, SCRIPTS);
		this.module = new File(workingdir, MODULE);
		this.reflections = new File(module, REFLECTIONS);
	}

	public File getOutputlocation() {
		return outputlocation;
	}

	public File getWorkingdir() {
		return workingdir;
	}

	public File getScripts() {
		return scriptdir;
	}

	public File getModule() {
		return module;
	}

	public File getReflections() {
		return reflections;
	}
}
