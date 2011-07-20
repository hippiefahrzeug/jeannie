package com.sb.jeannie.beans;

import java.io.File;
import java.util.Date;

public class Info {
	public static final String VERSION = "time";
	public static final String TIME = "time";
	public static final String WARN = "warn";
	public static final String INPUTLOCATION = "inputlocation";
	public static final String OUTPUTLOCATION = "outputlocation";
	
	private static final String WARN_MSG = "this file has been generated. do not edit.";

	private String version;
	private Date time;
	private File inputlocation;
	private File outputlocation;

	public Info(File inputlocation, File outputlocation) {
		time = new Date();
		this.inputlocation = inputlocation;
		this.outputlocation = outputlocation;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public File getOutputlocation() {
		return outputlocation;
	}

	public void setOutputlocation(File outputlocation) {
		this.outputlocation = outputlocation;
	}

	public File getInputlocation() {
		return inputlocation;
	}

	public void setInputlocation(File inputlocation) {
		this.inputlocation = inputlocation;
	}
	
	public String getWarn() {
		return WARN_MSG;
	}

	public String toString() {
		return "Info [version=" + version + ", time=" + time
				+ ", inputlocation=" + inputlocation + ", outputlocation="
				+ outputlocation + "]";
	}
}
