import { handleApiRequest } from "./ApiService";

const USER_API_URL = "/users";

// Send OTP for Registration
export const sendRegistrationOtp = async (email) =>
  handleApiRequest("post", `${USER_API_URL}/auth/send-registration-otp`, null, {
    email,
  });

// Validate OTP and Register User
export const validateRegistrationOtp = async (registrationData, otp) =>
  handleApiRequest(
    "post",
    `${USER_API_URL}/auth/validate-registration`,
    registrationData,
    { otp }
  );

// Login User
export const loginUser = async (loginData) =>
  handleApiRequest("post", `${USER_API_URL}/auth/user-login`, loginData);

// Login Admin
export const loginAdmin = async (adminData) =>
  handleApiRequest("post", `${USER_API_URL}/auth/admin-login`, adminData);

// Get User by ID
export const getUserById = async (id) =>
  handleApiRequest("get", `${USER_API_URL}/${id}`);

// Update User by ID
export const updateUserById = async (id, data) =>
  handleApiRequest("put", `${USER_API_URL}/${id}`, data);

// Block User
export const blockUser = async (id) =>
  handleApiRequest("patch", `${USER_API_URL}/block/${id}`);

// Unblock User
export const unBlockUser = async (id) =>
  handleApiRequest("patch", `${USER_API_URL}/unblock/${id}`);

// Get All Users
export const getAllUsers = async () =>
  handleApiRequest("get", `${USER_API_URL}`);
