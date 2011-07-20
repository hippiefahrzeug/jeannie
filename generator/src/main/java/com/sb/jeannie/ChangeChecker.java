package com.sb.jeannie;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * helper class that detect changes in files.
 * 
 * @author alvi
 */
public class ChangeChecker {
	private List<File> files = new ArrayList<File>();
	private long lastModified = 0;
	
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
		ChangeChecker cc = new ChangeChecker(compareTo);
		cc.hasChangedFiles();
		cc.add(compareFrom);
		return cc.hasChangedFiles();
	}
	
	/**
	 * adds files to the list of observed files. if path 
	 * points to a plain file, adds it. if path points to
	 * a directory, add all plain files of that directory
	 * 
	 * @param path
	 */
	public void add(File path) {
		if (!path.isDirectory()) {
			files.add(path);
		}
		else {
			files.addAll(Utils.allfiles(path));
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
}
