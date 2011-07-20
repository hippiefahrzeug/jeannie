package com.sb.jeannie.processors;

import java.util.Map;

import com.sb.jeannie.annotations.Processor;
import com.sb.jeannie.interfaces.ProcessorBase;

@Processor
public class DefaultProcessor implements ProcessorBase {
	private Map<String, Object> context;

	public void init(Map<String, Object> context) {
		this.context = context;
	}

	public Map<String, Object> getContext() {
		return context;
	}

	public void setContext(Map<String, Object> context) {
		this.context = context;
	}
	
	public String getName() {
		return getClass().getSimpleName();
	}

	public String getDescription() {
		return "sitting duck";
	}
}
