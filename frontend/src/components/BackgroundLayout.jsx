/* eslint-disable react/prop-types */
/* eslint-disable no-unused-vars */
import React from "react";

const BackgroundLayout = ({ children }) => {
  return (
    <div
      className="relative h-screen w-full bg-cover bg-center bg-no-repeat"
      style={{ backgroundImage: "url('/images/netflixBackground.jpg')" }}
    >
      {/* Overlay for blur background */}
      <div className="absolute inset-0 bg-black bg-opacity-50"></div>

      <div className="absolute inset-0 flex flex-col justify-center items-center z-30">
        {children}
      </div>
    </div>
  );
};

export default BackgroundLayout;
