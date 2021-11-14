package com.example.krg.exception;

public class ValidationException extends RuntimeException {

	private static final long serialVersionUID = -5943125300038357320L;
	
	public ValidationException(String message) {
		super(message);
	}

}
