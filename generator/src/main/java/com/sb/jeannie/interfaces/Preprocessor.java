package com.sb.jeannie.interfaces;

import java.util.List;


/**
 * a scriptlet extending Postprocess will run just before
 * the rendering phase of a template. use this to do the following
 * things:
 *    - collect model data
 * 
 * @author alvi
 */
public interface Preprocessor extends ProcessorBase {
	/**
	 * returns a list of objects for which the current file/template
	 * combination should be generated. 
	 * 
	 * The default implements the case of 1-1 generation: the list
	 * just contains the current object.
	 * 
	 * @return
	 */
	public List<Object> generatefor();
}
