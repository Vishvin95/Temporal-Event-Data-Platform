package com.temporal.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

public class Event {
	private String name;
	private String dataType;
	private String type;
	private boolean notNull;
	private boolean unique;

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
	public Event(String name, EventDataType dataType, EventType type, boolean notNull, boolean unique) {
		this.name = name;
		this.dataType = dataType.getEventDataType();
		this.type = type.getEventType();
		this.notNull = notNull;
		this.unique = unique;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@XmlElement(name = "notnull")
	public boolean isNotNull() {
		return notNull;
	}

	public void setNotNull(boolean notNull) {
		this.notNull = notNull;
	}

	@XmlElement(name = "unique")
	public boolean isUnique() {
		return unique;
	}

	
	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	@Override
	public String toString() {
		String eventString;
		eventString = "\t" + name + "\t" + dataType + "\t" + type;
		if(this.notNull)
			eventString = eventString + "\tnot_null";
		if(this.unique)
			eventString = eventString + "\tunique";
		return eventString;
	}
}
