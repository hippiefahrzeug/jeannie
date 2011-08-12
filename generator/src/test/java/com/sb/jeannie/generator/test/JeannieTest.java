package com.sb.jeannie.generator.test;

import java.io.File;

import org.junit.Test;

import com.sb.jeannie.ModulesHandler;

public class JeannieTest {
	
	public static final String [][] testcases = {
		{"../modules/testbed",
			 "../test",
			 "../generator/target/test/testbed"},
		{"../modules/propertyslurper",
			 "../test",
			 "../generator/target/test/propertyslurper"}
	};
	
	@Test
	public void testGenerator() {
		for (int i = 0; i < testcases.length ;i++) {
			File modulelocation = new File(testcases[i][0]);
			File inputlocation = new File(testcases[i][1]);
			File outputlocation = new File(testcases[i][2]);
			
			ModulesHandler handler = new ModulesHandler(
					modulelocation, 
					inputlocation,
					outputlocation,
					null,
					null);
			handler.generateAll();
		}
	}
}
