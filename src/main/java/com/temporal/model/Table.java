package com.temporal.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;


public class Table {
	private String name;
	private ArrayList<Column> columns;
	
	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElementWrapper(name = "row")
	@XmlElement(name = "column")
	public ArrayList<Column> getRawReadings() {
		return columns;
	}
	public void setRawReadings(ArrayList<Column> columns) {
		this.columns = columns;
	}
}
