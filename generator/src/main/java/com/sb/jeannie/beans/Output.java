package com.sb.jeannie.beans;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.sb.jeannie.utils.Utils;

public class Output {
	public static final String WORKINGDIR = ".jeannie-workingdir";
	private static final String SCRIPTS = "scripts";
	private static final String MODULE = "module";
	private static final String REFLECTIONS = "module-reflections.xml";
	private static final String STATUS = "status.json";

	private File outputlocation;
	private File workingdir;
	private File scriptdir;
	private File module;
	private File reflections;
	private File status;
	
	private Map<String, String> digests;

	public Output(File outputlocation) {
		this.outputlocation = outputlocation;
		this.workingdir = new File(outputlocation, WORKINGDIR);
		this.scriptdir = new File(workingdir, SCRIPTS);
		this.module = new File(workingdir, MODULE);
		this.reflections = new File(module, REFLECTIONS);
		this.status = new File(workingdir, STATUS);
		digests = new HashMap<String, String>();
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

	public File getStatus() {
		return status;
	}
	
	public void addGeneratedFile(File file, String result) {
		digests.put(file.getAbsolutePath(), Utils.sha1(result));
	}
	
	public boolean differs(File file, String result) {
		String key = file.getAbsolutePath();
		if (!digests.containsKey(key)) {
			return true;
		}
		String digest = Utils.sha1(result);
		return !digest.equals(digests.get(key));
	}
}
