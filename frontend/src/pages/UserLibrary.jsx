/* eslint-disable react-hooks/exhaustive-deps */
/* eslint-disable no-unused-vars */
import { faEdit, faPlay } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import UserNavbar from "../components/UserNavbar";
import { useAuth } from "../context/AuthContext";
import { getAllMoviesPurchasedByUser } from "../services/PurchaseService";
import { addReview } from "../services/ReviewService";
import { getUserById } from "../services/UserService";
import Swal from "sweetalert2";
import { PacmanLoader } from "react-spinners";

const UserLibrary = () => {
  const { user } = useAuth();
  const userId = user.userId;
  const [purchasedMovies, setPurchasedMovies] = useState([]);
  const [showReviewModal, setShowReviewModal] = useState(false);
  const [selectedMovie, setSelectedMovie] = useState(null);
  const [reviewText, setReviewText] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const labelConfig = window.labelConfig;

  useEffect(() => {
    const fetchPurchasedMovies = async () => {
      try {
        const movies = await getAllMoviesPurchasedByUser(userId);
        setPurchasedMovies(movies);
      } catch (err) {
        setError(labelConfig.library.errors.fetchMoviesError);
      } finally {
        setLoading(false);
      }
    };

    fetchPurchasedMovies();
  }, [userId]);

  const handleReviewSubmit = async () => {
    if (!reviewText.trim()) {
      setError(labelConfig.library.errors.addReviewError);
      return;
    }

    try {
      const currentUser = await getUserById(userId);
      const reviewDTO = {
        userId,
        movieId: selectedMovie.movieId,
        username: currentUser.fullName,
        moviename: selectedMovie.title,
        reviewText,
        reported: false,
      };

      await addReview(reviewDTO);
      Swal.fire({
        title: "Thank you!",
        text: "Your review has been submitted.",
        icon: "success",
        confirmButtonText: "OK",
      });

      setShowReviewModal(false);
      setReviewText("");
    } catch (err) {
      Swal.fire({
        title: "Oops!",
        text: "Something went wrong. Please try submitting your review again.",
        icon: "error",
        confirmButtonText: "OK",
      });
      setError("Oops! Something went wrong. Please try again.");
    }
  };

  return (
    <>
      <UserNavbar />
      <div className="bg-gray-200 min-h-screen text-gray-800">
        <div className="container mx-auto py-8 px-6">
          <h2 className="text-3xl font-semibold text-center text-gray-900 mb-8">
            🎥
            <span className="bg-clip-text text-transparent bg-gradient-to-r from-red-600 to-purple-600">
              {labelConfig.library.title}
            </span>{" "}
            - ▶️{" "}
            <span className="bg-clip-text text-transparent bg-gradient-to-r from-red-600 to-purple-600">
              {labelConfig.library.titleSubpart}
            </span>
          </h2>

          {/* Display errors if there is one */}
          {error && <p className="text-red-500 text-center text-lg">{error}</p>}

          {/* Loading State */}
          {loading ? (
            <div className="flex justify-center items-center py-10">
              <PacmanLoader size={20} color="red" loading={loading} />
            </div>
          ) : purchasedMovies.length === 0 && !error ? (
            <p className="text-center text-lg text-gray-500">
              {labelConfig.library?.errors?.noMoviesText}
            </p>
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
              {purchasedMovies.map((movie) => (
                <div
                  key={movie.movieId}
                  className="bg-white rounded-lg shadow-md hover:shadow-lg transition-all transform hover:scale-105"
                >
                  <img
                    src={movie.posterURL}
                    alt={movie.title}
                    className="w-full h-48 object-cover rounded-t-lg"
                  />
                  <div className="p-2">
                    <h3 className="text-lg text-center text-gray-800 font-bold mb-2">
                      {movie.title}
                    </h3>
                    <div className="flex justify-center items-center space-x-3">
                      {movie.status === "AVAILABLE" ? (
                        <>
                          <button
                            className="py-2 px-3 mb-2 text-sm bg-blue-600 text-white font-semibold rounded-md hover:bg-blue-500 flex items-center space-x-2 transition"
                            onClick={() =>
                              navigate(`/movie/watch`, {
                                state: { movieId: movie.movieId },
                              })
                            }
                          >
                            <FontAwesomeIcon icon={faPlay} size="sm" />
                            <span>
                              {labelConfig.library.buttonText.watchButton}
                            </span>
                          </button>
                          <button
                            className="py-2 px-3 mb-2 text-sm bg-green-600 text-white font-semibold rounded-md hover:bg-green-500 flex items-center space-x-2 transition"
                            onClick={() => {
                              setSelectedMovie(movie);
                              setShowReviewModal(true);
                            }}
                          >
                            <FontAwesomeIcon icon={faEdit} size="sm" />
                            <span>
                              {labelConfig.library.buttonText.writeReviewButton}
                            </span>
                          </button>
                        </>
                      ) : (
                        <span className="text-red-600 font-medium">
                          {labelConfig.library.unavailableText}
                        </span>
                      )}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}

          {/* Review Modal */}
          {showReviewModal && (
            <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50">
              <div className="bg-white p-6 rounded-lg shadow-lg w-96">
                <h3 className="text-xl font-semibold text-center text-gray-800 mb-4">
                  ✍️ {labelConfig.library.reviewModalTitle}{" "}
                  {selectedMovie.title}
                </h3>
                <textarea
                  value={reviewText}
                  onChange={(e) => setReviewText(e.target.value)}
                  rows="4"
                  maxLength={200}
                  className="w-full border border-gray-500 rounded-md p-3 mb-4 bg-gray-50 text-gray-800 focus:ring-2 focus:ring-blue-600"
                  placeholder={labelConfig.library.textAreaPlaceholder}
                />
                <div className="flex justify-center space-x-3">
                  <button
                    className="py-2 px-4 bg-green-600 text-white rounded-md hover:bg-green-500"
                    onClick={handleReviewSubmit}
                  >
                    {labelConfig.library.buttonText.submitReviewButton}
                  </button>
                  <button
                    className="py-2 px-4 bg-gray-600 text-white rounded-md hover:bg-gray-500"
                    onClick={() => setShowReviewModal(false)}
                  >
                    {labelConfig.library.buttonText.cancelButton}
                  </button>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </>
  );
};

export default UserLibrary;
