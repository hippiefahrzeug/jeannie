package com.sb.jeannie;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sb.jeannie.beans.Output;
import com.sb.jeannie.utils.ChangeChecker;
import com.sb.jeannie.utils.Utils;


/**
 * looper() remains in a loop of calling generate()
 * whenever any files have changed (both input or
 * module files.)
 */
public class Looper extends Main {
	private static final Logger LOG = LoggerFactory.getLogger(Looper.class);
	
	public static void main(String[] args) {
		ModulesHandler handler = createHandler(args);
		looper(handler);
	}
	
	/**
	 * never stops and calls generator whenever it detects a change.
	 */
	private static void looper(ModulesHandler handler) {
		Set<String> ignore = new HashSet<String>();
		ignore.add(Output.WORKINGDIR);

		File modulelocation = handler.getModuleslocation();
		File inputlocation = handler.getInputlocation();
		List<File> propertyfiles = handler.getPropertyfiles();
		
		ChangeChecker inputfiles = new ChangeChecker(inputlocation, ignore);
		ChangeChecker modulefiles = new ChangeChecker(modulelocation, ignore);
		
		for (File file : propertyfiles) {
			modulefiles.add(file);
		}
		
		inputfiles.hasChangedFiles(); // don't parse first time
		int n = 0;
		int numInputfiles = Utils.allfiles(inputlocation).size();
		int numModulefiles = Utils.allfiles(modulelocation).size();
		do {
			try {
				if (n % 4 == 0) { // expensive. don't do this all the time...
					int num = Utils.allfiles(inputlocation).size();
					if (numInputfiles != num) {
						numInputfiles = num;
						inputfiles = new ChangeChecker(inputlocation, ignore);
					}
					num = Utils.allfiles(modulelocation).size();
					if (numModulefiles != num) {
						numModulefiles = num;
						modulefiles = new ChangeChecker(modulelocation, ignore);
					}
				}
				
				if (inputfiles.hasChangedFiles() ||
					modulefiles.hasChangedFiles()
				) {
					handler.generateAll();
				}

				Thread.sleep(500);
			}
			catch (Exception e) {
				LOG.error("exception caught", e);
			}
			n++;
		} while(true);
	}
}
