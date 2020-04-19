package com.temporal.model;

public enum EventDataType {
	STRING("string"), BOOLEAN("boolean"), DECIMAL("decimal"), INTEGER("integer"), LONG("long"), DATE("date"),
	DATE_TIME("dateTime");

	private String value;

	private EventDataType(String value) {
		this.value = value;
	}

	String getEventDataType() {
		return value;
	}
}
