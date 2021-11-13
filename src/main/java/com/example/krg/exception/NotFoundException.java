package com.example.krg.exception;

/**
 * @author David Carradine
 *
 */
public class NotFoundException extends RuntimeException {


	private static final long serialVersionUID = 7216299408971049939L;
	
	public NotFoundException(String message) {
		super(message);
	}

}
