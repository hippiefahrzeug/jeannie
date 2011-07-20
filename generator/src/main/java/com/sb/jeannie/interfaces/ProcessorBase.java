package com.sb.jeannie.interfaces;

import java.util.Map;

public interface ProcessorBase {
	public void init(Map<String, Object> context);
	public String getName();
	public String getDescription();
}