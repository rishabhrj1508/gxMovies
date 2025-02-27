package com.endava.example.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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
import com.endava.example.utils.EmailService;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserMapper userMapper;

	@Mock
	private EmailService emailService;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserServiceImpl userService;

	private ConcurrentHashMap<String, String> otpStorage;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		otpStorage = userService.getOtpStorage();
		otpStorage.clear();
	}

	@Test
	void testSendOtpForRegistration_Positive() {
		String email = "test@example.com";

		when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
		doNothing().when(emailService).sendEmail(eq(email), anyString(), anyString());

		userService.sendOtpForRegistration(email);

		verify(emailService, times(1)).sendEmail(eq(email), eq("Registration OTP"),
				contains("Your OTP for registration is: "));
	}

	@Test
	void testSendOtpForRegistration_NullEmail() {
		String email = null;
		assertThrows(IllegalArgumentException.class, () -> userService.sendOtpForRegistration(email));

		verify(userRepository, never()).findByEmail(anyString());
		verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
	}

	@Test
	void testSendOtpForRegistration_EmptyEmail() {
		String email = "";
		assertThrows(IllegalArgumentException.class, () -> userService.sendOtpForRegistration(email));
		verify(userRepository, never()).findByEmail(anyString());
		verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
	}

	@Test
	void testSendOtpForRegistration_EmailAlreadyExists() {
		String email = "test@example.com";
		when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User()));
		assertThrows(ResourceAlreadyExistsException.class, () -> userService.sendOtpForRegistration(email));
		verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
	}

	@Test
	void testValidateOtpAndRegister_Success() {
		UserRegistrationDTO dto = new UserRegistrationDTO();
		dto.setFullName("Rishabh Jain");
		dto.setEmail("rishabh@gmail.com");
		dto.setPassword("password");
		dto.setAge(24);
		String otp = "123456";
		otpStorage.put(dto.getEmail(), otp);

		User user = new User();
		when(userMapper.toEntity(dto)).thenReturn(user);
		when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");
		when(userRepository.save(user)).thenReturn(user);

		UserDTO userDTO = new UserDTO();
		when(userMapper.toDto(user)).thenReturn(userDTO);

		UserDTO result = userService.validateOtpAndRegister(dto, "123456");

		assertNotNull(result);
		assertFalse(otpStorage.containsKey(dto.getEmail()));

		assertEquals("encodedPassword", user.getPassword());
		assertEquals("USER", user.getRole());
		assertEquals("ACTIVE", user.getStatus());

		verify(userRepository, times(1)).save(user);
		verify(userMapper, times(1)).toDto(user);
	}

	@Test
	void testValidateOtpAndRegister_NullDTO() {

		assertThrows(IllegalArgumentException.class, () -> userService.validateOtpAndRegister(null, "123456"));
	}

	@Test
	void validateOtpAndRegister_NullFullName() {
		UserRegistrationDTO dto = new UserRegistrationDTO();
		dto.setEmail("rishabh@gmail.com");
		dto.setPassword("password");
		dto.setFullName(null);
		dto.setAge(25);
		assertThrows(IllegalArgumentException.class, () -> {
			userService.validateOtpAndRegister(dto, "123456");
		});
	}

	@Test
	void testValidateOtpAndRegister_NullPassword() {
		UserRegistrationDTO dto = new UserRegistrationDTO();
		dto.setEmail("rishabh@gmail.com");
		dto.setPassword(null);
		dto.setFullName("Rishabh");
		dto.setAge(25);
		assertThrows(IllegalArgumentException.class, () -> {
			userService.validateOtpAndRegister(dto, "123456");
		});
	}

	@Test
	void testValidateOtpAndRegister_NullEmail() {
		UserRegistrationDTO dto = new UserRegistrationDTO();
		dto.setEmail(null);
		dto.setPassword("password");
		dto.setFullName("Rishabh");
		dto.setAge(25);
		assertThrows(IllegalArgumentException.class, () -> {
			userService.validateOtpAndRegister(dto, "123456");
		});
	}

	@Test
	void testValidateOtpAndRegister_AgeZero() {
		UserRegistrationDTO dto = new UserRegistrationDTO();
		dto.setEmail("rishabh@gmail.com");
		dto.setPassword("password");
		dto.setFullName("Rishabh");
		dto.setAge(0);
		assertThrows(IllegalArgumentException.class, () -> {
			userService.validateOtpAndRegister(dto, "123456");
		});
	}

	@Test
	void TestValidateOtpAndRegister_NullStoredOtp() {
		UserRegistrationDTO dto = new UserRegistrationDTO();
		dto.setFullName("Rishabh");
		dto.setEmail("rishabh@gmail.com");
		dto.setPassword("password");
		dto.setAge(25);
		otpStorage.remove(dto.getEmail());

		assertThrows(InvalidCredentialException.class, () -> userService.validateOtpAndRegister(dto, "123456"));
	}

	@Test
	void testValidateOtpAndRegister_InvalidOrExpiredOTP() {

		UserRegistrationDTO dto = new UserRegistrationDTO();
		dto.setFullName("Rishabh");
		dto.setEmail("rishabh@gmail.com");
		dto.setPassword("password");
		dto.setAge(25);
		otpStorage.put("rishabh@gmail.com", "654321");

		assertThrows(InvalidCredentialException.class, () -> userService.validateOtpAndRegister(dto, "123456"));
	}

	@Test
	void testLoginUser_Success() {
		LoginDTO dto = new LoginDTO();
		dto.setEmail("rishabh@gmail.com");
		dto.setPassword("password");

		User user = new User();
		user.setEmail("rishabh@gmail.com");
		user.setPassword("encodedPassword");
		user.setStatus("ACTIVE");
		user.setRole("USER");

		when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(true);

		UserDTO expected = new UserDTO();
		when(userMapper.toDto(user)).thenReturn(expected);

		UserDTO result = userService.loginUser(dto);

		assertNotNull(result);
		assertEquals(expected, result);

		verify(userRepository, times(1)).findByEmail(dto.getEmail());
		verify(passwordEncoder, times(1)).matches(dto.getPassword(), user.getPassword());
		verify(userMapper, times(1)).toDto(user);
	}

	@Test
	void testLoginUser_NullDTO() {
		assertThrows(IllegalArgumentException.class, () -> userService.loginUser(null));
	}

	@Test
	void testLoginUser_NullEmail() {
		LoginDTO dto = new LoginDTO();
		dto.setEmail(null);
		dto.setPassword("password");

		assertThrows(IllegalArgumentException.class, () -> userService.loginUser(dto));
	}

	@Test
	void testLoginUser_NullPassword() {
		LoginDTO dto = new LoginDTO();
		dto.setEmail("rishabh@gmail.com");
		dto.setPassword(null);

		assertThrows(IllegalArgumentException.class, () -> userService.loginUser(dto));
	}

	@Test
	void testLoginUser_UserNotFound() {
		LoginDTO dto = new LoginDTO();
		dto.setEmail("rish@gmail.com");
		dto.setPassword("password");

		when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> userService.loginUser(dto));
	}

	@Test
	void testLoginUser_InvalidPassword() {
		LoginDTO dto = new LoginDTO();
		dto.setEmail("rishabh@gmail.com");
		dto.setPassword("wrongPassword");

		User user = new User();
		user.setEmail(dto.getEmail());
		user.setPassword("encodedPassword");

		when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(false);

		assertThrows(InvalidCredentialException.class, () -> userService.loginUser(dto));
	}

	@Test
	void testLoginUser_BlockedAccount() {
		LoginDTO dto = new LoginDTO();
		dto.setEmail("rishabh@example.com");
		dto.setPassword("password");

		User user = new User();
		user.setEmail(dto.getEmail());
		user.setPassword("encodedPassword");
		user.setStatus("BLOCKED");

		when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(true);

		assertThrows(InvalidCredentialException.class, () -> userService.loginUser(dto));
	}

	@Test
	void testLoginUser_NonUserRole() {
		LoginDTO dto = new LoginDTO();
		dto.setEmail("rishabh@gmail.com");
		dto.setPassword("password");

		User user = new User();
		user.setEmail(dto.getEmail());
		user.setPassword("encodedPassword");
		user.setStatus("ACTIVE");
		user.setRole("ADMIN");

		when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(true);

		assertThrows(InvalidCredentialException.class, () -> userService.loginUser(dto));
	}

	@Test
	void testLoginAdmin_Success() {
		LoginDTO dto = new LoginDTO();
		dto.setEmail("admin@example.com");
		dto.setPassword("adminpassword");

		User admin = new User();
		admin.setEmail(dto.getEmail());
		admin.setPassword("encodedAdminPassword");
		admin.setRole("ADMIN");

		when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(admin));
		when(passwordEncoder.matches(dto.getPassword(), admin.getPassword())).thenReturn(true);
		UserDTO expectedAdminDTO = new UserDTO();
		when(userMapper.toDto(admin)).thenReturn(expectedAdminDTO);

		UserDTO actualAdminDTO = userService.loginAdmin(dto);
		assertEquals(expectedAdminDTO, actualAdminDTO);
	}

	@Test
	void testLoginAdmin_NullDTO() {
		assertThrows(IllegalArgumentException.class, () -> userService.loginAdmin(null));
	}

	@Test
	void testLoginAdmin_NullEmail() {
		LoginDTO dto = new LoginDTO();
		dto.setEmail(null);
		dto.setPassword("password");

		assertThrows(IllegalArgumentException.class, () -> userService.loginAdmin(dto));
	}

	@Test
	void testLoginAdmin_NullPassword() {
		LoginDTO dto = new LoginDTO();
		dto.setEmail("admin@example.com");
		dto.setPassword(null);

		assertThrows(IllegalArgumentException.class, () -> userService.loginAdmin(dto));
	}

	@Test
	void testLoginAdmin_EmailNotRegistered() {
		LoginDTO dto = new LoginDTO();
		dto.setEmail("admin@example.com");
		dto.setPassword("adminpassword");

		when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());

		assertThrows(InvalidCredentialException.class, () -> userService.loginAdmin(dto));
	}

	@Test
	void testLoginAdmin_InvalidPassword() {
		LoginDTO dto = new LoginDTO();
		dto.setEmail("admin@example.com");
		dto.setPassword("wrongAdminPassword");

		User admin = new User();
		admin.setEmail(dto.getEmail());
		admin.setPassword("encodedAdminPassword");

		when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(admin));
		when(passwordEncoder.matches(dto.getPassword(), admin.getPassword())).thenReturn(false);

		assertThrows(InvalidCredentialException.class, () -> userService.loginAdmin(dto));
	}

	@Test
	void testLoginAdmin_InvalidRole() {
		LoginDTO dto = new LoginDTO();
		dto.setEmail("admin@example.com");
		dto.setPassword("adminpassword");

		User admin = new User();
		admin.setEmail(dto.getEmail());
		admin.setPassword("encodedAdminPassword");
		admin.setRole("USER");

		when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(admin));
		when(passwordEncoder.matches(dto.getPassword(), admin.getPassword())).thenReturn(true);

		assertThrows(InvalidCredentialException.class, () -> userService.loginAdmin(dto));
	}

	@Test
	void testGetUserById_Success() {
		int userId = 1;

		User user = new User();
		user.setUserId(userId);
		user.setFullName("Rishabh");

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		UserDTO expectedUserDTO = new UserDTO();
		expectedUserDTO.setUserId(userId);
		expectedUserDTO.setFullName("Rishabh");

		when(userMapper.toDto(user)).thenReturn(expectedUserDTO);

		UserDTO actualUserDTO = userService.getUserById(userId);

		assertEquals(expectedUserDTO, actualUserDTO);
		assertEquals("Rishabh", actualUserDTO.getFullName());
	}

	@Test
	void testGetUserById_UserNotFound() {
		int userId = 1;

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(userId));
	}

	@Test
	void testGetAllUsers_Positive() {
		User user1 = new User();
		user1.setRole("USER");

		User user2 = new User();
		user2.setRole("USER");

		User admin = new User();
		admin.setRole("ADMIN");

		List<User> users = List.of(user1, user2, admin);

		when(userRepository.findAll()).thenReturn(users);
		UserDTO userDTO1 = new UserDTO();
		UserDTO userDTO2 = new UserDTO();

		when(userMapper.toDto(user1)).thenReturn(userDTO1);
		when(userMapper.toDto(user2)).thenReturn(userDTO2);

		List<UserDTO> expectedUserDTOs = List.of(userDTO1, userDTO2);
		List<UserDTO> actualUserDTOs = userService.getAllUsers();

		assertEquals(expectedUserDTOs, actualUserDTOs);
	}

	@Test
	void testUpdateUser_Success() {
		int userId = 1;

		User existingUser = new User();
		existingUser.setUserId(userId);
		existingUser.setFullName("Rishabh");
		existingUser.setAge(25);
		existingUser.setEmail("rishabh@example.com");

		UserUpdateDTO dto = new UserUpdateDTO();
		dto.setFullName("Rishabh Jain");
		dto.setAge(30);
		dto.setEmail("rishabh@gmail.com");

		when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
		when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
		when(userRepository.save(existingUser)).thenReturn(existingUser);

		UserDTO expectedDTO = new UserDTO();
		expectedDTO.setUserId(userId);
		expectedDTO.setFullName("Rishabh Jain");
		expectedDTO.setAge(30);
		expectedDTO.setEmail("rishabh@gmail.com");

		when(userMapper.toDto(existingUser)).thenReturn(expectedDTO);

		UserDTO result = userService.updateUser(userId, dto);

		assertNotNull(result);
		assertEquals("Rishabh Jain", result.getFullName());

	}

	@Test
	void testUpdateUser_EmailAlreadyExists() {
		int userId = 1;
		User existingUser = new User();
		existingUser.setUserId(userId);
		existingUser.setEmail("rishabh@gmail.com");

		UserUpdateDTO dto = new UserUpdateDTO();
		dto.setEmail("rishabhrj@gmail.com");

		User anotherUser = new User();
		anotherUser.setUserId(2);
		anotherUser.setEmail("rishabhrj@gmail.com");

		when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
		when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(anotherUser));

		assertThrows(ResourceAlreadyExistsException.class, () -> userService.updateUser(userId, dto));
		verify(userRepository, never()).save(existingUser);
	}

	@Test
	void testUpdateUser_UserNotFound() {
		int userId = 99;
		UserUpdateDTO dto = new UserUpdateDTO();
		dto.setFullName("Rishabh");

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(userId, dto));

		verify(userRepository, times(1)).findById(userId);
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void testUpdateUser_NullDto() {
		int userId = 1;
		assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userId, null));

	}

	@Test
	void testBlockUser_Success() {
		int userId = 1;

		User user = new User();
		user.setUserId(userId);
		user.setStatus("ACTIVE");

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		userService.blockUser(userId);

		assertEquals("BLOCKED", user.getStatus());
		verify(userRepository).save(user);
	}

	@Test
	void testBlockUser_UserNotFound() {
		int userId = 1;

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> userService.blockUser(userId));
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void testUnBlockUser_Success() {
		int userId = 1;

		User user = new User();
		user.setUserId(userId);
		user.setStatus("BLOCKED");

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		userService.unBlockUser(userId);

		assertEquals("ACTIVE", user.getStatus());
		verify(userRepository).save(user);
	}

	@Test
	void testUnBlockUser_UserNotFound() {
		int userId = 1;

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> userService.unBlockUser(userId));
		verify(userRepository, never()).save(any(User.class));
	}

}
