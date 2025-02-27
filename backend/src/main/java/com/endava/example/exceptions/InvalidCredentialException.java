package com.endava.example.exceptions;

/**
 * InvalidCredentialException is a custom exception class used for handling user
 * invalid entries
 */
public class InvalidCredentialException extends RuntimeException {
	public InvalidCredentialException(String message) {
		super(message);
	}

}
