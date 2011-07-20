package com.sb.jeannie.interfaces;

/**
 * a scriptlet extending Postprocess will run immediately after
 * the rendering phase of a template. use this to do the following
 * things:
 *    - specify the output name of the generated file
 *    - specify the output directory of the generated file
 *      (specify a relative path name!)
 *    - beautify the generated output
 *    - delete the generated output if some condition is met
 * @author alvi
 */
public interface Postprocessor extends ProcessorBase {
	public String getOutputdir();
	public String getOutputname();
	public Boolean getDongenerate();
}
