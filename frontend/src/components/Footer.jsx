/* eslint-disable no-unused-vars */
import React from "react";
import { NavLink } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const Footer = () => {
  const { user } = useAuth();

  return (
    <footer className="bg-white shadow-sm dark:bg-gray-900">
      <div className="w-full p-4">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between">
          {/* Logo Section- in the left*/}
          <div className="flex items-center justify-center space-x-3 rtl:space-x-reverse mb-2 sm:mb-0">
            <img
              src="/images/image.png"
              className="h-12 sm:h-14 lg:ml-10"
              alt="GxMovies Logo"
            />
          </div>

          {/* Copyright Section - in the center */}
          <p className="flex justify-center lg:ml-10 text-sm text-gray-500 dark:text-gray-400 sm:text-center mb-4 sm:mb-0">
            &copy; GxMovies. All Rights Reserved.
          </p>

          {/* Links Section - to the right*/}
          <ul className="flex flex-wrap justify-center text-sm sm:text-left font-medium text-gray-500 dark:text-gray-400">
            {user?.role === "USER" ? (
              <li className="mx-2 mb-2 sm:mb-0">
                <NavLink to="/about" className="hover:underline">
                  About
                </NavLink>
              </li>
            ) : (
              ""
            )}
            <li className="mx-2 mb-2 sm:mb-0">
              <NavLink to="#" className="hover:underline">
                Privacy Policy
              </NavLink>
            </li>
            <li className="mx-2 mb-2 sm:mb-0">
              <NavLink to="#" className="hover:underline">
                Licensing
              </NavLink>
            </li>
            <li className="mx-2 mb-2 sm:mb-0">
              <NavLink to="#" className="hover:underline">
                Contact
              </NavLink>
            </li>
          </ul>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
