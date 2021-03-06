package com.sb.jeannie.processors;

import java.io.File;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sb.jeannie.annotations.Processor;
import com.sb.jeannie.beans.Context;
import com.sb.jeannie.interfaces.Postprocessor;
import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.parser.ParseException;

@Processor
public class JavaPostprocessor extends DefaultPostprocessor implements Postprocessor {
    private static final String SRCDIR = "src";
	private final static Logger LOG = LoggerFactory.getLogger(JavaPostprocessor.class);
	private JavaDocBuilder builder;
	private JavaClass clazz;
	
	public String getDescription() {
		return "sets output location: <package>/<classname>.java";
	}

	public void init(Context context) {
		super.init(context);
		parse();
	}
	
	public String getOutputdir() {
		if (clazz == null) {
			return super.getOutputdir();
		}
		char s = File.separatorChar;
		String dir =  SRCDIR + s + clazz.getPackageName().replace('.', s);
		return dir;
	}

	public String getOutputname() {
		if (clazz == null) {
			return super.getOutputdir();
		}
		return clazz.getName() + ".java";
	}
	
	private void parse() {
		String result = (String)getContext().getResult();
		try {
			StringReader sr = new StringReader(result);
			builder = new JavaDocBuilder();
			builder.addSource(sr);
			JavaClass[] classes = builder.getClasses();
			for (int i = 0; i < classes.length; i++) {
				if (!classes[i].isInner()) {
					clazz = classes[i];
					break;
				}
			}
		}
		catch (ParseException pe) {
			List<String> lines = Arrays.asList(result.split("\n"));
			LOG.error(pe.getMessage());
			int errorline = pe.getLine();
			for (int i = errorline-3; i < errorline+3; i++) {
				if (i >= 0 && i < lines.size()) {
					if (i == errorline - 2) {
						LOG.error("{} ***", lines.get(i));
					}
					else {
						LOG.error("{}", lines.get(i));
					}
				}
			}
		}
	}
}
