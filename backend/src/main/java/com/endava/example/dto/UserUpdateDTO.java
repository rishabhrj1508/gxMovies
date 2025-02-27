package com.endava.example.dto;

import lombok.Data;

/**
 * UserUpdateDTO contains details for updating a user's personal information,
 * such as their fullName, age, and email.
 */
@Data
public class UserUpdateDTO {

	private String fullName;
	private int age;
	private String email;

}
