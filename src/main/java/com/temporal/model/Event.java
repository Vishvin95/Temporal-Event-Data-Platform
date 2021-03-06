package com.temporal.model;

import javax.xml.bind.annotation.XmlElement;

public class Event {
	private String name;
	private String dataType;
	private boolean notNull;
	private boolean unique;
	private MOE moe;

	public Event() {
		
	}
	
	/**
	 * @param name     : Name of the event
	 * @param dataType : Data type of event as EventDataType.STRING,
	 *                 EventDataType.BOOLEAN, EventDataType.DECIMAL,
	 *                 EventDataType.INTEGER, EventDataType.LONG, EventDataType.DATE,
	 *                 EventDataType.DATE_TIME
	 */
	public Event(String name, EventDataType dataType, boolean notNull, boolean unique, MOE moe) {
		this.name = name;
		this.dataType = dataType.getEventDataType();
		this.notNull = notNull;
		this.unique = unique;
		this.moe = moe;
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
	
	
	@XmlElement(name = "moe")
	public MOE getMoe() {
		return moe;
	}

	public void setMoe(MOE moe) {
		this.moe = moe;
	}

	@Override
	public String toString() {
		String eventString;
		eventString = "\t" + name + "\t" + dataType;
		if(this.notNull)
			eventString = eventString + "\tnot_null";
		if(this.unique)
			eventString = eventString + "\tunique";
		if(this.moe != null && this.moe.isOverlap())
			eventString = eventString + "\tmoe";
		return eventString;
	}
}
