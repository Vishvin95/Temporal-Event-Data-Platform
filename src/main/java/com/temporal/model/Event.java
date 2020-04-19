package com.temporal.model;

public class Event {
	private String name;
	private String dataType;
	private String type;

	public Event() {
		
	}
	
	/**
	 * @param name     : Name of the event
	 * @param dataType : Data type of event as EventDataType.STRING,
	 *                 EventDataType.BOOLEAN, EventDataType.DECIMAL,
	 *                 EventDataType.INTEGER, EventDataType.LONG, EventDataType.DATE,
	 *                 EventDataType.DATE_TIME
	 * @param type     : Is it a single occurrence event ( EventType.SOE ) or multi occurrence event ( EventType.MOE ) 
	 */
	public Event(String name, EventDataType dataType, EventType type) {
		this.name = name;
		this.dataType = dataType.getEventDataType();
		this.type = type.getEventType();
	}

	public String getname() {
		return name;
	}

	public void setname(String name) {
		this.name = name;
	}

	public String getdataType() {
		return dataType;
	}

	public void setdataType(String dataType) {
		this.dataType = dataType;
	}

	public String gettype() {
		return type;
	}

	public void settype(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "\t" + name + "\t" + dataType + "\t" + type;
	}
}
