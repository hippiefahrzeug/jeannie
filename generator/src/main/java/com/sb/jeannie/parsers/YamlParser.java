package com.sb.jeannie.parsers;

import java.io.File;
import java.io.FileReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.sb.jeannie.annotations.Parser;

@Parser(type="yaml", extensions={"yaml"}, prio=1)
public class YamlParser extends ParserSupport {
    private final static Logger LOG = LoggerFactory.getLogger(YamlParser.class);

	public Object parse(File file) {
		try {
	        Yaml yaml = new Yaml();
			FileReader fr = new FileReader(file);
	        Object load = yaml.load(fr);
			return load;
		}
		catch (Exception e) {
			LOG.error("exception caught: " + file, e);
			return null;
		}
	}
}
