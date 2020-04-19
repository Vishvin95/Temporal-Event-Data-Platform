package com.temporal.model;

public enum EventType {
	SOE("SOE"), MOE("MOE");

	private String value;

	private EventType(String value) {
		this.value = value;
	}

	String getEventType() {
		return value;
	}
}
