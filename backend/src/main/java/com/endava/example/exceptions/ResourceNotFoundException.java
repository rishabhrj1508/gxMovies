package com.endava.example.exceptions;

/**
 * ResourceNotFoundException is a custom exception class used for handling the
 * exceptions caused if the resource requested is not their in the db..
 */
public class ResourceNotFoundException extends RuntimeException {
	public ResourceNotFoundException(String message) {
		super(message);
	}
}
