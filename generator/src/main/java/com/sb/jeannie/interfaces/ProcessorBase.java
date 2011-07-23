package com.sb.jeannie.interfaces;

import com.sb.jeannie.beans.Context;

public interface ProcessorBase {
	public void init(Context context);
	public String getName();
	public String getDescription();
}