package com.sb.jeannie.utils.db;

public abstract class DatabaseSupport {
	private String description;
	private String name;

	public String getDescription() {
		if (description == null) {
			return "description missing";
		}
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
