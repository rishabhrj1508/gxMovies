/* eslint-disable no-unused-vars */
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import BackgroundLayout from "../components/BackgroundLayout";
import { useAuth } from "../context/AuthContext";
import { loginUser } from "../services/UserService";
import toast, { Toaster } from "react-hot-toast";
import { AiFillEye, AiFillEyeInvisible } from "react-icons/ai";

const UserLogin = () => {
  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });
  const [errors, setErrors] = useState({});
  const [error, setError] = useState("");
  const { login } = useAuth();
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const labelConfig = window.labelConfig;
  const [type, setType] = useState("password");

  const handleToggle = () => {
    setType(type === "password" ? "text" : "password");
  };

  // Form Validation
  const validateForm = () => {
    let formErrors = {};
    let valid = true;

    // Email validation
    if (!formData.email.trim()) {
      formErrors.email = "Email is required.";
      valid = false;
    } else if (
      !formData.email
        .trim()
        .endsWith(labelConfig.login.errors.emailEndsWithCheck)
    ) {
      formErrors.email = labelConfig.login.errors.emailEndsWithError;
      valid = false;
    }

    // Password validation
    if (!formData.password.trim()) {
      formErrors.password = "Password format is invalid";
      valid = false;
    }

    setErrors(formErrors);
    return valid;
  };

  // Handle Input Change
  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  // Handle Form Submission
  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setErrors({});

    if (!validateForm()) {
      return;
    }
    setLoading(true);

    try {
      const token = await loginUser(formData);
      login(token);

      // Success Toast
      toast.success("Login Successful");

      setTimeout(() => {
        navigate("/user/home");
      }, 1000);
    } catch (err) {
      setError(err.response?.data?.message || "Login failed.");
      console.log(err);
      setLoading(false);
    }
  };

  return (
    <>
      <BackgroundLayout>
        <div>
          <Toaster position="top-right" />
        </div>

        <div className="bg-white shadow-lg rounded-lg max-w-md w-full p-6">
          <div className="flex justify-center items-center">
            <img
              alt="GxMovies"
              src="/images/image.png"
              className="h-14 w-auto"
            />
          </div>
          <h2 className="text-xl font-semibold text-center mb-6 bg-clip-text text-transparent bg-gradient-to-r from-red-600 to-purple-600">
            {labelConfig.login.userLoginTitle}
          </h2>

          {/* Displaying Login Error */}
          {error && (
            <div className="bg-red-100 text-red-700 px-4 py-2 rounded-md mt-4">
              {error}
            </div>
          )}

          {/* Login Form */}
          <form onSubmit={handleSubmit}>
            <div>
              <label className="block text-gray-700 text-sm font-semibold mb-2">
                {labelConfig.login.emailInputLabel}
              </label>
              <input
                name="email"
                type="email"
                placeholder={labelConfig.login.emailPlaceholder}
                value={formData.email}
                onChange={handleChange}
                maxLength={50}
                className={`text-gray-700 border rounded-md py-2 px-4 w-full focus:ring-2 focus:ring-blue-500 ${
                  errors.email ? "border-red-500" : "border-gray-300"
                }`}
                required
              />
              {errors.email && (
                <p className="text-red-500 text-xs mt-1">{errors.email}</p>
              )}
            </div>

            <div className="mt-4 relative">
              <label className="block text-gray-700 text-sm font-semibold mb-2">
                {labelConfig.login.passwordInputLabel}
              </label>
              <input
                name="password"
                type={type}
                placeholder={labelConfig.login.passwordPlaceholder}
                value={formData.password}
                onChange={handleChange}
                maxLength={50}
                className={`text-gray-700 border rounded-md py-2 px-4 w-full focus:ring-2 focus:ring-blue-500 ${
                  errors.password ? "border-red-500" : "border-gray-300"
                }`}
                required
              />
              <span
                className="absolute right-4 top-12 transform -translate-y-1/2 cursor-pointer"
                onClick={handleToggle}
              >
                {type === "password" ? (
                  <AiFillEyeInvisible size={20} />
                ) : (
                  <AiFillEye size={20} />
                )}
              </span>
              {errors.password && (
                <p className="text-red-500 text-xs mt-1">{errors.password}</p>
              )}
            </div>

            <div className="mt-6">
              <button
                type="submit"
                className="bg-blue-600 text-white font-semibold py-2 px-4 w-full rounded-lg hover:bg-blue-500 focus:ring-2 focus:ring-blue-500"
                disabled={loading}
              >
                {loading
                  ? "Logging in..."
                  : labelConfig.login.buttonText.loginButton}
              </button>
            </div>
          </form>

          {/* Sign Up and Admin Login Links */}
          <div className="mt-6 text-center">
            <p className="text-sm text-gray-500">
              Don&apos;t have an account?{" "}
              <span
                className="text-blue-600 cursor-pointer hover:underline"
                onClick={() => navigate("/user/register")}
              >
                Sign Up
              </span>
            </p>
          </div>

          <div className="mt-6 text-center">
            <p className="text-sm text-gray-500">
              Not a User?{" "}
              <span
                className="text-blue-600 cursor-pointer hover:underline"
                onClick={() => navigate("/admin/login")}
              >
                Login as Admin
              </span>
            </p>
          </div>
        </div>
      </BackgroundLayout>
    </>
  );
};

export default UserLogin;
