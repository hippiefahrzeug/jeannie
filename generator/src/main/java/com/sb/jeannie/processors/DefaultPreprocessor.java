package com.sb.jeannie.processors;

import java.util.ArrayList;
import java.util.List;

import com.sb.jeannie.annotations.Processor;
import com.sb.jeannie.interfaces.Preprocessor;

@Processor
public class DefaultPreprocessor extends DefaultProcessor implements Preprocessor {

	/**
	 * this is the default: we generate one output file per input file,
	 * therefore this list is empty. i.e. only the 'current' object is
	 * used for generation. jeannie will add 'current' if this list is
	 * empty.
	 * 
	 * Note that jeannie takes *all* generatefor lists of all 
	 * preprocessors and combines them in as set.
	 * 
	 * If this list contains objects, then the generate will iterate 
	 * through them and set the 'iterator' property. Additionalli, a
	 * 'counter' object contains the number of the current iterator
	 * within the 'generatefor'- list
	 * 
	 * 'current' is always set to the original object..
	 */
	public List<Object> generatefor() {
		ArrayList<Object> result = new ArrayList<Object>();
		// result.add(getContext().getCurrent());
		return result;
	}
}
