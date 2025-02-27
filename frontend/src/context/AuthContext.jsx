/* eslint-disable react-refresh/only-export-components */
/* eslint-disable react/prop-types */
/* eslint-disable no-unused-vars */
import React, { createContext, useContext, useState, useEffect } from "react";
import { jwtDecode } from "jwt-decode";
import { getUserById } from "../services/UserService";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    try {
      const token = localStorage.getItem("token");
      if (token) {
        const decoded = jwtDecode(token);
        return {
          userId: decoded.userId,
          role: decoded.role,
        };
      }
      return null;
    } catch (error) {
      console.error("Error decoding token:", error);
      return null;
    }
  });

  const [username, setUsername] = useState(() => {
    return localStorage.getItem("username") || null;
  });

  useEffect(() => {
    const fetchUsername = async () => {
      if (user?.userId) {
        try {
          const response = await getUserById(user.userId); // Await the Promise
          setUsername(response.fullName);
          localStorage.setItem("username", response.fullName);
        } catch (error) {
          console.error("Error fetching username:", error);
        }
      }
    };

    fetchUsername();
  }, [user?.userId, username]);

  const login = (token) => {
    localStorage.setItem("token", token);
    const decoded = jwtDecode(token);
    setUser({ userId: decoded.userId, role: decoded.role });
  };

  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("username"); // Clear username from localStorage
    setUser(null);
    setUsername(null); // Clear the username on logout
  };

  return (
    <AuthContext.Provider value={{ user, username, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
