package com.sb.jeannie.parsers;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sb.jeannie.annotations.Parser;

@Parser(type="properties", extensions={"properties"}, prio=1)
public class PropertiesParser extends ParserSupport {
    private final static Logger LOG = LoggerFactory.getLogger(PropertiesParser.class);
	
	public Object parse(File file) {
		try {
			Properties p = new Properties();
			p.load(new FileInputStream(file));
			return p;
		}
		catch (Exception e) {
			LOG.error("exception caught: " + file, e);
			return null;
		}
	}
}
