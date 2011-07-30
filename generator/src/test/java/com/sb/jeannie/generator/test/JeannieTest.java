package com.sb.jeannie.generator.test;

import org.junit.Test;

import com.sb.jeannie.Jeannie;

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
			String modulelocation = testcases[i][0];
			String inputlocation = testcases[i][1];
			String outputlocation = testcases[i][2];
			Jeannie jeannie = new Jeannie(
					modulelocation, 
					inputlocation,
					outputlocation
			);
			jeannie.generate();
		}
	}
}
