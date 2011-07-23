package com.sb.jeannie.processors;

import java.util.ArrayList;
import java.util.List;

import com.sb.jeannie.annotations.Processor;
import com.sb.jeannie.interfaces.Preprocessor;

@Processor
public class DefaultPreprocessor extends DefaultProcessor implements Preprocessor {

	/**
	 * this is the default: we generate one output file per input file,
	 * therefore this list contains just the current object.
	 */
	public List<Object> generatefor() {
		ArrayList<Object> result = new ArrayList<Object>();
		result.add(getContext().getCurrent());
		return result;
	}
}
