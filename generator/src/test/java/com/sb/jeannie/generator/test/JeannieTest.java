package com.sb.jeannie.generator.test;

import org.junit.Test;

import com.sb.jeannie.Jeannie;

public class JeannieTest {
	
	public static final String [][] testcases = {
			{"../modules/testbed",
			 "../test",
			 "../generator/target/test/testbed"}
	};
	
	@Test
	public void testGenerator() {
		for (int i = 0; i < testcases.length ;i++) {
			String modulelocation = testcases[0][0];
			String inputlocation = testcases[0][1];
			String outputlocation = testcases[0][2];
			Jeannie jeannie = new Jeannie(
					modulelocation, 
					inputlocation,
					outputlocation
			);
			jeannie.generate();
		}
	}
}
