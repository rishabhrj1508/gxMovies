/* eslint-disable react/prop-types */
/* eslint-disable no-unused-vars */
import React from "react";
import Confetti from "react-confetti";

const ConfettiAroundIcon = ({ show }) => {
  if (!show) return null;

  // styles for positioning the confetti with respect to the area
  const confettiStyles = {
    position: "absolute",
    width: "100%",
    top: "-10px",
    height: "400px",
    pointerEvents: "none",
  };

  return (
    <Confetti
      style={confettiStyles}
      gravity={0.1}
      initialVelocityX={3}
      initialVelocityY={-10}
      recycle={false}
    />
  );
};

export default ConfettiAroundIcon;
