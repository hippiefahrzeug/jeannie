package com.sb.jeannie.parsers;

import java.io.File;
import java.io.FileReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.sb.jeannie.annotations.Parser;
import com.sb.jeannie.utils.db.Database;

@Parser(type="json", extensions={"json"}, prio=1)
public class JsonParser extends ParserSupport {
    private final static Logger LOG = LoggerFactory.getLogger(JsonParser.class);

	public Object parse(File file) {
		try {
			Gson gson = new Gson();
			FileReader fr = new FileReader(file);
			Object fromJson = gson.fromJson(fr, Database.class);
			return fromJson;
		}
		catch (Exception e) {
			LOG.error("exception caught: " + file, e);
			return null;
		}
	}
}
