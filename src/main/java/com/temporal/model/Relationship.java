package com.temporal.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Relationship {
	private String name;
	private String type;
	private String from;
	private String to;

	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@XmlElement
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	@XmlElement
	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	@Override
	public String toString() {
		return "\t" + this.type + "\t" + this.from + "\t" + this.to;
	}
}
