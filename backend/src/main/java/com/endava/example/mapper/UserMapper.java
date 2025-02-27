package com.endava.example.mapper;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.endava.example.dto.UserDTO;
import com.endava.example.dto.UserRegistrationDTO;
import com.endava.example.entity.User;

@Component
public class UserMapper {

	public User toEntity(UserRegistrationDTO dto) {
		User user = new User();
		user.setAge(dto.getAge());
		user.setFullName(dto.getFullName());
		user.setEmail(dto.getEmail());
		user.setPassword(dto.getPassword());
		user.setRole("USER"); // by default user
		user.setStatus("ACTIVE");// by default active
		user.setCreatedAt(LocalDate.now());
		user.setUpdatedAt(LocalDate.now());
		return user;
	}

	public UserDTO toDto(User user) {
		UserDTO dto = new UserDTO();
		dto.setUserId(user.getUserId());
		dto.setFullName(user.getFullName());
		dto.setAge(user.getAge());
		dto.setEmail(user.getEmail());
		dto.setRole(user.getRole());
		dto.setStatus(user.getStatus());
		dto.setCreatedAt(user.getCreatedAt());
		dto.setUpdatedAt(user.getUpdatedAt());
		return dto;

	}

}
