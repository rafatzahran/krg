package com.example.krg.exception;

/**
 * @author David Carradine
 *
 */
public class BadRequestException extends RuntimeException {


	private static final long serialVersionUID = 7216299408971049939L;

	public BadRequestException(String message) {
		super(message);
	}

}
