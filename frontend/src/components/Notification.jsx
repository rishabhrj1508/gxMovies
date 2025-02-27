/* eslint-disable react-hooks/exhaustive-deps */
/* eslint-disable no-unused-vars */
import React, { useEffect } from "react";
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const NotificationComponent = () => {
  const connectToSSE = () => {
    const eventSource = new EventSource("https://gxmovies.onrender.com/notifications");

    // Listen for messages ..positioning it to the top right
    eventSource.onmessage = function (event) {
      toast.info(event.data, {
        position: "top-right",
        autoClose: 5000,
        hideProgressBar: false,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: "dark",
      });
    };

    // Handle connection errors
    eventSource.onerror = function () {
      console.error("SSE connection failed. Reconnecting...");
      eventSource.close();

      // Attempt reconnection after 5 seconds
      setTimeout(() => connectToSSE(), 5000);
    };
  };

  // Connecting the client to sse endpoint in the start only..
  useEffect(() => {
    connectToSSE(); // Connect to the SSE endpoint
    return () => {}; // Cleanup is handled by reconnection logic
  }, []);

  return (
    <div>
      <ToastContainer />
    </div>
  );
};

export default NotificationComponent;
