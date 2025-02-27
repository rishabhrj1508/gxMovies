package com.endava.example.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.endava.example.dto.LoginDTO;
import com.endava.example.dto.UserDTO;
import com.endava.example.dto.UserRegistrationDTO;
import com.endava.example.dto.UserUpdateDTO;
import com.endava.example.service.UserService;
import com.endava.example.utils.GenericResponse;
import com.endava.example.utils.JwtUtils;

/**
 * UserController handles all requests related to users.. such as registration ,
 * login , block , unblock , fetching ..
 */
@RestController
@RequestMapping("api/users")
public class UserController {

	// Injecting the required service and utility class..
	private UserService userService;
	private JwtUtils jwtUtil;

	public UserController(UserService userService, JwtUtils jwtUtil) {
		super();
		this.userService = userService;
		this.jwtUtil = jwtUtil;
	}

	/**
	 * send OTP for user registration. This is the first step in the registration
	 * process, where the user provides their email to receive an OTP for
	 * validation.
	 * 
	 * @param email the email address to send the OTP to.
	 * @return ApiResponse indicating that the OTP was sent successfully..
	 */
	@PostMapping("/auth/send-registration-otp")
	public ResponseEntity<GenericResponse<String>> sendOtpForRegistration(@RequestParam String email) {
		userService.sendOtpForRegistration(email);
		return ResponseEntity.ok(new GenericResponse<>(true, "OTP sent successfully.", null));
	}

	/**
	 * validates the OTP and register the user.. After receiving the OTP, the user
	 * provides their details for registration. validates the OTP and proceeds with
	 * the registration process.
	 * 
	 * @param dto user registration details.
	 * @param otp to validate the registration.
	 * @return ApiResponse with the registered user's details as UserDTO..
	 */
	@PostMapping("/auth/validate-registration")
	public ResponseEntity<GenericResponse<UserDTO>> validateOtpAndRegister(@RequestBody UserRegistrationDTO dto,
			@RequestParam String otp) {
		UserDTO registeredUser = userService.validateOtpAndRegister(dto, otp);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new GenericResponse<>(true, "User registered successfully.", registeredUser));
	}

	/**
	 * for regular users to log in. After successful login, a JWT token is generated
	 * for the user, which can be used for authentication on subsequent requests.
	 * 
	 * @param dto the login details (email and password).
	 * @return ApiResponse with a success message and the generated JWT token.
	 */
	@PostMapping("/auth/user-login")
	public ResponseEntity<GenericResponse<String>> loginUser(@RequestBody LoginDTO dto) {
		UserDTO user = userService.loginUser(dto);
		String token = jwtUtil.generateToken(user.getUserId(),user.getRole());
		return ResponseEntity.ok(new GenericResponse<>(true, "Login successful.", token));
	}

	/**
	 * for admins to log in. admins have a separate login process and can receive a
	 * JWT token for accessing admin-level resources.
	 * 
	 * @param loginDTO the login details (email and password).
	 * @return ApiResponse with a success message and the generated JWT token..
	 */
	@PostMapping("/auth/admin-login")
	public ResponseEntity<GenericResponse<String>> loginAdmin(@RequestBody LoginDTO loginDTO) {
		UserDTO admin = userService.loginAdmin(loginDTO);
		String token = jwtUtil.generateToken(admin.getUserId(),admin.getRole());
		return ResponseEntity.ok(new GenericResponse<>(true, "Admin login successful.", token));
	}

	/**
	 * fetch all users in the system.
	 * 
	 * @return ApiResponse containing a list of all users.
	 */
	@GetMapping
	public ResponseEntity<GenericResponse<List<UserDTO>>> getAllUsers() {
		List<UserDTO> users = userService.getAllUsers();
		return ResponseEntity.ok(new GenericResponse<>(true, "Users fetched successfully.", users));
	}

	/**
	 * fetch a specific user by their ID..
	 * 
	 * @param userId the ID of the user to fetch..
	 * @return ApiResponse containing the user's details..
	 */
	@GetMapping("/{userId}")
	public ResponseEntity<GenericResponse<UserDTO>> getUserById(@PathVariable int userId) {
		UserDTO user = userService.getUserById(userId);
		return ResponseEntity.ok(new GenericResponse<>(true, "User details fetched successfully.", user));
	}

	/**
	 * update an existing user's details..
	 * 
	 * @param userId the ID of the user to update..
	 * @param dto    the updated user information..
	 * @return ApiResponse containing the updated user details..
	 */
	@PutMapping("/{userId}")
	public ResponseEntity<GenericResponse<UserDTO>> updateUser(@PathVariable int userId,
			@RequestBody UserUpdateDTO dto) {
		UserDTO updatedUser = userService.updateUser(userId, dto);
		return ResponseEntity.ok(new GenericResponse<>(true, "User updated successfully.", updatedUser));
	}

	/**
	 * blocks a user.
	 * 
	 * @param userId the id of the user to block..
	 * @return ApiResponse indicating that the user was blocked successfully..
	 */
	@PatchMapping("/block/{userId}")
	public ResponseEntity<GenericResponse<String>> blockUser(@PathVariable int userId) {
		userService.blockUser(userId);
		return ResponseEntity.ok(new GenericResponse<>(true, "User blocked successfully.", null));
	}

	/**
	 * unblocks a user.
	 * 
	 * @param userId the id of the user to unblock..
	 * @return ApiResponse indicating that the user was unblocked successfully...
	 */
	@PatchMapping("/unblock/{userId}")
	public ResponseEntity<GenericResponse<String>> unblockUser(@PathVariable int userId) {
		userService.unBlockUser(userId);
		return ResponseEntity.ok(new GenericResponse<>(true, "User unblocked successfully.", null));
	}

}
