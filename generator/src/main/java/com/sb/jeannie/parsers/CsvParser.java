package com.sb.jeannie.parsers;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

import com.sb.jeannie.annotations.Parser;
import com.sb.jeannie.beans.JeannieProperties;

@Parser(type="csv", extensions={"csv"}, prio=1)
public class CsvParser extends ParserSupport {
    private final static Logger LOG = LoggerFactory.getLogger(CsvParser.class);
	
	public Object parse(File file) {
		try {
			CSVReader reader = new CSVReader(
					new FileReader(file), 
					JeannieProperties.getCsvSeparator().charAt(0), 
					JeannieProperties.getCsvQuoteCharacter().charAt(0), 
					Integer.valueOf(JeannieProperties.getCsvSkipLines())
			);

            List<String[]> lines = reader.readAll();
            List<List<String>> result = new ArrayList<List<String>>();
            for (String[] line : lines) {
                result.add(Arrays.asList(line));
			}
			return result;
		}
		catch (Exception e) {
			LOG.error("exception caught: " + file, e);
			return null;
		}
	}
}
