package com.sb.jeannie.parsers;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sb.jeannie.Utils;
import com.sb.jeannie.annotations.Parser;

@Parser(type="plain")
public class PlainFileParser extends ParserSupport {
    private final static Logger LOG = LoggerFactory.getLogger(PlainFileParser.class);
	
	public Object parse(File file) {
		try {
			return Utils.loadFile(file);
		}
		catch (Exception e) {
			LOG.error("exception caught: " + file, e);
			return null;
		}
	}
}
