package com.sb.jeannie.parsers;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sb.jeannie.annotations.Parser;
import com.thoughtworks.qdox.JavaDocBuilder;

@Parser(type="java", extensions={"java"}, prio=1)
public class JavaParser extends ParserSupport {
    private final static Logger LOG = LoggerFactory.getLogger(JavaParser.class);
	
	public Object parse(File file) {
		try {
			JavaDocBuilder javaDocBuilder = new JavaDocBuilder();
			javaDocBuilder.addSource(file);
			return javaDocBuilder;
		}
		catch (Exception e) {
			LOG.error("exception caught: " + file, e);
			return null;
		}
	}
}
