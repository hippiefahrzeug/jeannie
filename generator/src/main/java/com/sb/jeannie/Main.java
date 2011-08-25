package com.sb.jeannie;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sb.jeannie.beans.Output;
import com.sb.jeannie.utils.ChangeChecker;
import com.sb.jeannie.utils.Utils;

public class Main {
	private static final Logger LOG = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) {
		if (args.length < 3) {
			usage();
			return;
		}
		
		LogConfiguration.configure();
		
		ModulesHandler handler = createHandler(args);
		
		if (handler != null) {
			if (!args[0].equals("-looper")) {
				handler.generateAll();
			}
			else {
				looper(handler);
			}
		}
	}
	
	private static void usage() {
		LOG.error("usage: [-looper] <moduledir> <inputdir> <outputdir> [propertyfile_1]...[propertyfile_n]");
	}
	
	protected static ModulesHandler createHandler(String[] args) {
		int idx = 0;
		String mode = args[0];
		
		if (mode.equals("-looper")) {
			idx = 1;
			if (args.length < 3) {
				usage();
				return null;
			}
		}
		
		File module = new File(args[idx++]);
		File input = new File(args[idx++]);
		File output = new File(args[idx++]);
		
		List<File> props = new ArrayList<File>();
		for (int i = idx; i < args.length; i++) {
			props.add(new File(args[i]));
		}
		
		ModulesHandler handler = new ModulesHandler(module, input, output, props, null);
		return handler;
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
