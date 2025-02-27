package com.endava.example.exceptions;

/**
 * ResourceAlreadyExistsException is a custom exception class used for handling
 * exceptions caused if some resource is already there in the db..
 */
public class ResourceAlreadyExistsException extends RuntimeException {
	public ResourceAlreadyExistsException(String message) {
		super(message);
	}

}
