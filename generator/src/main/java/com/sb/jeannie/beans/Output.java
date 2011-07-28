package com.sb.jeannie.beans;

import java.io.File;

public class Output {
	private static final String WORKINGDIR = "workingdir";
	private static final String SCRIPTS = "scripts";
	private static final String MODULE = "module";

	private File outputlocation;
	private File workingdir;
	private File scriptdir;
	private File module;

	public Output(File outputlocation) {
		this.outputlocation = outputlocation;
		this.workingdir = new File(outputlocation, WORKINGDIR);
		this.scriptdir = new File(workingdir, SCRIPTS);
		this.module = new File(workingdir, MODULE);
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
}
