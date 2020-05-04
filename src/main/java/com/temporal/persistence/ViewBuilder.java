package com.temporal.persistence;

public class ViewBuilder extends AbstractSqlBuilder {
	private String name;
	private SelectBuilder select;
	
	public ViewBuilder(String name, SelectBuilder select) {
		this.name = name;
		this.select = select;
	}
	
	@Override
	public String toString() {		
		return "CREATE VIEW " + name + " AS " + select.toString();
	}
}
