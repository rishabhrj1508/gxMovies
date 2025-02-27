package com.endava.example.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.endava.example.dto.LoginDTO;
import com.endava.example.dto.UserDTO;
import com.endava.example.dto.UserRegistrationDTO;
import com.endava.example.dto.UserUpdateDTO;
import com.endava.example.exceptions.InvalidCredentialException;
import com.endava.example.exceptions.ResourceAlreadyExistsException;
import com.endava.example.exceptions.ResourceNotFoundException;
import com.endava.example.service.UserService;
import com.endava.example.utils.JwtAuthenticationFilter;
import com.endava.example.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private UserService userService;

	@MockitoBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@MockitoBean
	private JwtUtils jwtUtils;

	@Test
	void testSendOtpForRegistration_Success() throws Exception {
		
		String email = "rishabh@gmail.com";
		doNothing().when(userService).sendOtpForRegistration(email);
		
		mockMvc.perform(post("/api/users/auth/send-registration-otp").param("email", email)).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("OTP sent successfully."));
		
		verify(userService, times(1)).sendOtpForRegistration(email);
		
	}

	@Test
	void testSendOtpForRegistration_EmailAlreadyExists() throws Exception {
		String email = "rishabhrj1508@gmail.com";
		doThrow(new ResourceAlreadyExistsException("Email is already registered")).when(userService)
				.sendOtpForRegistration(email);
		mockMvc.perform(post("/api/users/auth/send-registration-otp").param("email", email))
				.andExpect(status().isConflict()).andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("Email is already registered"));
		verify(userService, times(1)).sendOtpForRegistration(email);
	}

	@Test
	void testValidateOtpAndRegister_Success() throws Exception {

		UserRegistrationDTO registrationDTO = new UserRegistrationDTO();
		registrationDTO.setFullName("Rishabh Jain");
		registrationDTO.setAge(24);
		registrationDTO.setEmail("rishabh@gmail.com");
		registrationDTO.setPassword("password");

		String otp = "123456";

		UserDTO userDTO = new UserDTO();
		userDTO.setUserId(1);
		userDTO.setFullName("Rishabh");
		userDTO.setAge(24);
		userDTO.setEmail("rishabh@gmail.com");

		when(userService.validateOtpAndRegister(any(UserRegistrationDTO.class), eq(otp))).thenReturn(userDTO);

		mockMvc.perform(post("/api/users/auth/validate-registration").param("otp", otp)
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registrationDTO)))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.email").value("rishabh@gmail.com"));

		verify(userService, times(1)).validateOtpAndRegister(registrationDTO, otp);

	}

	@Test
	void testValidateOtpAndRegister_InvalidOtp() throws Exception {
		UserRegistrationDTO registrationDTO = new UserRegistrationDTO();
		registrationDTO.setFullName("Rishabh");
		registrationDTO.setAge(24);
		registrationDTO.setEmail("rishabh@example.com");
		registrationDTO.setPassword("password");
		String otp = "123456";

		when(userService.validateOtpAndRegister(any(UserRegistrationDTO.class), eq(otp)))
				.thenThrow(new InvalidCredentialException("Invalid or expired OTP."));

		mockMvc.perform(post("/api/users/auth/validate-registration").param("otp", otp)
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registrationDTO)))
				.andExpect(status().isUnauthorized()).andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("Invalid or expired OTP."));

		verify(userService, times(1)).validateOtpAndRegister(registrationDTO, otp);

	}

	@Test
	void testLoginUser_Success() throws Exception {

		LoginDTO loginDTO = new LoginDTO();
		loginDTO.setEmail("rishabhrj1508@gmail.com");
		loginDTO.setPassword("rishabhrj1508");

		UserDTO userDTO = new UserDTO();
		userDTO.setUserId(1);
		userDTO.setFullName("Rishabh Jain");
		userDTO.setAge(24);
		userDTO.setEmail("rishabh@gmail.com");
		userDTO.setRole("USER");

		String token = "my-jwt-token";

		when(userService.loginUser(any(LoginDTO.class))).thenReturn(userDTO);
		when(jwtUtils.generateToken(userDTO.getUserId(), userDTO.getRole())).thenReturn(token);

		mockMvc.perform(post("/api/users/auth/user-login").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginDTO))).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Login successful.")).andExpect(jsonPath("$.data").value(token));

		verify(userService, times(1)).loginUser(any(LoginDTO.class));
		verify(jwtUtils, times(1)).generateToken(userDTO.getUserId(), userDTO.getRole());
	}

	@Test
	void testLoginUser_InvalidCredentials() throws Exception {
		LoginDTO loginDTO = new LoginDTO();
		loginDTO.setEmail("rishabh@gmail.com");
		loginDTO.setPassword("wrongpassword");

		when(userService.loginUser(any(LoginDTO.class)))
				.thenThrow(new InvalidCredentialException("Invalid email or password."));

		mockMvc.perform(post("/api/users/auth/user-login").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginDTO))).andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("Invalid email or password."));

		verify(userService, times(1)).loginUser(any(LoginDTO.class));
		verify(jwtUtils, times(0)).generateToken(anyInt(), anyString());
	}

	@Test
	void testLoginAdmin_Success() throws Exception {

		LoginDTO loginDTO = new LoginDTO();
		loginDTO.setEmail("admin@example.com");
		loginDTO.setPassword("adminpassword");

		UserDTO adminDTO = new UserDTO();
		adminDTO.setUserId(1);
		adminDTO.setFullName("Admin User");
		adminDTO.setAge(30);
		adminDTO.setEmail("admin@example.com");
		adminDTO.setRole("ADMIN");

		String token = "mocked-jwt-token";

		when(userService.loginAdmin(any(LoginDTO.class))).thenReturn(adminDTO);
		when(jwtUtils.generateToken(adminDTO.getUserId(), adminDTO.getRole())).thenReturn(token);

		mockMvc.perform(post("/api/users/auth/admin-login").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginDTO))).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Admin login successful."))
				.andExpect(jsonPath("$.data").value(token));

		verify(userService, times(1)).loginAdmin(any(LoginDTO.class));
		verify(jwtUtils, times(1)).generateToken(adminDTO.getUserId(), adminDTO.getRole());
	}

	@Test
	void testLoginAdmin_InvalidCredentials() throws Exception {

		LoginDTO loginDTO = new LoginDTO();
		loginDTO.setEmail("admin@example.com");
		loginDTO.setPassword("wrongpassword");

		when(userService.loginAdmin(any(LoginDTO.class)))
				.thenThrow(new InvalidCredentialException("Invalid email or password"));

		mockMvc.perform(post("/api/users/auth/admin-login").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginDTO))).andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("Invalid email or password"));

		verify(userService, times(1)).loginAdmin(any(LoginDTO.class));
		verify(jwtUtils, times(0)).generateToken(anyInt(), anyString());
	}

	@Test
	void testGetAllUsers_Success() throws Exception {
		UserDTO user1 = new UserDTO();
		user1.setUserId(1);
		user1.setFullName("Rishabh");
		user1.setAge(24);
		user1.setEmail("rishabh@gmail.com");
		user1.setRole("USER");

		UserDTO user2 = new UserDTO();
		user2.setUserId(2);
		user2.setFullName("Satyam");
		user2.setAge(30);
		user2.setEmail("satyam@example.com");
		user2.setRole("USER");

		List<UserDTO> users = Arrays.asList(user1, user2);

		when(userService.getAllUsers()).thenReturn(users);

		mockMvc.perform(get("/api/users").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Users fetched successfully."))
				.andExpect(jsonPath("$.data[0].userId").value(1))
				.andExpect(jsonPath("$.data[0].fullName").value("Rishabh"))
				.andExpect(jsonPath("$.data[1].userId").value(2))
				.andExpect(jsonPath("$.data[1].fullName").value("Satyam"));

		verify(userService, times(1)).getAllUsers();
	}

	@Test
	void testGetAllUsers_EmptyList() throws Exception {
		when(userService.getAllUsers()).thenReturn(Collections.emptyList());

		mockMvc.perform(get("/api/users").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Users fetched successfully."))
				.andExpect(jsonPath("$.data").isEmpty());

		verify(userService, times(1)).getAllUsers();
	}

	@Test
	void testGetUserById_Success() throws Exception {

		int userId = 1;
		UserDTO user = new UserDTO();
		user.setUserId(userId);
		user.setFullName("Rishabh Jain");
		user.setAge(24);
		user.setEmail("rishabh@gmail.com");
		user.setRole("USER");

		when(userService.getUserById(userId)).thenReturn(user);

		mockMvc.perform(get("/api/users/{userId}", userId).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("User details fetched successfully."))
				.andExpect(jsonPath("$.data.userId").value(userId))
				.andExpect(jsonPath("$.data.fullName").value("Rishabh Jain"))
				.andExpect(jsonPath("$.data.email").value("rishabh@gmail.com"));

		verify(userService, times(1)).getUserById(userId);
	}

	@Test
	void testGetUserById_UserNotFound() throws Exception {

		int userId = 99;
		when(userService.getUserById(userId))
				.thenThrow(new ResourceNotFoundException("User not found with ID: " + userId));

		mockMvc.perform(get("/api/users/{userId}", userId).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("User not found with ID: " + userId));

		verify(userService, times(1)).getUserById(userId);
	}

	@Test
	void testUpdateUser_Success() throws Exception {
		int userId = 1;

		UserUpdateDTO updateDTO = new UserUpdateDTO();
		updateDTO.setFullName("Rishabh");
		updateDTO.setAge(25);
		updateDTO.setEmail("rishabh@example.com");

		UserDTO updatedUser = new UserDTO();
		updatedUser.setUserId(userId);
		updatedUser.setFullName("Rishabh");
		updatedUser.setAge(25);
		updatedUser.setEmail("rishabh@example.com");
		updatedUser.setRole("USER");

		when(userService.updateUser(eq(userId), any(UserUpdateDTO.class))).thenReturn(updatedUser);

		mockMvc.perform(put("/api/users/{userId}", userId).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDTO))).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("User updated successfully."))
				.andExpect(jsonPath("$.data.userId").value(userId))
				.andExpect(jsonPath("$.data.fullName").value("Rishabh"))
				.andExpect(jsonPath("$.data.email").value("rishabh@example.com"));

		verify(userService, times(1)).updateUser(eq(userId), any(UserUpdateDTO.class));
	}

	@Test
	void testUpdateUser_UserNotFound() throws Exception {
		int userId = 99;
		UserUpdateDTO updateDTO = new UserUpdateDTO();
		updateDTO.setFullName("Not present");
		updateDTO.setAge(30);
		updateDTO.setEmail("notPresent@example.com");

		when(userService.updateUser(eq(userId), any(UserUpdateDTO.class)))
				.thenThrow(new ResourceNotFoundException("User not found with ID: " + userId));

		mockMvc.perform(put("/api/users/{userId}", userId).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDTO))).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("User not found with ID: " + userId));

		verify(userService, times(1)).updateUser(eq(userId), any(UserUpdateDTO.class));
	}

	@Test
	void testBlockUser_Success() throws Exception {
		int userId = 1;
		doNothing().when(userService).blockUser(userId);

		mockMvc.perform(patch("/api/users/block/{userId}", userId)).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("User blocked successfully."));

		verify(userService, times(1)).blockUser(userId);
	}

	@Test
	void testBlockUser_UserNotFound() throws Exception {
		int userId = 99;
		doThrow(new ResourceNotFoundException("User not found with ID: " + userId)).when(userService).blockUser(userId);

		mockMvc.perform(patch("/api/users/block/{userId}", userId)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("User not found with ID: " + userId));

		verify(userService, times(1)).blockUser(userId);
	}

	@Test
	void testUnblockUser_Success() throws Exception {
		int userId = 1;
		doNothing().when(userService).unBlockUser(userId);

		mockMvc.perform(patch("/api/users/unblock/{userId}", userId)).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("User unblocked successfully."));

		verify(userService, times(1)).unBlockUser(userId);
	}

	@Test
	void testUnblockUser_UserNotFound() throws Exception {
		int userId = 99;
		doThrow(new ResourceNotFoundException("User not found with ID: " + userId)).when(userService)
				.unBlockUser(userId);

		mockMvc.perform(patch("/api/users/unblock/{userId}", userId)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("User not found with ID: " + userId));

		verify(userService, times(1)).unBlockUser(userId);
	}

}
