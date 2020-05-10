package com.temporal.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class Column {
	private String key;
	private String value;
	private Date validFrom;
	private Date validTo;
	
	public Column() {
	
	}
	
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
	
	@XmlElement(name = "validFrom")
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public Date getValidFrom() {
		return validFrom;
	}
	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}
	
	@XmlElement(name = "validTo")
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public Date getValidTo() {
		return validTo;
	}
	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}

	@Override
	public String toString() {
		return "Column{" +
				"key='" + key + '\'' +
				", value='" + value + '\'' +
				", validFrom=" + validFrom +
				", validTo=" + validTo +
				'}';
	}
}
