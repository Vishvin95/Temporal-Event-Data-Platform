package com.temporal.model;

public enum RelationshipType {
	ONE_TO_ONE("11"), ONE_TO_MANY("1n"), MANY_TO_ONE("n1"), MANY_TO_MANY("nn");
	
	private String value;
	private RelationshipType(String value) {
		this.value = value;
	}
	
	String getRelationshipType()
	{
		return value;
	}
}
