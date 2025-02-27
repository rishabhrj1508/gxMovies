import { useEffect } from "react";
import toast, { Toaster } from "react-hot-toast";
import { useNavigate } from "react-router-dom";

const SessionTimeout = () => {
  const navigate = useNavigate();

  useEffect(() => {
    // to track the time for inacitivity
    let timer;

    const logoutUser = () => {
      localStorage.clear();
      navigate("/");
      toast.error("You have been logged out due to inactivity");
    };

    // Reset the inactivity timer on user activity
    const resettimer = () => {
      clearTimeout(timer);
      timer = setTimeout(logoutUser, 1 * 60 * 60 * 1000); // 1 hour
    };

    // only checking for mouse and keyboard events
    window.addEventListener("mousemove", resettimer);
    window.addEventListener("keydown", resettimer);

    // page load start the timer
    resettimer();

    // Cleanup event listeners when component is unmounted
    return () => {
      clearTimeout(timer);
      window.removeEventListener("mousemove", resettimer);
      window.removeEventListener("keydown", resettimer);
      <Toaster />;
    };
  }, [navigate]);

  return null;
};

export default SessionTimeout;
