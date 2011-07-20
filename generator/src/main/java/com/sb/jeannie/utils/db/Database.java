package com.sb.jeannie.utils.db;

import java.util.List;

import com.sb.jeannie.utils.StringUtils;

public class Database extends DatabaseSupport {
	private List<Table> tables;

	public List<Table> getTables() {
		return tables;
	}

	public void setTables(List<Table> tables) {
		this.tables = tables;
	}
	
	@Override
	public String toString() {
		return StringUtils.toString(this);
	}
}
