package com.endava.example.service;

import java.util.List;

import com.endava.example.dto.LoginDTO;
import com.endava.example.dto.UserDTO;
import com.endava.example.dto.UserRegistrationDTO;
import com.endava.example.dto.UserUpdateDTO;

public interface UserService {

	UserDTO loginAdmin(LoginDTO dto);

	void sendOtpForRegistration(String email);

	UserDTO validateOtpAndRegister(UserRegistrationDTO dto, String otp);

	UserDTO loginUser(LoginDTO dto);

	UserDTO getUserById(int userId);

	UserDTO updateUser(int userId, UserUpdateDTO dto);

	void blockUser(int userId);

	void unBlockUser(int userId);

	List<UserDTO> getAllUsers();
}
