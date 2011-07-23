package com.sb.jeannie.processors;

import java.io.File;

import com.sb.jeannie.annotations.Processor;
import com.sb.jeannie.interfaces.Postprocessor;

@Processor
public class DefaultPostprocessor extends DefaultProcessor implements Postprocessor {
	public String getDescription() {
		return "sets output location: <templatefilename/currentfile>";
	}

	public String getOutputdir() {
		Object obj = getContext().getCurrenttemplate();
		return obj.toString();
	}

	public String getOutputname() {
		File f = (File)getContext().getCurrentfile();
		return f.getName();
	}

	public Boolean getDongenerate() {
		return Boolean.FALSE;
	}
}
