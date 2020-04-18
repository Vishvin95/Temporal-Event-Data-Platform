package com.temporal.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Relationship {
	private String name;
	private String type;
	private String from;
	private String to;

	public Relationship() {

	}

	/**
	 * @param name : Name of the relationship
	 * @param type : Either of four, RelationshipType.ONE_TO_ONE,
	 *               RelationshipType.ONE_TO_MANY, RelationshipType.MANY_TO_ONE,
	 *               RelationshipType.MANY_TO_MANY
	 * @param from : Domain on left side of relationship
	 * @param to   : Domain on right side of relationship
	 */
	public Relationship(String name, RelationshipType type, Domain from, Domain to) {
		this.name = name;
		this.type = type.getRelationshipType();
		this.from = from.getname();
		this.to = to.getname();
	}

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
