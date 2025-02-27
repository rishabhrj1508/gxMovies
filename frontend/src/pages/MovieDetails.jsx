/* eslint-disable no-unused-vars */
import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { addToCart, checkMovieInCartOfUser } from "../services/CartService";
import {
  addToFavorites,
  checkFavoriteStatus,
  removeFromFavorites,
} from "../services/FavoriteService";
import { getMovieById, getRecommendedMovies } from "../services/MovieService";
import { checkIfMoviePurchased } from "../services/PurchaseService";
import { getMovieReviews, handleReportReview } from "../services/ReviewService";

import {
  faFlag,
  faHeart,
  faPlay,
  faShareAlt,
  faShoppingCart,
  faUser,
} from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import toast, { Toaster } from "react-hot-toast";
import ReactPaginate from "react-paginate";
import ConfettiAroundIcon from "../components/Confetti";

const MovieDetails = () => {
  const location = useLocation();
  const movieId = location.state.movieId;
  const { user } = useAuth();
  const userId = user.userId;
  const [movie, setMovie] = useState(null);
  const [reviews, setReviews] = useState([]);
  const [isFavorite, setIsFavorite] = useState(false);
  const [isInCart, setIsInCart] = useState(false);
  const [isPurchased, setIsPurchased] = useState(false);
  const [recommendedMovies, setRecommendedMovies] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [reviewsPerPage] = useState(4);
  const [showShareOptions, setShowShareOptions] = useState(false);
  const [showConfetti, setShowConfetti] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    getMovieById(movieId).then(setMovie);
    getMovieReviews(movieId).then(setReviews);
    checkFavoriteStatus(userId, movieId).then(setIsFavorite);
    checkMovieInCartOfUser(userId, movieId).then(setIsInCart);
    checkIfMoviePurchased(userId, movieId).then(setIsPurchased);
  }, [movieId, userId]);

  useEffect(() => {
    if (movie?.genre) {
      getRecommendedMovies(movie.genre).then((movies) => {
        const filteredMovies = movies.filter((m) => m.movieId != movieId);
        setRecommendedMovies(filteredMovies);
      });
    }
  }, [movie?.genre, movieId]);

  const handleAddToFavorites = () => {
    if (isFavorite) {
      removeFromFavorites(userId, movieId)
        .then(() => {
          setIsFavorite(false);
          toast.success("Removed from favorites", {
            style: {
              border: "1px solid #E50914",
              padding: "16px",
              color: "#000000",
              background: "#FFFFFF",
              fontFamily: "Arial, sans-serif",
            },
            iconTheme: {
              primary: "#E50914",
              secondary: "#000000",
            },
          });
        })
        .catch((error) => {
          throw error;
        });
    } else {
      addToFavorites({ userId, movieId })
        .then(() => {
          setIsFavorite(true);
          setShowConfetti(true);
          setTimeout(() => {
            setShowConfetti(false);
          }, 2000);
          toast.success("Added to favorites", {
            style: {
              border: "1px solid #E50914",
              padding: "16px",
              color: "#000000",
              background: "#FFFFFF",
              fontFamily: "Arial, sans-serif",
            },
            iconTheme: {
              primary: "#E50914",
              secondary: "#000000",
            },
          });
        })
        .catch((error) => {
          throw error;
        });
    }
  };

  const handleAddToCart = () => {
    addToCart({ userId, movieId }).then(() => {
      setIsInCart(true);
      toast.success("Added to Cart", {
        style: {
          border: "1px solid #E50914",
          padding: "16px",
          color: "#000000",
          background: "#FFFFFF",
          fontFamily: "Arial, sans-serif",
        },
        iconTheme: {
          primary: "#E50914",
          secondary: "#000000",
        },
      });
    });
  };

  const handleWatchNow = () => {
    navigate(`/movie/watch`, {
      state: {
        movieId: movieId,
      },
    });
  };

  const handleShareOutlook = () => {
    const subject = `Check out this movie: ${movie?.title}`;
    const body = `Here are the details:\n
                  Title: ${movie?.title}\n
                  Genre: ${movie?.genre}\n
                  Release Date: ${movie?.releaseDate}\n
                  Rating: ${movie?.averageRating} ⭐\n
                  Price: Rs. ${movie?.price}`;
    window.location.href = `mailto:?subject=${encodeURIComponent(
      subject
    )}&body=${encodeURIComponent(body)}`;
  };

  const handleShareTeams = () => {
    const message = `Check out this movie:
      Title: ${movie?.title}
      Genre: ${movie?.genre}
      Release Date: ${movie?.releaseDate}
      Rating: ${movie?.averageRating} ⭐
      Price: Rs. ${movie?.price}`;
    const teamsUrl = `https://teams.microsoft.com/l/chat/0/0?users=&message=${encodeURIComponent(
      message
    )}`;
    window.open(teamsUrl, "_blank");
  };

  const handlePageClick = ({ selected }) => {
    setCurrentPage(selected);
  };

  const offset = currentPage * reviewsPerPage;
  const currentReviews = reviews.slice(offset, offset + reviewsPerPage);
  const pageCount = Math.ceil(reviews.length / reviewsPerPage);

  return (
    <>
      <Toaster position="top-right" />
      <div className="bg-gray-100 min-h-screen p-2 flex justify-center items-start">
        <div className="container mx-auto px-6">
          <div className="text-center rounded-md mb-2">
            <h1
              className="text-3xl font-bold capitalize truncate whitespace-nowrap overflow-hidden text-ellipsis"
              title={movie?.title}
            >
              {" "}
              {movie?.title} ⬇️✨
            </h1>
            <p className="text-lg pt-2 mb-2">
              Reviews and Top Recommendations 🎬{" "}
            </p>
          </div>
          <div className="flex flex-col lg:flex-row gap-4 w-full max-w-7xl text-left">
            <div className="flex-1 bg-white rounded-lg shadow-md p-2 relative">
              <img
                src={movie?.posterURL}
                alt={movie?.title}
                className="w-full max-h-64 object-cover rounded-lg shadow-lg mb-4"
              />

              <div className="flex justify-between  w-full space-x-4 relative">
                <p
                  className="text-lg mb-1 pl-2 mt-2 truncate whitespace-nowrap overflow-hidden text-ellipsis max-w-[300px]"
                  title={movie?.genre}
                >
                  <strong>Genre:</strong> {movie?.genre}
                </p>
                <span>
                  {showConfetti && <ConfettiAroundIcon show={showConfetti} />}
                </span>
                <div className="flex gap-4 mb-2">
                  <button
                    onClick={handleAddToFavorites}
                    className={`text-3xl transition-transform transform hover:scale-150 ${
                      isFavorite ? "text-red-500" : "text-gray-400"
                    }`}
                  >
                    <FontAwesomeIcon icon={faHeart} />
                  </button>

                  {isPurchased ? (
                    <button
                      onClick={handleWatchNow}
                      className="px-4 bg-green-500 text-white rounded-lg font-semibold shadow-md hover:bg-green-600 flex items-center"
                    >
                      <FontAwesomeIcon icon={faPlay} className="mr-2" /> Watch
                      Now
                    </button>
                  ) : isInCart ? (
                    <button
                      disabled
                      className="px-4 py-2 bg-gray-400 text-white rounded-lg font-semibold shadow-md flex items-center"
                    >
                      <FontAwesomeIcon icon={faShoppingCart} className="mr-2" />{" "}
                      Already in Cart
                    </button>
                  ) : (
                    <button
                      onClick={handleAddToCart}
                      className="px-4 py-2 bg-blue-500 text-white rounded-lg font-semibold shadow-md hover:bg-blue-600 flex items-center"
                    >
                      <FontAwesomeIcon icon={faShoppingCart} className="mr-2" />{" "}
                      Add to Cart
                    </button>
                  )}

                  <button
                    onClick={() => setShowShareOptions(!showShareOptions)}
                    className="px-4 py-2 text-white bg-blue-500 shadow-md rounded-lg font-semibold flex items-center transition-transform transform hover:scale-105"
                  >
                    <FontAwesomeIcon icon={faShareAlt} className="mr-2" /> Share
                  </button>
                </div>
              </div>

              <div className="justify-start items-start mb-4 pl-2">
                <div className="w-5/6">
                  <p
                    className="text-lg mb-1 truncate whitespace-nowrap overflow-hidden text-ellipsis max-w-[600px] text-wrap"
                    title={movie?.description}
                  >
                    <strong>Description:</strong> {movie?.description}
                  </p>
                  <p className="text-lg mb-1">
                    <strong>Release Date:</strong> {movie?.releaseDate}
                  </p>
                  <p className="text-lg mb-1">
                    <strong>Price:</strong> Rs. {movie?.price}
                  </p>
                  <p className="text-lg mb-1">
                    <strong>Rating:</strong> {movie?.averageRating} ⭐
                  </p>
                </div>
              </div>
            </div>

            <div className="w-full lg:w-2/5 bg-white rounded-lg shadow-lg p-4 text-left">
              <h2 className="text-2xl font-bold text-black mb-4 items-center text-center">
                🌟 Reviews
              </h2>
              <div className="space-y-2 max-h-100">
                {currentReviews.length > 0 ? (
                  currentReviews.map((review) => (
                    <div
                      key={review.reviewId}
                      className="bg-white border border-gray-300 rounded-lg p-4 shadow-md"
                    >
                      <p className="text-sm text-gray-700 mb-2 flex items-center">
                        <FontAwesomeIcon icon={faUser} className="mr-2" />{" "}
                        {review.username}
                        {review.userId !== userId ? (
                          <button
                            className="ml-auto text-red-500 hover:text-red-700 flex items-center transition-transform transform hover:scale-105"
                            onClick={() => handleReportReview(review.reviewId)}
                          >
                            <FontAwesomeIcon icon={faFlag} className="mr-1" />{" "}
                            Report
                          </button>
                        ) : (
                          ""
                        )}
                      </p>
                      <p
                        className="text-md text-black pl-4 truncate whitespace-nowrap overflow-hidden text-ellipsis"
                        title={review.reviewText}
                      >
                        {review.reviewText}
                      </p>
                    </div>
                  ))
                ) : (
                  <p className="text-gray-600 text-center">
                    No reviews available for this movie.
                  </p>
                )}
              </div>
              {reviews.length > 0 && pageCount > 1 && (
                <ReactPaginate
                  previousLabel={"Previous"}
                  nextLabel={"Next"}
                  breakLabel={"..."}
                  breakClassName={"text-gray-500"}
                  pageCount={pageCount}
                  marginPagesDisplayed={1}
                  pageRangeDisplayed={3}
                  onPageChange={handlePageClick}
                  containerClassName={
                    "flex items-center justify-center space-x-1 mt-2"
                  }
                  activeClassName={"bg-red-600 rounded-lg text-white"}
                  pageLinkClassName={
                    "text-gray-800 border border-gray-300 rounded-lg px-3 py-2 transition duration-300 hover:bg-red-500 hover:text-white block w-full text-center"
                  }
                  previousClassName={`border border-gray-300 rounded-lg px-2 hover:bg-red-500 py-2 transition duration-300 ${
                    currentPage === 0
                      ? "cursor-not-allowed opacity-50"
                      : "hover:bg-gray-200"
                  }`}
                  nextClassName={`border border-gray-300 rounded-lg px-2 py-2 hover:bg-red-500 transition duration-300 ${
                    currentPage === pageCount - 1
                      ? "cursor-not-allowed opacity-50"
                      : "hover:bg-gray-200"
                  }`}
                  disabledClassName={"pointer-events-none opacity-50"}
                />
              )}
            </div>
          </div>

          <div className="mt-20">
            <h2 className="text-3xl font-bold text-center m-6 truncate whitespace-nowrap overflow-hidden text-ellipsis">
              🎬✨ Cinematic Gems Inspired by {`"${movie?.title}"`} 🌟📽️
            </h2>
            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6 mb-8 mt-8">
              {recommendedMovies.length > 0 ? (
                recommendedMovies.map((recommendedMovie) => (
                  <div
                    key={recommendedMovie.movieId}
                    className="bg-white rounded-lg overflow-hidden shadow-xl transition-transform transform hover:scale-105 hover:shadow-2xl pb-2 cursor-pointer cusro"
                    onClick={() => {
                      navigate(`/movies`, {
                        state: {
                          movieId: recommendedMovie.movieId,
                        },
                      });
                    }}
                  >
                    <img
                      src={recommendedMovie.posterURL}
                      alt={recommendedMovie.title}
                      className="w-full h-64 object-cover rounded-3xl mb-4 p-2"
                    />
                    <h3 className="text-xl font-semibold pl-4">
                      {recommendedMovie.title}
                    </h3>
                    <p className="text-gray-700 pl-4">
                      {recommendedMovie.genre}
                    </p>
                    <p className="text-gray-700 pl-4">
                      Rs. {recommendedMovie.price}
                    </p>
                  </div>
                ))
              ) : (
                <div className="text-gray-600 text-center">
                  No recommendations available.
                </div>
              )}
            </div>
          </div>
        </div>

        {showShareOptions && (
          <div
            className="fixed inset-0 bg-gray-800 bg-opacity-50 flex justify-center items-center"
            onClick={() => setShowShareOptions(false)}
          >
            <div className="bg-white rounded-lg p-8 shadow-lg max-w-md w-full">
              <h3 className="text-2xl font-semibold mb-4">Share this movie</h3>
              <button
                onClick={handleShareOutlook}
                className="w-full py-2 text-center bg-purple-500 text-white rounded-lg font-semibold mb-4"
              >
                Share on Outlook
              </button>
              <button
                onClick={handleShareTeams}
                className="w-full py-2 text-center bg-purple-500 text-white rounded-lg font-semibold"
              >
                Share on Teams
              </button>
            </div>
          </div>
        )}
      </div>
    </>
  );
};

export default MovieDetails;
