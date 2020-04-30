package com.temporal.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;


public class RawReadings {
	private String name;
	private ArrayList<RawReading> rawReadings;
	
	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement(name = "reading")
	public ArrayList<RawReading> getRawReadings() {
		return rawReadings;
	}
	public void setRawReadings(ArrayList<RawReading> rawReadings) {
		this.rawReadings = rawReadings;
	}
}
