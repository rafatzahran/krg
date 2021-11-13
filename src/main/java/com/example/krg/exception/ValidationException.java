package com.example.krg.exception;

/**
 * @author Jean-Claude Van Damme
 *
 */

public class ValidationException extends RuntimeException {

	private static final long serialVersionUID = -5943125300038357320L;
	
	public ValidationException(String message) {
		super(message);
	}

}
