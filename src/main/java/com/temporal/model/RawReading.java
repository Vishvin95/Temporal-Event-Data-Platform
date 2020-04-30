package com.temporal.model;

import javax.xml.bind.annotation.XmlElement;

public class RawReading {
	private String key;
	private String value;
	
	@XmlElement(name = "key")
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	@XmlElement(name = "value")
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
