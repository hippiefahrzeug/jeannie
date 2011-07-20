package com.sb.jeannie;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
    private final static Logger LOG = LoggerFactory.getLogger(Utils.class);
    public static Charset ENCODING;
    public static String NL = System.getProperty("line.separator");
    public static String DEFAULTCHARSET = "ISO-8859-1";
    
    public static final String PROPERTY_ENCODING = "encoding";

    static {
        setEncoding();
    }
    
    public static void setEncoding() {
    	String charset = System.getProperty(PROPERTY_ENCODING);
    	try {
    		if (charset == null) {
    			charset = DEFAULTCHARSET;
    		}
    		LOG.debug("setting encoding to: " + charset);
    		ENCODING = Charset.forName(charset);
    	}
    	catch (Exception e) {
    		LOG.error("exception caught", e);
    	}
    	finally {
    		if (ENCODING == null) {
    			LOG.error("charset '" + charset + "' not supported, setting to: '" + DEFAULTCHARSET + "'");
    			ENCODING = Charset.forName(DEFAULTCHARSET);
    		}
    	}
    }

	public static List<File> allfiles(File path) {
		return allfiles(path, null);
	}
	
	/**
	 * return a list of all files found inside 'path'. If
	 * 'extension' is not null, only returns files that end 
	 * with the extension-string.
	 * 
	 * If path is a plain file, a list with only that file 
	 * is returned.
	 * 
	 * @param path
	 * @param extension
	 * @return never returns null
	 */
	public static List<File> allfiles(File path, String extension) {
		ArrayList<File> files = new ArrayList<File>();
		allfiles(files, path, extension);
		return files;
	}
	
	private static void allfiles(List<File> files, File path, String extension) {
		if (!path.exists()) {
			return;
		}
		
		if (path.isDirectory()) {
			File[] listFiles = path.listFiles();
			for (int i = 0; i < listFiles.length; i++) {
				File file = listFiles[i];
				allfiles(files, file, extension);
			}
		}
		else if (path.isFile()) {
			if (extension == null || path.getName().endsWith(extension)) {
				files.add(path);
			}
		}
	}
	
	/**
	 * deletes all plain files within path if it is a directory
	 * deletes file, if path is a plain file
	 * 
	 * @param path
	 */
	public static void deleteAll(File path) {
		List<File> files = allfiles(path);
		for (File file : files) {
			file.delete();
		}
	}
	
    public static String loadFileAsString(File file) {
    	List<String> cont = loadFile(file);
    	StringBuilder sb = new StringBuilder();
    	for (String line : cont) {
        	sb.append(line);
        	sb.append(NL);
		}
    	return sb.toString();
    }

    public static List<String> loadFile(File file) {
		BufferedReader br = null;
		try {
			if (!file.isFile()) {
				return null;
			}
			InputStream is = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(is, ENCODING);
			br = new BufferedReader(isr);
			String s = null;
			List<String> lines = new ArrayList<String>();
			while ((s = br.readLine()) != null) {
				lines.add(s);
			}
			return lines;
		}
		catch (IOException e) {
			LOG.error("exception caught", e);
			return new ArrayList<String>();
		}
		finally {
			if (br != null) {
				try {
					br.close();
				}
				catch (IOException e) {
					LOG.error("exception caught", e);
				}
			}
		}
    }
    
    static <T> T nvl(T in, T def) {
    	if(in == null) {
    		return def;
    	}
    	return in;
    }
}
