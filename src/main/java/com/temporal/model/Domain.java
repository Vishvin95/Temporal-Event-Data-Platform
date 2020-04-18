package com.temporal.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Domain {
	private String name;
	private ArrayList<Event> events;
	
	@XmlAttribute(name="name")
	public String getname() {
		return name;
	}
	public void setname(String name) {
		this.name = name;
	}
	
	@XmlElement(name="event")
	public ArrayList<Event> getEvents() {
		return events;
	}
	public void setEvents(ArrayList<Event> events) {
		this.events = events;
	}
}
