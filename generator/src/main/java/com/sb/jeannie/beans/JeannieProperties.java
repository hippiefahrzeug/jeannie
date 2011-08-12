package com.sb.jeannie.beans;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author alvi
 */
public class JeannieProperties extends BeanSupport {
	private static final Logger LOG = LoggerFactory.getLogger(JeannieProperties.class);
	
    public static final String GLOBAL_ENCODING = "globalEncoding";
    public static final String GLOBAL_SKIP_UPTODATE_CHECK = "globalSkipUptodateCheck";
    public static final String GLOBAL_VERBOSE = "globalVerbose";
    public static final String GLOBAL_DEBUG = "globalDebug";
    public static final String GLOBAL_TYPES = "globalTypes";
    public static final String GLOBAL_DELIMITER_START_CHAR = "globalDelimiterStartChar";
    public static final String GLOBAL_DELIMITER_END_CHAR = "globalDelimiterEndChar";
    public static final String GLOBAL_EXTERNAL_PACKAGE = "globalExternalPackage";
    public static final String CSV_SEPARATOR = "csvSeparator";
    public static final String CSV_SKIP_LINES = "csvSkipLines";
    public static final String CSV_QUOTE_CHARACTER = "csvQuoteCharacter";

    protected static Map<String, String> PROPERTY_MAP;
	protected static Map<String, String> props;

    static {
        PROPERTY_MAP = new HashMap<String, String>();

        PROPERTY_MAP.put(GLOBAL_ENCODING, "set encoding type for read and written files");
        PROPERTY_MAP.put(GLOBAL_SKIP_UPTODATE_CHECK, "skip up-to-date check");
        PROPERTY_MAP.put(GLOBAL_VERBOSE, "print out the index whenever the generator runs");
        PROPERTY_MAP.put(GLOBAL_DEBUG, "DEBUG mode - causes lots of output");
        PROPERTY_MAP.put(GLOBAL_TYPES, "only parse these types of files (comma separated)");
        PROPERTY_MAP.put(GLOBAL_DELIMITER_START_CHAR, "start delimiter character for stringtemplate expressions");
        PROPERTY_MAP.put(GLOBAL_DELIMITER_END_CHAR, "end delimiter character for stringtemplate expressions");
        PROPERTY_MAP.put(GLOBAL_EXTERNAL_PACKAGE, "user package that will be scanned to find parsers and processors");
        PROPERTY_MAP.put(CSV_SEPARATOR, "csv parser: character to use for separation");
        PROPERTY_MAP.put(CSV_SKIP_LINES, "csv parser: how many lines to skip");
        PROPERTY_MAP.put(CSV_QUOTE_CHARACTER, "csv parser: character used to quote");

        init();
    }
    
    public static void init() {
        // set the defaults here
        props = new HashMap<String, String>();
        
        props.put(GLOBAL_ENCODING, "ISO-8859-1");
        props.put(GLOBAL_SKIP_UPTODATE_CHECK, "false");
        props.put(GLOBAL_VERBOSE, "false");
        props.put(GLOBAL_DEBUG, "true");
        props.put(GLOBAL_TYPES, "all");
        props.put(GLOBAL_DELIMITER_START_CHAR, "$");
        props.put(GLOBAL_DELIMITER_END_CHAR, "$");
        props.put(GLOBAL_EXTERNAL_PACKAGE, null);
        props.put(CSV_SEPARATOR, ",");
        props.put(CSV_SKIP_LINES, "0");
        props.put(CSV_QUOTE_CHARACTER, "\"");
        
        Properties sysproperties = System.getProperties();
        handleProperties(PROPERTY_MAP, sysproperties, props);
    }
    
    public static void handleProperties(Properties properties) {
        handleProperties(PROPERTY_MAP, properties, props);
    }
    
	protected Map<String, String> getPropertyMap() {
		return PROPERTY_MAP;
	}

	public Map<String, String> getProps() {
		return props;
	}

    public static Map<String, String> getProperties() {
    	return props;
    }

	public static void log() {
		log(LOG, PROPERTY_MAP, props);
	}
	
    public static String getIndex() {
        return props.get(INDEX);
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

    public static String getGlobalExternalPackage() {
        return props.get(GLOBAL_EXTERNAL_PACKAGE);
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
