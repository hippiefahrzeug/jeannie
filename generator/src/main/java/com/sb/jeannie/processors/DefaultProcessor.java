package com.sb.jeannie.processors;

import com.sb.jeannie.annotations.Processor;
import com.sb.jeannie.beans.Context;
import com.sb.jeannie.interfaces.ProcessorBase;

@Processor
public class DefaultProcessor implements ProcessorBase {
	private Context context;

	public void init(Context context) {
		this.context = context;
	}

	public Context getContext() {
		return context;
	}

	public String getName() {
		return getClass().getSimpleName();
	}

	public String getDescription() {
		return "sitting duck";
	}
}
