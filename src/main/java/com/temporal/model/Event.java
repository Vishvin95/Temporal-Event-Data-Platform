package com.temporal.model;

public class Event {
	private String name;
	private String dataType;
	private String type;

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
