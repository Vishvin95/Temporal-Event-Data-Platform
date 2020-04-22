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
	 *             RelationshipType.ONE_TO_MANY, RelationshipType.MANY_TO_ONE,
	 *             RelationshipType.MANY_TO_MANY
	 * @param from : Domain on left side of relationship
	 * @param to   : Domain on right side of relationship
	 */
	public Relationship(String name, RelationshipType type, Domain from, Domain to) {
		this.name = name;
		this.type = type.getRelationshipType();
		this.from = from.getname();
		this.to = to.getname();
	}
	
	Relationship(String name, RelationshipType type, String from, String to) {
		this.name = name;
		this.type = type.getRelationshipType();
		this.from = from;
		this.to = to;
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
		return this.name + "\t" + this.type + "\t" + this.from + "\t" + this.to;
	}
	
	@Override
	public boolean equals(Object obj) {
		Relationship r = (Relationship) obj;

		// Same name, same content
		// Different name, same content
		if (
				// A B & A B & relationship also same
				(r.getFrom().equals(this.from) && r.getTo().equals(this.to) && r.getType().equals(this.type))

				// 11 or nn relationship, with from and to same, A B 11 and B A 11  , A B nn and B A nn
				|| ((r.getType().equals(this.type)
						&& (r.getType().equals(RelationshipType.ONE_TO_ONE.getRelationshipType())
								|| r.getType().equals(RelationshipType.MANY_TO_MANY.getRelationshipType()))
						&& r.getFrom().equals(this.to)) && r.getTo().equals(this.from))
			)
			return true;
		else
			return false;
	}
	
	@Override
	public int hashCode() {
		return 0;
	}
}
