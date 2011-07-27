package com.sb.jeannie.parsers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sb.jeannie.annotations.Parser;

@Parser(type="csv", extensions={"csv"}, prio=1)
public class JarParser extends ParserSupport {
    private final static Logger LOG = LoggerFactory.getLogger(JarParser.class);
	
	public Object parse(File file) {
		try {
			JarFile jar = new JarFile(file);
			Enumeration<JarEntry> enumer = jar.entries();
			ArrayList<JarEntry> result = Collections.list(enumer);
			return result;
		}
		catch (Exception e) {
			LOG.error("exception caught: " + file, e);
			return null;
		}
	}
}
