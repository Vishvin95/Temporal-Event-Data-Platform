package com.temporal.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Domain {
	private String name;
	private boolean temporal;
	private ArrayList<Event> events;
	private String primaryKey;

	public Domain() {

	}

	private Domain(DomainBuilder domainBuilder) {
		this.name = domainBuilder.name;
		this.events = domainBuilder.events;
		this.primaryKey = domainBuilder.primaryKey;
	}

	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name = "temporal")
	public boolean isTemporal() {
		return temporal;
	}

	public void setTemporal(boolean temporal) {
		this.temporal = temporal;
	}

	@XmlElement(name = "event")
	public ArrayList<Event> getEvents() {
		return events;
	}

	public void setEvents(ArrayList<Event> events) {
		this.events = events;
	}
	
	@XmlElement(name = "primaryKey")
	public String getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	@Override
	public String toString() {
		String res = " " + name + "\n Temporal: "+ temporal +"\n";
		for(Event event: events)
		{
			res = res + " " + event.getName() + "\t" + event.getDataType() + "\t";
			if(event.isNotNull())
				res = res + "not_null\t";
			if(event.isUnique())
				res = res + "unique";
			res = res + "\n";
		}
		res = res + " Primary Key: " + primaryKey + "\n";
		return res;
	}

	public static class DomainBuilder {
		private final String name;
		private final boolean temporal;
		private ArrayList<Event> events;
		private final String primaryKey;

		public DomainBuilder(String name, boolean temporal, String primaryKey) {
			this.name = name;
			this.temporal = temporal;
			this.events = new ArrayList<Event>();
			this.primaryKey = primaryKey;
		}

		public DomainBuilder addEvent(Event event) {
			events.add(event);
			return this;
		}

		public Domain build() {
			Domain domain = new Domain(this);
			validateDomain(domain);
			return domain;
		}

		private boolean validateDomain(Domain domain) {
			if (domain.events.size() < 1)
				return false;
			else
				return true;
		}
	}
}
