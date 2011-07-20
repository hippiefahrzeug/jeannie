package com.sb.jeannie.utils.db;

import java.util.List;

public class Table extends DatabaseSupport {
	private List<Column> columns;

	public List<Column> getColumns() {
		return columns;
	}
	
	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}
}
