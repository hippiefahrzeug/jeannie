package com.sb.jeannie;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
	private static final Logger LOG = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) {
		if (args.length < 3) {
			usage();
			return;
		}
		
		int idx = 0;
		String mode = args[0];
		
		if (mode.equals("-looper")) {
			idx = 1;
			if (args.length < 3) {
				usage();
				return;
			}
		}
		
		File module = new File(args[idx++]);
		File input = new File(args[idx++]);
		File output = new File(args[idx++]);
		
		List<File> props = new ArrayList<File>();
		for (int i = idx; i < args.length; i++) {
			props.add(new File(args[i]));
		}
		
		/*
		Jeannie jeannie = new Jeannie(module, input, output, props);
		jeannie.generate();
		*/
		
		ModulesHandler handler = new ModulesHandler(module, input, output, props, null);
		handler.generateAll();
	}
	
	private static void usage() {
		LOG.error("usage: [-looper] <moduledir> <inputdir> <outputdir>");
	}
}
