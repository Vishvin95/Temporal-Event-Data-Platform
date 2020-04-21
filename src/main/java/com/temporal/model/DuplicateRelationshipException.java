package com.temporal.model;

public class DuplicateRelationshipException extends Exception {
	private static final long serialVersionUID = 1L;
	private String message;
	
	public DuplicateRelationshipException(String message) {
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return message + "\n" + super.getMessage();
	}
}
