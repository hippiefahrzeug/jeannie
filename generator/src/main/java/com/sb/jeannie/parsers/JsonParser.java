package com.sb.jeannie.parsers;

import java.io.File;
import java.io.FileReader;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
			Object fromJson = null;
			if (cn.contains("List")) {
				fromJson = gson.fromJson(fr, new TypeToken<List<Object>>() {}.getType());
			}
			else if (cn.contains("Map")) {
				fromJson = gson.fromJson(fr, new TypeToken<Map<Object, Object>>() {}.getType());
			}
			else {
				Class<?> jClass = cl.loadClass(cn);
				fromJson = gson.fromJson(fr, jClass);
			}
			
			return fromJson;
		}
		catch (Exception e) {
			LOG.error("exception caught: " + file, e);
			return null;
		}
	}
}
