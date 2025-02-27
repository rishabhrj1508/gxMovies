import axios from "axios";
import Swal from "sweetalert2";

const API_URL = import.meta.env.VITE_API_BASE_URL;

// Function to retrieve the token from localStorage
const getToken = () => localStorage.getItem("token");

// Create an Axios instance
const api = axios.create({
  baseURL: API_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

//  Request Interceptor: Attach JWT token to every request
api.interceptors.request.use(
  (config) => {
    const token = getToken();
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

//  Response Interceptor: Auto-logout on 401 unauthorized (User Blocked) - i am sending it from the from jwt filter
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // If it's a blocked user and there's a token, show SweetAlert
      if (getToken()) {
        Swal.fire({
          icon: "warning",
          title: "Access Denied",
          text: "Your account has been blocked. You will be logged out.",
          confirmButtonText: "OK",
        }).then((event) => {
          if (event.isConfirmed) {
            localStorage.removeItem("token");
            setTimeout(() => {
              window.location.href = "/";
            }, 500); // 500ms delay
          }
        });
      }
    }
    // for rate limiter - 429 status code -- requests made more than allocated
    if (error.response && error.response.status === 429) {
      Swal.fire({
        icon: "warning",
        title: error.response.data,
        text: "Please wait while the request limit refills..",
      });
    }
    return Promise.reject(error); // see what this line do
  }
);

export default api;
