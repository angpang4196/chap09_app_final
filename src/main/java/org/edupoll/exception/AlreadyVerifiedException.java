package org.edupoll.exception;

public class AlreadyVerifiedException extends Exception {

	private static final long serialVersionUID = 1L;

	public AlreadyVerifiedException(String message) {
		super(message);
	}

}
