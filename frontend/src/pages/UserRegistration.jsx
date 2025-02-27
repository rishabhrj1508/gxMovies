/* eslint-disable no-unused-vars */
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import BackgroundLayout from "../components/BackgroundLayout";
import {
  sendRegistrationOtp,
  validateRegistrationOtp,
} from "../services/UserService";
import Swal from "sweetalert2";
import toast, { Toaster } from "react-hot-toast";

const UserRegister = () => {
  const [formData, setFormData] = useState({
    fullName: "",
    age: "",
    email: "",
    password: "",
    confirmPassword: "",
    otp: "",
  });
  const [showOtpModal, setShowOtpModal] = useState(false);
  const [errors, setErrors] = useState({});
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const labelConfig = window.labelConfig;

  const validateForm = () => {
    let formErrors = {};
    let valid = true;

    // Full Name validation
    // this regex will make sure only letters and one space is allowed in between
    const namePattern = /^[A-Za-z]+(?: [A-Za-z]+)*$/;
    const trimmedName = formData.fullName.trim();

    if (!trimmedName) {
      formErrors.fullName = labelConfig.userRegistration.errors.fullnameError;
      valid = false;
    } else if (!namePattern.test(trimmedName)) {
      formErrors.fullName = labelConfig.userRegistration.errors.fullnameInvalid;
      valid = false;
    }

    // Age validation
    if (!formData.age || formData.age < 14 || formData.age > 110) {
      formErrors.age = labelConfig.userRegistration.errors.ageError;
      valid = false;
    }

    // Email validation - with format and @gmail.com
    if (
      !formData.email
        .trim()
        .endsWith(labelConfig.userRegistration.errors.emailEndsWithCheck)
    ) {
      formErrors.email = labelConfig.userRegistration.errors.emailEndsWithError;
      valid = false;
    }

    // Password validation
    const regexPassword =
      /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

    if (!regexPassword.test(formData.password.trim())) {
      formErrors.password = labelConfig.userRegistration.errors.passwordError;
      valid = false;
    }

    // Confirm Password validation
    if (formData.password !== formData.confirmPassword) {
      formErrors.confirmPassword =
        labelConfig.userRegistration.errors.confirmPasswordError;
      valid = false;
    }

    setErrors(formErrors);
    return valid;
  };

  const handleChange = (e) => {
    let { name, value } = e.target;

    if (name === "fullName") {
      value = value.replace(/^\s+/, "").replace(/\s+/g, " ");
    }

    if (name === "email") {
      value = value.replace(/^\s+/, "");
    }

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSendOtp = async (e) => {
    e.preventDefault();
    setError("");
    setErrors({});
    console.log(formData);

    if (!validateForm()) {
      return;
    }

    try {
      setLoading(true);
      await sendRegistrationOtp(formData.email);
      toast.success("OTP sent to your email", {
        style: {
          border: "1px solid #E50914",
          padding: "16px",
          color: "#000000",
          background: "#FFFFFF",
          fontFamily: "Arial, sans-serif",
        },
        iconTheme: {
          primary: "#E50914",
          secondary: "#000000",
        },
      });
      setShowOtpModal(true);
    } catch (err) {
      setError(err.response?.data?.message || "Failed to send OTP.");
    } finally {
      setLoading(false);
    }
  };

  const handleValidateOtp = async () => {
    setError("");
    setLoading(true);

    try {
      await validateRegistrationOtp(
        {
          fullName: formData.fullName,
          age: formData.age,
          email: formData.email,
          password: formData.password,
        },
        formData.otp
      );
      Swal.fire({
        title: "Registration SuccessFull..",
        text: "You can now log in..",
        timer: 2000,
        type: "sucess",
      });
      navigate("/");
    } catch (err) {
      setError(
        err.response?.data?.message || "Invalid OTP or registration failed."
      );
    } finally {
      setLoading(false);
    }
  };

  const handleCancelOtp = () => {
    setShowOtpModal(false);
    navigate("/user/register");
  };

  return (
    <>
      <BackgroundLayout>
        <Toaster position="top-right" />
        <div className="bg-white shadow-lg rounded-lg max-w-md w-full p-6 z-30">
          <div className="flex justify-center items-center">
            <img
              alt="GxMovies"
              src="/images/image.png"
              className="h-14 w-auto"
            />
          </div>
          <h2 className="text-xl font-semibold text-center text-gray-700">
            {labelConfig.userRegistration.title}
          </h2>

          {error && (
            <div className="bg-red-100 text-red-700 px-4 py-2 rounded-md mt-4">
              {error}
            </div>
          )}

          <form onSubmit={handleSendOtp} className="mt-6">
            {/* Full Name */}
            <div>
              <label className="block text-gray-700 text-sm font-semibold mb-1">
                Full Name
              </label>
              <input
                name="fullName"
                type="text"
                placeholder={labelConfig.userRegistration.fullnamePlaceholder}
                value={formData.fullName}
                onChange={handleChange}
                className={`text-gray-700 border rounded-md py-2 px-4 w-full focus:ring-2 focus:ring-blue-500 ${
                  errors.fullName ? "border-red-500" : "border-gray-300"
                }`}
                maxLength={40}
                required
              />
              {errors.fullName && (
                <p className="text-red-500 text-xs mt-1">{errors.fullName}</p>
              )}
            </div>

            {/* Age */}
            <div className="mt-1">
              <label className="block text-gray-700 text-sm font-semibold mb-1">
                Age
              </label>
              <input
                name="age"
                type="number"
                min={14}
                max={110}
                placeholder={labelConfig.userRegistration.agePlaceholder}
                value={formData.age}
                onChange={handleChange}
                className={`text-gray-700 border rounded-md py-2 px-4 w-full focus:ring-2 focus:ring-blue-500 ${
                  errors.age ? "border-red-500" : "border-gray-300"
                }`}
                required
              />
              {errors.age && (
                <p className="text-red-500 text-xs mt-1">{errors.age}</p>
              )}
            </div>

            {/* Email */}
            <div className="mt-4">
              <label className="block text-gray-700 text-sm font-semibold mb-1">
                Email Address
              </label>
              <input
                name="email"
                type="email"
                maxLength={50}
                placeholder={labelConfig.userRegistration.emailPlaceholder}
                value={formData.email}
                onChange={handleChange}
                className={`text-gray-700 border rounded-md py-2 px-4 w-full focus:ring-2 focus:ring-blue-500 ${
                  errors.email ? "border-red-500" : "border-gray-300"
                }`}
                required
              />
              {errors.email && (
                <p className="text-red-500 text-xs mt-1">{errors.email}</p>
              )}
            </div>

            {/* Password */}
            <div className="mt-4">
              <label className="block text-gray-700 text-sm font-semibold mb-1">
                Password
              </label>
              <input
                name="password"
                type="password"
                max={50}
                placeholder={labelConfig.userRegistration.passwordPlaceholder}
                value={formData.password}
                onChange={handleChange}
                className={`text-gray-700 border rounded-md py-2 px-4 w-full focus:ring-2 focus:ring-blue-500 ${
                  errors.password ? "border-red-500" : "border-gray-300"
                }`}
                required
              />
              {errors.password && (
                <p className="text-red-500 text-xs mt-1">{errors.password}</p>
              )}
            </div>

            {/* Confirm Password */}
            <div className="mt-4">
              <label className="block text-gray-700 text-sm font-semibold mb-1">
                Confirm Password
              </label>
              <input
                name="confirmPassword"
                type="password"
                maxLength={50}
                placeholder={
                  labelConfig.userRegistration.confirmPasswordPlaceholder
                }
                value={formData.confirmPassword}
                onChange={handleChange}
                className={`text-gray-700 border rounded-md py-2 px-4 w-full focus:ring-2 focus:ring-blue-500 ${
                  errors.confirmPassword ? "border-red-500" : "border-gray-300"
                }`}
                required
              />
              {errors.confirmPassword && (
                <p className="text-red-500 text-xs mt-1">
                  {errors.confirmPassword}
                </p>
              )}
            </div>

            {/* Register Button */}
            <div className="mt-4">
              <button
                type="submit"
                className="bg-blue-600 text-white font-semibold py-2 px-4 w-full rounded-lg hover:bg-blue-500 focus:ring-2 focus:ring-blue-500"
                disabled={loading}
              >
                {loading
                  ? labelConfig.userRegistration.registerButtonIfLoadingTrue
                  : labelConfig.userRegistration.registerButtonIfLoadingFalse}
              </button>
            </div>
          </form>

          {/* Already have an account? */}
          <div className="mt-4 text-center">
            <p className="text-sm text-gray-500">
              Already have an account?{" "}
              <span
                className="text-blue-600 cursor-pointer hover:underline"
                onClick={() => navigate("/")}
              >
                Login
              </span>
            </p>
          </div>
        </div>

        {/* OTP Modal */}
        {showOtpModal && (
          <div className="otp-modal fixed top-0 left-0 w-full h-full flex items-center justify-center bg-gray-900 bg-opacity-50 z-40">
            <div className="bg-white p-6 rounded-lg shadow-lg max-w-sm w-full">
              <h2 className="text-lg font-semibold text-gray-700 text-center">
                Enter OTP
              </h2>
              <input
                className="text-gray-700 border border-gray-300 rounded-md py-2 px-4 w-full focus:ring-2 focus:ring-blue-500 mt-4"
                type="text"
                placeholder={labelConfig.userRegistration.otpPlaceholder}
                name="otp"
                maxLength={6}
                value={formData.otp}
                onChange={handleChange}
                required
              />
              <div className="mt-4 flex justify-center space-x-4">
                <button
                  onClick={handleValidateOtp}
                  className="bg-blue-600 text-white font-semibold py-2 px-4 rounded-lg hover:bg-blue-500 focus:ring-2 focus:ring-blue-500"
                  disabled={loading}
                >
                  {loading
                    ? labelConfig.userRegistration.otpButtonIfLoadingTrue
                    : labelConfig.userRegistration.otpButtonIfLoadingFalse}
                </button>
                <button
                  onClick={handleCancelOtp}
                  className="bg-gray-600 text-white font-semibold py-2 px-4 rounded-lg hover:bg-gray-500 focus:ring-2 focus:ring-gray-500"
                  disabled={loading}
                >
                  {labelConfig.userRegistration.buttonText.cancelButton}
                </button>
              </div>
              {error && (
                <div className="bg-red-100 text-red-700 px-4 py-2 rounded-md mt-4">
                  {error}
                </div>
              )}
            </div>
          </div>
        )}
      </BackgroundLayout>
    </>
  );
};

export default UserRegister;
