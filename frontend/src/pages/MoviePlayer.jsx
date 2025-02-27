/* eslint-disable no-unused-vars */
import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import UserNavbar from "../components/UserNavbar";
import { getMovieById } from "../services/MovieService";

const MoviePlayer = () => {
  const location = useLocation();
  const movieId = location.state.movieId;
  const [movie, setMovie] = useState(null);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchMovieDetails = async () => {
      try {
        const fetchedMovie = await getMovieById(movieId);
        if (fetchedMovie) {
          if (
            fetchedMovie.trailerURL &&
            fetchedMovie.trailerURL.includes("youtube.com/watch?v=")
          ) {
            const embedURL = fetchedMovie.trailerURL.replace(
              "https://www.youtube.com/watch?v=",
              "https://www.youtube.com/embed/"
            );
            fetchedMovie.trailerURL = embedURL;
          }
          setMovie(fetchedMovie);
        } else {
          setError("Movie not found.");
        }
      } catch (err) {
        setError("Failed to load movie details.", err);
      }
    };

    fetchMovieDetails();
  }, [movieId]);

  if (error) {
    return (
      <div className="text-center mt-5">
        <p className="text-red-500">{error}</p>
        <button
          onClick={() => navigate("/user/library")}
          className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-700"
        >
          Go Back
        </button>
      </div>
    );
  }

  return (
    <>
      <UserNavbar />
      <div className="flex flex-col min-h-screen bg-gray-100">
        <h1 className="text-3xl font-semibold text-center mt-6">
          Sit Back and Enjoy! Have Your Popcorn Ready!🍿
        </h1>
        <div className="flex flex-col md:flex-row w-full max-w-screen-xl mx-auto shadow-lg rounded-lg overflow-hidden mt-4 p-4 flex-grow">
          {movie ? (
            <>
              <div className="md:w-2/3 w-full p-4">
                <div className="flex justify-center mb-4">
                  <iframe
                    width="100%"
                    height="450"
                    src={movie.trailerURL}
                    title={movie.title}
                    frameBorder="0"
                    allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture"
                    allowFullScreen
                    onError={(e) => {
                      setError(
                        "Failed to load the video. Please try again later."
                      );
                      e.target.style.display = "none";
                    }}
                    className="w-full border rounded-lg shadow-lg"
                  ></iframe>
                </div>
              </div>

              <div className="md:w-1/3 h-max bg-gray-200 p-6 flex flex-col justify-start mt-4 shadow-xl hover:shadow-2xl">
                <div>
                  <h3 className="text-2xl font-semibold mb-4 mt-4 text-center text-gray-900">
                    Now Playing 🎬
                  </h3>
                  <p className="text-gray-700 mb-4">
                    <span className="font-semibold">Title:</span> {movie.title}
                  </p>
                  <p className="text-gray-700 mb-4">
                    <span className="font-semibold">Genre:</span> {movie.genre}
                  </p>
                  <p className="text-gray-700 mb-4">
                    <span className="font-semibold">Description:</span>{" "}
                    {movie.description}
                  </p>
                  <p className="text-gray-700 mb-4">
                    <span className="font-semibold">Release Date:</span>{" "}
                    {movie.releaseDate}
                  </p>
                  <p className="text-gray-700 mb-4">
                    <span className="font-semibold">Rating:</span>{" "}
                    {movie.averageRating} / 10
                  </p>
                </div>
                <div className="text-center mt-6 mb-4">
                  <button
                    onClick={() => navigate(`/user/library`)}
                    className="bg-blue-500 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition-all duration-200"
                  >
                    Back to Library
                  </button>
                </div>
              </div>
            </>
          ) : (
            <div className="text-center text-gray-900 mt-10">
              Loading movie details...
            </div>
          )}
        </div>
      </div>
    </>
  );
};

export default MoviePlayer;
