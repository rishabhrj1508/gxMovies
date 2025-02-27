/* eslint-disable react/prop-types */
import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const ProtectedRoute = ({ role, children }) => {
  const { user } = useAuth();
  if (!user) {
    return <Navigate to="/" replace />; // Redirect to login if not authenticated or role mismatch
  }

  if (user.role !== role) {
    return <Navigate to="/forbidden" replace />;
  }

  return children;
};

export default ProtectedRoute;
