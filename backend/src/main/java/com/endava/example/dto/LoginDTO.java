package com.endava.example.dto;

import lombok.Data;

/**
 * LoginDTO holds information about user credentials, including user's email and
 * user's password that will be used to login into the system.
 */
@Data
public class LoginDTO {

	private String email;
	private String password;
}
