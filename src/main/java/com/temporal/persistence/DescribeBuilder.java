package com.temporal.persistence;

public class DescribeBuilder extends AbstractSqlBuilder{
	private String table;
	
	public DescribeBuilder(String table) {
		this.table = table;
	}
	
	@Override
	public String toString() {
		StringBuilder sqlBuilder = new StringBuilder().append("DESCRIBE ").append(table);
		return sqlBuilder.toString();
	}
}
