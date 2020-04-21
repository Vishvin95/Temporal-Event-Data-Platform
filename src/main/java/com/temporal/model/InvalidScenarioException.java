package com.temporal.model;

public class InvalidScenarioException extends Exception{

	private static final long serialVersionUID = 1L;
	private String message;
	
	public InvalidScenarioException(String message) {
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return message + "\n" + super.getMessage();
	}
}
