package com.sb.jeannie.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * helper class that detect changes in files.
 * 
 * @author alvi
 */
public class ChangeChecker {
	private static Map<File, Long> ages = new HashMap<File, Long>();
	private List<File> files = new ArrayList<File>();
	private Set<String> ignore;
	private long lastModified = 0;
	
	/**
	 * @param ignore set of file names that shall be ignored
	 */
	public ChangeChecker(File file, Set<String> ignore) {
		this.ignore = ignore;
		add(file);
	}
	
	public ChangeChecker(File file) {
		add(file);
	}
	
	/**
	 * returns true if file compareFrom (or all files in compareFrom, if
	 * it points to a directory) is newer than file compareTo (or newer
	 * than any file in compareTo if it points to a directory)
	 * 
	 * @param compareFrom
	 * @param compareTo
	 */
	public static boolean newerThan(File compareFrom, File compareTo) {
		return newerThan(compareFrom, compareTo, null);
	}
	
	/**
	 * returns true if file compareFrom (or all files in compareFrom, if
	 * it points to a directory) is newer than file compareTo (or newer
	 * than any file in compareTo if it points to a directory)
	 * 
	 * @param compareFrom
	 * @param compareTo
	 */
	public static boolean newerThan(File compareFrom, File compareTo, Set<String> ignore) {
		ChangeChecker cc = new ChangeChecker(compareFrom, ignore);
		cc.hasChangedFiles();
		ages.put(compareFrom, cc.lastModified);
		cc.add(compareTo);
		boolean result = cc.hasChangedFiles();
		ages.put(compareTo, cc.lastModified);
		return !result;
	}
	
	/**
	 * adds files to the list of observed files. if path 
	 * points to a plain file, adds it. if path points to
	 * a directory, add all plain files of that directory
	 * 
	 * @param path
	 */
	public void add(File path) {
		if (path == null) {
			return;
		}
		
		if (!path.isDirectory()) {
			files.add(path);
		}
		else {
			files.addAll(Utils.allfiles(path, ignore));
		}
	}
	
	/**
	 * calling this method will set lastModified to the
	 * lastModified of the youngest obseved file.
	 * 
	 * @return true on first call, true or false on second call
	 */
	public boolean hasChangedFiles() {
		boolean result = false;
		if (files.size() == 0) {
			return result = true;
		}
		for (File file : files) {
			if (file.exists()) {
				long l = file.lastModified();
				if (l > lastModified) {
					lastModified = l;
					result = true;
				}
			}
		}
		return result;
	}
	
	public int numberOfFiles() {
		return files.size();
	}

	public static Long getAge(File f) {
        long now = System.currentTimeMillis();
		long lm = ages.get(f);
		return now-lm;
	}
}
