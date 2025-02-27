package com.endava.example.dto;

import lombok.Data;

/**
 * UserRegistrationDTO containing the required details for registration of the
 * user.. includes fullName , age , email and password of the user..
 */
@Data
public class UserRegistrationDTO {

	private String fullName;
	private int age;
	private String email;
	private String password;
}
