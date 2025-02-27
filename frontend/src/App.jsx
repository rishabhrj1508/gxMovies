/* eslint-disable no-unused-vars */
import React, { useEffect } from "react";
import { BrowserRouter, Route, Routes, useLocation } from "react-router-dom";
import "./App.css";
import AboutUs from "./components/About";
import AdminNavbar from "./components/AdminNavbar";
import Footer from "./components/Footer";
import Notification from "./components/Notification";
import SessionTimeout from "./components/SessionTimeout";
import UserNavbar from "./components/UserNavbar";
import AdminDashboard from "./pages/AdminDashboard";
import AdminLogin from "./pages/AdminLogin";
import Cart from "./pages/Cart";
import Favorite from "./pages/Favorite";
import MovieDetails from "./pages/MovieDetails";
import MovieManagement from "./pages/MovieManagement";
import MoviePlayer from "./pages/MoviePlayer";
import NotFound from "./pages/NotFound";
import Orders from "./pages/Orders";
import ReviewManagement from "./pages/ReviewManagement";
import Unauthorized from "./pages/Unauthorized";
import UserHome from "./pages/UserHome";
import UserLibrary from "./pages/UserLibrary";
import UserLogin from "./pages/UserLogin";
import UserManagement from "./pages/UserManagement";
import UserProfile from "./pages/UserProfile";
import UserRegistration from "./pages/UserRegistration";
import ProtectedRoute from "./routes/ProtectedRoute";

const App = () => {
  // function to make every route page display from the top only
  function ScrollToTop() {
    const location = useLocation();

    useEffect(() => {
      window.scrollTo(0, 0);
    }, [location]);

    return null;
  }

  return (
    <BrowserRouter>
      <Notification />
      <SessionTimeout />
      <div>
        <ScrollToTop />
        <Routes>
          <Route path="/" element={<UserLogin />} />
          <Route path="/user/register" element={<UserRegistration />} />
          <Route path="/admin/login" element={<AdminLogin />} />
          <Route
            path="/about"
            element={
              <ProtectedRoute role="USER">
                <UserNavbar />
                <AboutUs />
              </ProtectedRoute>
            }
          />
          <Route
            path="/user/home"
            element={
              <ProtectedRoute role="USER">
                <UserNavbar />
                <UserHome />
              </ProtectedRoute>
            }
          />

          <Route
            path="/movies"
            element={
              <ProtectedRoute role="USER">
                <UserNavbar />
                <MovieDetails />
              </ProtectedRoute>
            }
          />

          <Route
            path="/user/profile"
            element={
              <ProtectedRoute role="USER">
                <UserProfile />
              </ProtectedRoute>
            }
          />

          <Route
            path="/user/favorites"
            element={
              <ProtectedRoute role="USER">
                <Favorite />
              </ProtectedRoute>
            }
          />

          <Route
            path="/user/cart"
            element={
              <ProtectedRoute role="USER">
                <Cart />
              </ProtectedRoute>
            }
          />

          <Route
            path="/user/orders"
            element={
              <ProtectedRoute role="USER">
                <Orders />
              </ProtectedRoute>
            }
          />

          <Route
            path="/user/library"
            element={
              <ProtectedRoute role="USER">
                <UserLibrary />
              </ProtectedRoute>
            }
          />

          <Route
            path="/movie/watch"
            element={
              <ProtectedRoute role="USER">
                <MoviePlayer />
              </ProtectedRoute>
            }
          />

          <Route
            path="/admin/dashboard"
            element={
              <ProtectedRoute role="ADMIN">
                <AdminNavbar />
                <AdminDashboard />
              </ProtectedRoute>
            }
          />

          <Route
            path="/admin/movies"
            element={
              <ProtectedRoute role="ADMIN">
                <AdminNavbar />
                <MovieManagement />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/reviews"
            element={
              <ProtectedRoute role="ADMIN">
                <AdminNavbar />
                <ReviewManagement />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/users"
            element={
              <ProtectedRoute role="ADMIN">
                <AdminNavbar />
                <UserManagement />
              </ProtectedRoute>
            }
          />

          <Route path="/forbidden" element={<Unauthorized />} />

          <Route path="*" element={<NotFound />} />
        </Routes>
        <Footer />
      </div>
    </BrowserRouter>
  );
};

export default App;
