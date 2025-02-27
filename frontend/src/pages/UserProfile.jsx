/* eslint-disable no-unused-vars */
import React, { useEffect, useState } from "react";
import UserNavbar from "../components/UserNavbar";
import { useAuth } from "../context/AuthContext";
import { getUserById, updateUserById } from "../services/UserService";
import Swal from "sweetalert2";

const Profile = () => {
  const labelConfig = window.labelConfig;
  const { user } = useAuth();
  const userId = user.userId;
  const [profile, setProfile] = useState({ fullName: "", age: "", email: "" });
  const [originalProfile, setOriginalProfile] = useState({
    fullName: "",
    age: "",
    email: "",
  });
  const [isEditing, setIsEditing] = useState(false);
  const [error, setError] = useState("");
  const [formErrors, setFormErrors] = useState({
    fullName: "",
    age: "",
    email: "",
  });

  const getInitials = (name) => {
    return name
      .split(" ")
      .map((word) => word[0])
      .join("");
  };

  useEffect(() => {
    if (userId) {
      fetchUserData();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [userId]);

  const fetchUserData = async () => {
    try {
      const data = await getUserById(userId);
      setProfile(data);
      setOriginalProfile(data);
    } catch (err) {
      setError("Failed to load profile data.");
      throw err;
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setProfile((prevProfile) => ({ ...prevProfile, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    if (!validate()) return;
    try {
      const updatedProfile = await updateUserById(userId, profile);
      setProfile(updatedProfile);
      localStorage.setItem("username", updatedProfile.fullName);
      setOriginalProfile(updatedProfile);
      setIsEditing(false);
      Swal.fire({
        icon: "success",
        title: "Profile Updated Successfully!",
        showConfirmButton: false,
        timer: 2000,
      });
    } catch (error) {
      setError(
        error.response?.data?.message || "An unexpected error occurred."
      );
    }
  };

  const validate = () => {
    let isValid = true;
    let errors = { fullName: "", age: "", email: "" };

    if (!profile.fullName || !/^[a-zA-Z\s]+$/.test(profile.fullName)) {
      errors.fullName = "Full Name must contain only letters and spaces.";
      isValid = false;
    }

    if (!profile.age || profile.age < 14 || profile.age > 110) {
      errors.age = "Age must be between 14 and 110.";
      isValid = false;
    }

    if (
      !profile.email ||
      !/^[a-zA-Z0-9._%+-]+@gmail\.com$/.test(profile.email)
    ) {
      errors.email =
        "Please enter a valid Gmail address ending with @gmail.com.";
      isValid = false;
    }

    setFormErrors(errors);
    return isValid;
  };

  const hasChanges = () => {
    return (
      profile.fullName !== originalProfile.fullName ||
      profile.age !== originalProfile.age ||
      profile.email !== originalProfile.email
    );
  };

  return (
    <>
      <UserNavbar />
      <div className="flex flex-col min-h-screen bg-gray-100">
        <h1 className="text-4xl font-bold mt-6 text-center text-gray-800">
          <span className="bg-clip-text text-transparent bg-gradient-to-r from-red-600 to-purple-600">
            Your Profile
          </span>{" "}
          😎
        </h1>
        <div className="flex-grow flex items-center justify-center px-4">
          <div className="bg-white shadow-lg rounded-lg w-full max-w-4xl p-6 flex">
            <div className="hidden md:flex flex-1 justify-center">
              <img
                src="/images/profile2.png"
                alt="Profile Illustration"
                className="w-96"
              />
            </div>
            <div className="flex-1">
              {error && (
                <p className="text-red-500 text-center mb-4">{error}</p>
              )}
              {!isEditing ? (
                <div className="p-6 rounded-lg border border-gray-300 bg-gray-50">
                  <div className="flex justify-center">
                    <div className="h-24 w-24 rounded-full bg-blue-500 flex items-center justify-center text-white text-2xl font-bold shadow-md">
                      {getInitials(profile.fullName)}
                    </div>
                  </div>
                  <p className="text-gray-700 mb-2">
                    <strong>{labelConfig.profile.nameDisplayLabel}:</strong>{" "}
                    {profile.fullName}
                  </p>
                  <p className="text-gray-700 mb-2">
                    <strong>{labelConfig.profile.ageDisplayLabel}:</strong>{" "}
                    {profile.age}
                  </p>
                  <p className="text-gray-700 mb-4">
                    <strong>{labelConfig.profile.emailDisplayLabel}:</strong>{" "}
                    {profile.email}
                  </p>
                  <button
                    className="w-full py-2 px-4 bg-red-500 text-white font-semibold rounded-lg hover:bg-red-400"
                    onClick={() => {
                      setIsEditing(true);
                      setFormErrors("");
                      setError("");
                    }}
                  >
                    {labelConfig.profile.buttonText.updateButton}
                  </button>
                </div>
              ) : (
                <form onSubmit={handleSubmit} className="space-y-4">
                  <div className="flex justify-center">
                    <div className="h-24 w-24 rounded-full bg-blue-500 flex items-center justify-center text-white text-2xl font-bold shadow-md">
                      {getInitials(profile.fullName)}
                    </div>
                  </div>
                  <input
                    type="text"
                    name="fullName"
                    value={profile.fullName}
                    onChange={handleInputChange}
                    className="w-full py-2 px-4 border border-gray-300 rounded-md"
                    placeholder={labelConfig.profile.form.nameLabel}
                    required
                  />
                  {formErrors.fullName && (
                    <p className="text-red-500 text-sm">
                      {formErrors.fullName}
                    </p>
                  )}

                  <input
                    type="number"
                    name="age"
                    value={profile.age}
                    onChange={handleInputChange}
                    className="w-full py-2 px-4 border border-gray-300 rounded-md"
                    placeholder={labelConfig.profile.form.ageLabel}
                    required
                    min={14}
                    max={110}
                  />
                  {formErrors.age && (
                    <p className="text-red-500 text-sm">{formErrors.age}</p>
                  )}
                  <input
                    type="email"
                    name="email"
                    value={profile.email}
                    onChange={handleInputChange}
                    className="w-full py-2 px-4 border border-gray-300 rounded-md"
                    placeholder={labelConfig.profile.form.emailLabel}
                    required
                  />
                  {formErrors.email && (
                    <p className="text-red-500 text-sm">{formErrors.email}</p>
                  )}
                  <div className="flex space-x-4">
                    <button
                      type="submit"
                      className="w-full py-2 px-4 bg-green-500 text-white rounded-lg disabled:opacity-50 disabled:cursor-not-allowed"
                      disabled={!hasChanges()}
                    >
                      {labelConfig.profile.buttonText.saveButton}
                    </button>
                    <button
                      type="button"
                      className="w-full py-2 px-4 bg-gray-600 text-white rounded-lg hover:bg-gray-500"
                      onClick={() => {
                        setIsEditing(false);
                        setFormErrors("");
                        setError("");
                        fetchUserData();
                      }}
                    >
                      {labelConfig.profile.buttonText.cancelButton}
                    </button>
                  </div>
                </form>
              )}
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default Profile;
