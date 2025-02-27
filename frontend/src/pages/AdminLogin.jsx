// eslint-disable-next-line no-unused-vars
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { loginAdmin } from "../services/UserService";
import { jwtDecode } from "jwt-decode";
import BackgroundLayout from "../components/BackgroundLayout";
import Swal from "sweetalert2";
import toast, { Toaster } from "react-hot-toast";

const AdminLogin = () => {
  const [formData, setFormData] = useState({ email: "", password: "" });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();
  const labelConfig = window.labelConfig;

  const validateForm = () => {
    let formErrors = {};
    let valid = true;

    // Email validation
    if (!formData.email.endsWith(labelConfig.login.errors.emailEndsWithCheck)) {
      formErrors.email = labelConfig.login.errors.emailEndsWithError;
      valid = false;
    }

    // Password validation
    if (formData.password.length < 6) {
      formErrors.password = labelConfig.login.errors.passwordError;
      valid = false;
    }

    setErrors(formErrors);
    return valid;
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    setLoading(true);

    try {
      const token = await loginAdmin(formData);

      const decoded = jwtDecode(token);
      const { role } = decoded;

      if (role !== "ADMIN") {
        throw new Error("This login is for admins only!");
      }

      login(token);

      toast.success("Login Successful");

      setTimeout(() => {
        navigate("/admin/dashboard");
      }, 1000);
    } catch (err) {
      console.log(err)  
      toast.error(err.response.data.message);
      setLoading(false);
    }
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
          <h2 className="text-xl font-semibold text-center mb-6 bg-clip-text text-transparent bg-gradient-to-r from-red-600 to-purple-600">
            {labelConfig.login.adminLoginTitle}
          </h2>

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

            <div className="mt-4">
              <label className="block text-gray-700 text-sm font-semibold mb-2">
                {labelConfig.login.passwordInputLabel}
              </label>
              <input
                name="password"
                type="password"
                placeholder={labelConfig.login.passwordPlaceholder}
                value={formData.password}
                onChange={handleChange}
                maxLength={50}
                className={`text-gray-700 border rounded-md py-2 px-4 w-full focus:ring-2 focus:ring-blue-500 ${
                  errors.password ? "border-red-500" : "border-gray-300"
                }`}
                required
              />
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
                  : labelConfig.login.buttonText.loginButton}{" "}
              </button>
            </div>
          </form>

          <div className="mt-6 text-center">
            <p className="text-sm text-gray-500">
              Not an admin?{" "}
              <span
                className="text-blue-600 cursor-pointer hover:underline"
                onClick={() => navigate("/")}
              >
                Login as User
              </span>
            </p>
          </div>
        </div>
      </BackgroundLayout>
    </>
  );
};

export default AdminLogin;
