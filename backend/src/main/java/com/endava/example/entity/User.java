package com.endava.example.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * User Entity class represents a user in the system. This class maps to the
 * 'users' table in the database. The user entity contains information related
 * to the user's, such as name, age, email, password, role, and account status.
 * It also includes fields like 'createdAt' and 'updatedAt'.
 */

@Entity
@Data
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int userId;

	@Column(nullable = false)
	private String fullName;

	@Column(nullable = false)
	private int age;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String role = "USER"; // ADMIN OR USER

	@Column(nullable = false)
	private String status = "ACTIVE"; // ACTIVE(Default) OR BLOCKED

	@Column(nullable = false)
	private LocalDate createdAt;

	@Column
	private LocalDate updatedAt;


}
