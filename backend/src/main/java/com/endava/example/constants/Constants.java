package com.endava.example.constants;

public class Constants {

    // User-related messages
    public static final String USER_NOT_FOUND = "User not found with id: ";
    public static final String USER_ALREADY_EXISTS = "User with this email already exists.";
    public static final String INVALID_CREDENTIALS = "Invalid email or password.";
    public static final String ACCOUNT_BLOCKED = "Your account is blocked. Please contact admin.";
    public static final String ADMIN_ONLY_LOGIN = "This login is only for admins.";
    public static final String USER_ONLY_LOGIN = "This login is only for users.";
    public static final String EMAIL_REQUIRED = "Email cannot be null or empty.";
    public static final String INVALID_EMAIL = "Email cannot be null or empty.";
    public static final String EMAIL_ALREADY_EXISTS = "User with this email already exists.";

    // OTP-related messages
    public static final String OTP_INVALID_OR_EXPIRED = "Invalid or expired OTP.";
    public static final String OTP_SUBJECT = "Registration OTP";
    public static final String OTP_MESSAGE = "Your OTP for registration is: ";

    // Status Constants
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_BLOCKED = "BLOCKED";
    
    // Role Constants
    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";

    // Error Messages
    public static final String LOGIN_EMAIL_NOT_FOUND = "Email is not registered yet.";
    public static final String LOGIN_INVALID_CREDENTIALS = "Invalid email or password.";
    public static final String USER_UPDATE_DTO_REQUIRED = "UserUpdateDto cannot be null.";
    public static final String EMAIL_UPDATE_REQUIRED = "Email cannot be null or empty.";
   

    private Constants() {
        // Private constructor to prevent instantiation
    }
}
