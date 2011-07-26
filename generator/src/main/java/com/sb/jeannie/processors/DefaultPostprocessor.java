package com.sb.jeannie.processors;

import java.io.File;

import com.sb.jeannie.annotations.Processor;
import com.sb.jeannie.beans.Context;
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
		Context context = getContext();
		Integer counter = context.getCounter();
		File f = context.getCurrentfile();
		String appendix = "";
		if (counter > 0) {
			appendix =  "_" + counter;
		}
		return f.getName() + appendix;
	}

	public Boolean getDongenerate() {
		return Boolean.FALSE;
	}
}
