package com.endava.example.dto;

import java.time.LocalDate;

import lombok.Data;

/**
 * UserDTO containing details of a user, including personal information - name ,
 * age , email ,role , account status, and date for creation and updates.
 */
@Data
public class UserDTO {
	private int userId;
	private String fullName;
	private int age;
	private String email;
	private String role;
	private String status;
	private LocalDate createdAt;
	private LocalDate updatedAt;
}
