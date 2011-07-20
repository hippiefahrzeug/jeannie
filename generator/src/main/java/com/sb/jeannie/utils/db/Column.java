package com.sb.jeannie.utils.db;

import com.sb.jeannie.utils.db.Types.Type;

public class Column extends DatabaseSupport {
	private Type type;
	private int size;
	private boolean primary;
	private boolean required;
	
	public Column() {
		primary = false;
		required = false;
	}
	
	public Type getType() {
		return type;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
	public int getSize() {
		return size;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public boolean isRequired() {
		return required;
	}
	
	public void setRequired(boolean required) {
		this.required = required;
	}
	
	public boolean isPrimary() {
		return primary;
	}
	
	public void setPrimary(boolean primary) {
		this.primary = primary;
	}
}
