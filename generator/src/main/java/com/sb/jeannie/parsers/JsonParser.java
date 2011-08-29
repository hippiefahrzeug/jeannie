package com.sb.jeannie.parsers;

import java.io.File;
import java.io.FileReader;
import java.net.URLClassLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.sb.jeannie.Generator;
import com.sb.jeannie.annotations.Parser;
import com.sb.jeannie.beans.JeannieProperties;

@Parser(type="json", extensions={"json"}, prio=1)
public class JsonParser extends ParserSupport {
    private final static Logger LOG = LoggerFactory.getLogger(JsonParser.class);

	public Object parse(File file) {
		try {
			Gson gson = new Gson();
			FileReader fr = new FileReader(file);
			
			URLClassLoader cl = (URLClassLoader)Generator.class.getClassLoader();
			String cn = JeannieProperties.getJsonClassname();
			Class<?> scriptletClass = cl.loadClass(cn);
			
			Object fromJson = gson.fromJson(fr, scriptletClass);
			return fromJson;
		}
		catch (Exception e) {
			LOG.error("exception caught: " + file, e);
			return null;
		}
	}
}
