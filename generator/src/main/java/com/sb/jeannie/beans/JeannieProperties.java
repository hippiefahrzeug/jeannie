package com.sb.jeannie.beans;

import java.util.HashMap;
import java.util.Map;

import org.stringtemplate.v4.STGroup;

/**
 * @author alvi
 */
public class JeannieProperties extends BeanSupport {
    public static final String GLOBAL_ENCODING = "globalEncoding";
    public static final String GLOBAL_SKIP_UPTODATE_CHECK = "globalSkipUptodateCheck";
    public static final String GLOBAL_VERBOSE = "globalVerbose";
    public static final String GLOBAL_DEBUG = "globalDebug";
    public static final String GLOBAL_TYPES = "globalTypes";
    public static final String GLOBAL_DELIMITER_START_CHAR = "globalDelimiterStartChar";
    public static final String GLOBAL_DELIMITER_END_CHAR = "globalDelimiterEndChar";
    public static final String CSV_SEPARATOR = "csvSeparator";
    public static final String CSV_SKIP_LINES = "csvSkipLines";
    public static final String CSV_QUOTE_CHARACTER = "csvQuoteCharacter";

    static {
        PROPERTY_MAP = new HashMap<String, String>();

        PROPERTY_MAP.put(GLOBAL_ENCODING, "set encoding type for read and written files");
        PROPERTY_MAP.put(GLOBAL_SKIP_UPTODATE_CHECK, "skip up-to-date check");
        PROPERTY_MAP.put(GLOBAL_VERBOSE, "print out the index whenever the generator runs");
        PROPERTY_MAP.put(GLOBAL_DEBUG, "debug mode - causes lots of output");
        PROPERTY_MAP.put(GLOBAL_TYPES, "only parse these types of files (comma separated)");
        PROPERTY_MAP.put(GLOBAL_DELIMITER_START_CHAR, "start delimiter character for stringtemplate expressions");
        PROPERTY_MAP.put(GLOBAL_DELIMITER_END_CHAR, "end delimiter character for stringtemplate expressions");
        PROPERTY_MAP.put(CSV_SEPARATOR, "csv parser: character to use for separation");
        PROPERTY_MAP.put(CSV_SKIP_LINES, "csv parser: how many lines to skip");
        PROPERTY_MAP.put(CSV_QUOTE_CHARACTER, "csv parser: character used to quote");

        // set the defaults here
        props = new HashMap<String, String>();
        
        props.put(GLOBAL_ENCODING, "ISO-8859-1");
        props.put(GLOBAL_SKIP_UPTODATE_CHECK, "false");
        props.put(GLOBAL_VERBOSE, "false");
        props.put(GLOBAL_DEBUG, "false");
        props.put(GLOBAL_TYPES, "all");
        props.put(GLOBAL_DELIMITER_START_CHAR, "$");
        props.put(GLOBAL_DELIMITER_END_CHAR, "$");
        props.put(CSV_SEPARATOR, ",");
        props.put(CSV_SKIP_LINES, "0");
        props.put(CSV_QUOTE_CHARACTER, "\"");
    }
    
    public JeannieProperties(STGroup stg, Map<String, Object> properties) {
        handleTemplates(PROPERTY_MAP, stg);
        handleProperties(PROPERTY_MAP, properties);
    }

    public static String getGlobalEncoding() {
        return props.get(GLOBAL_ENCODING);
    }

    public static String getGlobalSkipUptodateCheck() {
        return props.get(GLOBAL_SKIP_UPTODATE_CHECK);
    }

    public static String getGlobalVerbose() {
        return props.get(GLOBAL_VERBOSE);
    }

    public static String getGlobalDebug() {
        return props.get(GLOBAL_DEBUG);
    }

    public static String getGlobalTypes() {
        return props.get(GLOBAL_TYPES);
    }

    public static String getGlobalDelimiterStartChar() {
        return props.get(GLOBAL_DELIMITER_START_CHAR);
    }

    public static String getGlobalDelimiterEndChar() {
        return props.get(GLOBAL_DELIMITER_END_CHAR);
    }

    public static String getCsvSeparator() {
        return props.get(CSV_SEPARATOR);
    }

    public static String getCsvSkipLines() {
        return props.get(CSV_SKIP_LINES);
    }

    public static String getCsvQuoteCharacter() {
        return props.get(CSV_QUOTE_CHARACTER);
    }
}
