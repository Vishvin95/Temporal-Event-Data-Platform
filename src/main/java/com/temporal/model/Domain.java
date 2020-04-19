package com.temporal.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Domain {
	private String name;
	private ArrayList<Event> events;

	public Domain() {

	}

	private Domain(DomainBuilder domainBuilder) {
		this.name = domainBuilder.name;
		this.events = domainBuilder.events;
	}

	@XmlAttribute(name = "name")
	public String getname() {
		return name;
	}

	public void setname(String name) {
		this.name = name;
	}

	@XmlElement(name = "event")
	public ArrayList<Event> getEvents() {
		return events;
	}

	public void setEvents(ArrayList<Event> events) {
		this.events = events;
	}

	public static class DomainBuilder {
		private final String name;
		private ArrayList<Event> events;

		public DomainBuilder(String name) {
			this.name = name;
			this.events = new ArrayList<Event>();
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
