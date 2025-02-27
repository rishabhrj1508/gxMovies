package com.endava.example.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.endava.example.dto.LoginDTO;
import com.endava.example.dto.UserDTO;
import com.endava.example.dto.UserRegistrationDTO;
import com.endava.example.dto.UserUpdateDTO;
import com.endava.example.entity.User;
import com.endava.example.exceptions.InvalidCredentialException;
import com.endava.example.exceptions.ResourceAlreadyExistsException;
import com.endava.example.exceptions.ResourceNotFoundException;
import com.endava.example.mapper.UserMapper;
import com.endava.example.repository.UserRepository;
import com.endava.example.service.UserService;
import com.endava.example.utils.EmailService;
import com.endava.example.utils.OtpGenerator;

/**
 * Implementation of the UserService interface to manage user-related
 * operations. This includes user registration, login, blocking and unblocking
 * the users, OTP generation for registration, and user profile updates.
 */
@Service
public class UserServiceImpl implements UserService {

	// Injecting required dependencies
	private UserRepository userRepository;

	private UserMapper userMapper;

	private EmailService emailService;

	private PasswordEncoder passwordEncoder;

	public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, EmailService emailService,
			PasswordEncoder passwordEncoder) {
		super();
		this.userRepository = userRepository;
		this.userMapper = userMapper;
		this.emailService = emailService;
		this.passwordEncoder = passwordEncoder;
	}

	// Temporary storage for OTP during registration
	private final ConcurrentHashMap<String, String> otpStorage = new ConcurrentHashMap<>();

	public ConcurrentHashMap<String, String> getOtpStorage() {
		return otpStorage;
	}

	/**
	 * Sends an OTP to the provided email for registration purposes.
	 * 
	 * @param email the email address to send the OTP to.
	 * @throws ResourceAlreadyExistsException if the email is already associated
	 *                                        with an existing user.
	 */
	@Override
	public void sendOtpForRegistration(String email) {

		if (email == null || email.isEmpty()) {
			throw new IllegalArgumentException("Email cannot be null or empty");
		}

		if (userRepository.findByEmail(email).isPresent()) {
			throw new ResourceAlreadyExistsException("User with this email already exists.");
		}

		String otp = OtpGenerator.generateOTP();
		emailService.sendEmail(email, "Registration OTP", "Your OTP for registration is: " + otp);

		otpStorage.put(email, otp);
	}

	/**
	 * Validates the OTP and registers a new user.
	 * 
	 * @param dto containing user registration details.
	 * @param otp the OTP provided by the user.
	 * @return UserDTO containing the registered user details.
	 * @throws InvalidCredentialException     if the OTP is invalid or expired.
	 */
	@Override
	public UserDTO validateOtpAndRegister(UserRegistrationDTO dto, String otp) {

		if (dto == null || dto.getEmail() == null || dto.getPassword() == null || dto.getFullName() == null
				|| dto.getAge() == 0) {
			throw new IllegalArgumentException("Email , Password , FullName or Age cannot be null");
		}

		String storedOtp = otpStorage.get(dto.getEmail());

		if (storedOtp == null || !storedOtp.equals(otp)) {
			throw new InvalidCredentialException("Invalid or expired OTP.");
		}

		otpStorage.remove(dto.getEmail());

		User user = userMapper.toEntity(dto);
		user.setPassword(passwordEncoder.encode(dto.getPassword()));
		userRepository.save(user);

		return userMapper.toDto(user);
	}

	/**
	 * Logs in as a regular user with the provided credentials.
	 * 
	 * @param dto containing email and password.
	 * @return UserDTO containing logged-in user details.
	 * @throws InvalidCredentialException if the credentials are invalid or the user
	 *                                    is blocked.
	 * @throws ResourceNotFoundException  if the user is not found.
	 */
	@Override
	public UserDTO loginUser(LoginDTO dto) {

		if (dto == null || dto.getEmail() == null || dto.getPassword() == null) {
			throw new IllegalArgumentException("Email or password cannot be null");
		}

		User user = userRepository.findByEmail(dto.getEmail())
				.orElseThrow(() -> new ResourceNotFoundException("User with this email not found."));

		if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
			throw new InvalidCredentialException("Invalid email or password.");
		}

		if ("BLOCKED".equals(user.getStatus())) {
			throw new InvalidCredentialException("Your account is blocked. Please contact admin.");
		}

		if (!"USER".equals(user.getRole())) {
			throw new InvalidCredentialException("This login is only for users.");
		}

		return userMapper.toDto(user);
	}

	/**
	 * Logs in as an admin with the provided credentials.
	 * 
	 * @param dto containing email and password.
	 * @return UserDTO containing logged-in admin details.
	 * @throws IllegalArgumentException   if the LoginDTO is null or credentials are
	 *                                    invalid.
	 * @throws InvalidCredentialException if the credentials are invalid or the user
	 *                                    is not an admin.
	 */
	@Override
	public UserDTO loginAdmin(LoginDTO dto) {

		if (dto == null || dto.getEmail() == null || dto.getPassword() == null) {
			throw new IllegalArgumentException("Email or password cannot be null.");
		}

		User admin = userRepository.findByEmail(dto.getEmail())
				.orElseThrow(() -> new InvalidCredentialException("Email is not registered yet"));

		if (!passwordEncoder.matches(dto.getPassword(), admin.getPassword())) {
			throw new InvalidCredentialException("Invalid email or password.");
		}

		if (!"ADMIN".equals(admin.getRole())) {
			throw new InvalidCredentialException("This login is only for admins.");
		}

		return userMapper.toDto(admin);
	}

	/**
	 * Retrieves the details of a user by their ID.
	 * 
	 * @param userId the ID of the user.
	 * @return UserDTO containing the user's details.
	 * @throws ResourceNotFoundException if no user is found with the provided ID.
	 */
	@Override
	public UserDTO getUserById(int userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id : " + userId));
		return userMapper.toDto(user);
	}

	/**
	 * Retrieves a list of all users with the role "USER".
	 * 
	 * @return a list of UserDTOs containing details of all users.
	 */
	@Override
	public List<UserDTO> getAllUsers() {
		return userRepository.findAll().stream().filter(user -> "USER".equals(user.getRole())).map(userMapper::toDto)
				.toList();
	}

	/**
	 * Updates the user's details.
	 * 
	 * @param userId the ID of the user to be updated.
	 * @param dto    the details to update.
	 * @return UserDTO containing the updated user details.
	 * @throws ResourceNotFoundException      if the user with the given ID is not
	 *                                        found.
	 * @throws IllegalArgumentException       if the email is invalid.
	 * @throws ResourceAlreadyExistsException if the email is already in use by
	 *                                        another user.
	 */
	@Override
	public UserDTO updateUser(int userId, UserUpdateDTO dto) {

		if (dto == null) {
			throw new IllegalArgumentException("DTO cannot be null");
		}

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

		if (dto.getFullName() != null) {
			user.setFullName(dto.getFullName());
		}

		if (dto.getAge() > 0) {
			user.setAge(dto.getAge());
		}

		String newEmail = dto.getEmail();
		if (newEmail != null && !newEmail.isEmpty() && !user.getEmail().equalsIgnoreCase(newEmail)) {
			userRepository.findByEmail(newEmail).ifPresent(existingUser -> {
				throw new ResourceAlreadyExistsException("User with this email already exists.");
			});
			user.setEmail(newEmail);
		}

		user.setUpdatedAt(LocalDate.now());

		userRepository.save(user);

		return userMapper.toDto(user);
	}

	/**
	 * Blocks a user by their ID.
	 * 
	 * @param userId the ID of the user to block.
	 * @throws ResourceNotFoundException if the user is not found.
	 */
	@Override
	public void blockUser(int userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

		user.setStatus("BLOCKED");
		userRepository.save(user);
	}

	/**
	 * Unblocks a user by their ID.
	 * 
	 * @param userId the ID of the user to unblock.
	 * @throws ResourceNotFoundException if the user is not found.
	 */
	@Override
	public void unBlockUser(int userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

		user.setStatus("ACTIVE");
		userRepository.save(user);
	}

}
