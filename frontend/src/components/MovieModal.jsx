/* eslint-disable react/prop-types */
/* eslint-disable no-unused-vars */
import React, { useEffect, useState } from "react";
import { addMovie, getMovieById, updateMovie } from "../services/MovieService";
import toast, { Toaster } from "react-hot-toast";
import { Pointer } from "lucide-react";

const MovieModal = ({ movieId, isEdit, closeModal, handleUpdateMovie }) => {
  const [formData, setFormData] = useState({
    title: "",
    description: "",
    genre: "",
    releaseDate: "",
    averageRating: "",
    price: "",
    posterURL: "",
    trailerURL: "",
    status: "AVAILABLE",
  });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  // called once to fetch the movie with the movie Id and in the edit mode
  useEffect(() => {
    if (isEdit && movieId) {
      getMovieById(movieId)
        .then((movie) => setFormData(movie))
        .catch((error) => console.error("Error fetching movie data:", error));
    }
  }, [isEdit, movieId]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  //validation
  const validateForm = () => {
    let formErrors = {};
    let valid = true;

    // Title validation
    if (!formData.title.trim()) {
      formErrors.title = "Title is required.";
      valid = false;
    }

    // Genre validation
    if (!formData.genre.trim()) {
      formErrors.genre = "Genre is required.";
      valid = false;
    }

    // Description validation
    if (!formData.description.trim()) {
      formErrors.description = "Description is required.";
      valid = false;
    }

    // Release Date validation
    if (!formData.releaseDate) {
      formErrors.releaseDate = "Release date is required.";
      valid = false;
    }

    // Price validation
    if (formData.price <= 0) {
      formErrors.price = "Price must be greater than zero.";
      valid = false;
    }

    // Average Rating validation
    if (formData.averageRating < 0 || formData.averageRating > 10) {
      formErrors.averageRating = "Rating must be between 0 and 10.";
      valid = false;
    }

    setErrors(formErrors);
    return valid;
  };
  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    setLoading(true);

    try {
      let response;

      if (isEdit) {
        response = await updateMovie(movieId, formData);
      } else {
        response = await addMovie(formData);
      }

      handleUpdateMovie({ ...formData, movieId });
      closeModal();
    } catch (error) {
      if (error.response) {
        const errorMessage =
          error.response.data.message || "Error saving movie.";
        toast.error(errorMessage);
      } else {
        toast.error("An unknown error occurred.");
      }
      console.error("Error saving movie:", error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed top-0 left-0 w-full h-full flex items-center justify-center bg-gray-900 bg-opacity-50 z-50 p-10">
      <div className="bg-white rounded-lg shadow-lg w-full max-w-3xl overflow-hidden">
        <div className="px-6 py-4 border-b flex justify-between">
          <h2 className="text-lg font-semibold text-gray-700">
            {isEdit ? "Edit Movie" : "Add New Movie"}
          </h2>
          <svg
            xmlns="http://www.w3.org/2000/svg"
            x="0px"
            y="0px"
            width="20"
            height="20"
            viewBox="0 0 50 50"
            onClick={closeModal}
            className="cursor-pointer"
          >
            <path d="M 9.15625 6.3125 L 6.3125 9.15625 L 22.15625 25 L 6.21875 40.96875 L 9.03125 43.78125 L 25 27.84375 L 40.9375 43.78125 L 43.78125 40.9375 L 27.84375 25 L 43.6875 9.15625 L 40.84375 6.3125 L 25 22.15625 Z"></path>
          </svg>
        </div>

        <form
          onSubmit={handleSubmit}
          className="px-6 py-4 max-h-[70vh] overflow-y-auto"
        >
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            {/* Title */}
            <div>
              <label className="text-sm text-gray-700 font-medium">Title</label>
              <input
                type="text"
                name="title"
                value={formData.title}
                onChange={handleChange}
                placeholder="Enter movie title"
                maxLength={50}
                required
                className={`w-full mt-1 px-3 py-2 border rounded-md focus:ring focus:ring-blue-500 ${
                  errors.title ? "border-red-500" : "border-gray-300"
                }`}
              />
              {errors.title && (
                <p className="text-red-500 text-xs mt-1">{errors.title}</p>
              )}
            </div>

            {/* Genre */}
            <div>
              <label className="text-sm text-gray-700 font-medium">Genre</label>
              <select
                name="genre"
                value={formData.genre}
                onChange={handleChange}
                required
                className={`w-full mt-1 px-3 py-2 border rounded-md focus:ring focus:ring-blue-500 ${
                  errors.genre ? "border-red-500" : "border-gray-300"
                }`}
              >
                <option value="">Select Genre</option>
                <option value="Action">Action</option>
                <option value="Comedy">Comedy</option>
                <option value="Superhero">Superhero</option>
                <option value="Drama">Drama</option>
                <option value="Horror">Horror</option>
                <option value="Sci-Fi">Sci-Fi</option>
                <option value="Romance">Romance</option>
                <option value="Thriller">Thriller</option>
              </select>
              {errors.genre && (
                <p className="text-red-500 text-xs mt-1">{errors.genre}</p>
              )}
            </div>

            {/* Description */}
            <div className="sm:col-span-2">
              <label className="text-sm text-gray-700 font-medium">
                Description
              </label>
              <textarea
                name="description"
                value={formData.description}
                onChange={handleChange}
                placeholder="Enter description"
                required
                maxLength={200}
                rows={3}
                className={`w-full mt-1 px-3 py-2 border rounded-md focus:ring focus:ring-blue-500 ${
                  errors.description ? "border-red-500" : "border-gray-300"
                }`}
              />
              {errors.description && (
                <p className="text-red-500 text-xs mt-1">
                  {errors.description}
                </p>
              )}
            </div>

            {/* Release Date */}
            <div>
              <label className="text-sm text-gray-700 font-medium">
                Release Date
              </label>
              <input
                type="date"
                name="releaseDate"
                value={formData.releaseDate}
                onChange={handleChange}
                required
                max={new Date().toISOString().split("T")[0]}
                className={`w-full mt-1 px-3 py-2 border rounded-md focus:ring focus:ring-blue-500 ${
                  errors.releaseDate ? "border-red-500" : "border-gray-300"
                }`}
              />
              {errors.releaseDate && (
                <p className="text-red-500 text-xs mt-1">
                  {errors.releaseDate}
                </p>
              )}
            </div>

            {/* Price */}
            <div>
              <label className="text-sm text-gray-700 font-medium">Price</label>
              <input
                type="number"
                name="price"
                value={formData.price}
                onChange={handleChange}
                placeholder="Enter price"
                required
                min={99}
                max={9999}
                step={1}
                className={`w-full mt-1 px-3 py-2 border rounded-md focus:ring focus:ring-blue-500 ${
                  errors.price ? "border-red-500" : "border-gray-300"
                }`}
              />
              {errors.price && (
                <p className="text-red-500 text-xs mt-1">{errors.price}</p>
              )}
            </div>

            {/* Average Rating */}
            <div>
              <label className="text-sm text-gray-700 font-medium">
                Average Rating
              </label>
              <input
                type="number"
                name="averageRating"
                value={formData.averageRating}
                onChange={handleChange}
                placeholder="Enter rating"
                required
                min="1"
                max="10"
                step="0.1"
                className={`w-full mt-1 px-3 py-2 border rounded-md focus:ring focus:ring-blue-500 ${
                  errors.averageRating ? "border-red-500" : "border-gray-300"
                }`}
              />
              {errors.averageRating && (
                <p className="text-red-500 text-xs mt-1">
                  {errors.averageRating}
                </p>
              )}
            </div>

            {/* Poster URL */}
            <div>
              <label className="text-sm text-gray-700 font-medium">
                Poster URL
              </label>
              <input
                type="url"
                name="posterURL"
                value={formData.posterURL}
                onChange={handleChange}
                placeholder="Enter poster URL"
                maxLength={600}
                required
                className={`w-full mt-1 px-3 py-2 border rounded-md focus:ring focus:ring-blue-500 ${
                  errors.posterURL ? "border-red-500" : "border-gray-300"
                }`}
              />
              {errors.posterURL && (
                <p className="text-red-500 text-xs mt-1">{errors.posterURL}</p>
              )}
            </div>

            {/* Trailer URL */}
            <div>
              <label className="text-sm text-gray-700 font-medium">
                Trailer URL
              </label>
              <input
                type="url"
                name="trailerURL"
                value={formData.trailerURL}
                onChange={handleChange}
                placeholder="Enter trailer URL"
                maxLength={600}
                required
                className={`w-full mt-1 px-3 py-2 border rounded-md focus:ring focus:ring-blue-500 ${
                  errors.trailerURL ? "border-red-500" : "border-gray-300"
                }`}
              />
              {errors.trailerURL && (
                <p className="text-red-500 text-xs mt-1">{errors.trailerURL}</p>
              )}
            </div>

            {/* Status */}
            <div className="sm:col-span-2">
              <label className="text-sm text-gray-700 font-medium">
                Status
              </label>
              <select
                name="status"
                value={formData.status}
                onChange={handleChange}
                required
                className="w-full mt-1 px-3 py-2 border rounded-md focus:ring focus:ring-blue-500"
              >
                <option value="AVAILABLE">Available</option>
                <option value="UNAVAILABLE">Unavailable</option>
              </select>
            </div>
          </div>

          {/* Actions */}
          <div className="flex justify-end mt-6 space-x-4">
            <button
              type="button"
              onClick={closeModal}
              className="bg-gray-500 text-white px-4 py-2 rounded-md hover:bg-gray-400"
            >
              Close
            </button>
            <button
              type="submit"
              className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-500"
              disabled={loading}
            >
              {loading ? "Saving..." : isEdit ? "Update" : "Add"} Movie
            </button>
          </div>
        </form>
      </div>
      <Toaster position="top-right" />
    </div>
  );
};

export default MovieModal;
