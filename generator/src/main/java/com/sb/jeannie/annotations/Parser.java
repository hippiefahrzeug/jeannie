package com.sb.jeannie.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Parser {
	/**
	 * @return a plain text name for the file format being parsed
	 */
	String type() default "unknown";
	
	/**
	 * @return a list of possible extensions that will trigger this parser
	 */
	String [] extensions() default {};
	
	/**
	 * @return priority of this parser. the lower the value, 
	 * the lower the priority. The priority determines which
	 * internalized object ends up in the inputmap. This is
	 * important if you have more than one parser responsible
	 * for parsing a particular file type e.g. if
	 * you have two parsers which parse java files, the parser
	 * with the higher priority wins and the object will end
	 * up in the inputmap.
	 * However, you can still retrieve other parser's version
	 * of the object with the given input file. Just get it from
	 * the parser directly.
	 */
	int prio() default 0;
}
