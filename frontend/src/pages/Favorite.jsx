/* eslint-disable react-hooks/exhaustive-deps */
/* eslint-disable no-unused-vars */
import { faPlay, faShoppingCart } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import UserNavbar from "../components/UserNavbar";
import { useAuth } from "../context/AuthContext";
import { addToCart, checkMovieInCartOfUser } from "../services/CartService";
import {
  getFavoritesOfUser,
  removeFromFavorites,
} from "../services/FavoriteService";
import { getAllMoviesPurchasedByUser } from "../services/PurchaseService";
import toast, { Toaster } from "react-hot-toast";
import PacmanLoader from "react-spinners/PacmanLoader";

const Favorite = () => {
  const [favorites, setFavorites] = useState([]);
  const [cartStatus, setCartStatus] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [cartError, setCartError] = useState(null);
  const [purchasedMovies, setPurchasedMovies] = useState([]);
  const { user } = useAuth();
  const userId = user.userId;
  const navigate = useNavigate();
  const labelConfig = window.labelConfig;

  const fetchFavorites = async () => {
    try {
      setLoading(true);
      const response = await getFavoritesOfUser(userId);
      setFavorites(response.reverse());
    } catch (error) {
      setError(labelConfig.favorites.errors.fetchFavoritesError);
      console.error("Error fetching favorites", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (userId) {
      fetchFavorites();
    }
  }, [userId]);

  useEffect(() => {
    if (userId) {
      const fetchPurchasedMovies = async () => {
        const response = await getAllMoviesPurchasedByUser(userId);
        setPurchasedMovies(response);
      };
      fetchPurchasedMovies();
    }
  }, [userId]);

  useEffect(() => {
    if (userId && favorites.length > 0) {
      const fetchCartStatus = async () => {
        const newCartStatus = {};
        for (const fav of favorites) {
          try {
            const isInCart = await checkMovieInCartOfUser(
              userId,
              fav.movieDTO.movieId
            );
            newCartStatus[fav.movieDTO.movieId] = isInCart;
          } catch (error) {
            setCartError(labelConfig.favorites.errors.fetchCartStatus);
            console.error("Error fetching cart status", error);
          }
        }
        setCartStatus(newCartStatus);
      };
      fetchCartStatus();
    }
  }, [userId, favorites]);

  const handleRemoveFavorite = async (userId, movieId) => {
    await removeFromFavorites(userId, movieId);
    toast.success("Removed from favorites");
    fetchFavorites();
  };

  const handleAddToCart = async (userId, movieId) => {
    try {
      await addToCart({ userId, movieId });
      setCartStatus({ ...cartStatus, [movieId]: true });
      toast.success("Added to Cart");
    } catch (error) {
      setCartError(labelConfig.favorites.errors.addToCartError);
      console.error("Error adding to cart", error);
    }
  };

  const isPurchased = (movieId) =>
    purchasedMovies.some((movie) => movie.movieId === movieId);

  const handleWatchNow = (movieId) =>
    navigate(`/movie/watch`, { state: { movieId } });

  return (
    <>
      <UserNavbar />
      <Toaster position="top-right" />
      <div className="bg-gray-100 min-h-screen text-gray-800">
        <div className="container mx-auto py-6 px-4">
          <h1 className="text-3xl font-bold mb-6 text-center text-gray-800">
            <span className="bg-clip-text text-transparent bg-gradient-to-r from-red-600 to-purple-600">
              {labelConfig.favorites.title}
            </span>{" "}
            ❤️
          </h1>

          {loading && (
            <PacmanLoader
              size={20}
              color="red"
              className="mt-10 mx-auto"
              loading={loading}
            />
          )}
          {error && !loading && (
            <div className="text-red-500 text-center mt-4">{error}</div>
          )}
          {cartError && (
            <div className="bg-red-100 text-red-800 p-3 rounded-md mb-4 text-center">
              {cartError}
            </div>
          )}

          {!loading && !error && favorites.length === 0 && (
            <div className="flex flex-col items-center">
              <img
                src="/images/favnew.png"
                alt="No favorites"
                className="mb-2 max-w-xs"
              />
              <h2 className="text-lg font-semibold text-gray-600 mb-2">
                {labelConfig.favorites.noFavoritesText}
              </h2>
              <button
                onClick={() => navigate("/user/home")}
                className="bg-blue-600 text-white py-2 px-6 rounded-lg hover:bg-blue-700 transition duration-300"
              >
                {labelConfig.favorites.buttonText.continueBrowsingButton}
              </button>
            </div>
          )}

          {!loading && !error && favorites.length > 0 && (
            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
              {favorites.map((fav) => (
                <div
                  key={fav.favoriteId}
                  className="bg-white rounded-lg overflow-hidden shadow-xl hover:scale-105 transition"
                >
                  <img
                    src={fav.movieDTO.posterURL}
                    alt={fav.movieDTO.title}
                    className="w-full h-60 object-cover"
                  />
                  <div className="p-3">
                    <h3 className="text-sm font-bold text-gray-800 truncate">
                      {fav.movieDTO.title}
                    </h3>
                    <p className="text-xs text-gray-500">
                      Rating: {fav.movieDTO.averageRating} ★
                    </p>
                    <div className="flex flex-row gap-4">
                      <button
                        onClick={() =>
                          handleRemoveFavorite(fav.userId, fav.movieId)
                        }
                        className="w-1/2 mt-3 py-2 text-xs bg-red-500 text-white font-semibold rounded-md hover:bg-red-600 transition"
                      >
                        {labelConfig.favorites.buttonText.removeButton}
                      </button>
                      {isPurchased(fav.movieDTO.movieId) ? (
                        <button
                          onClick={() => handleWatchNow(fav.movieDTO.movieId)}
                          className="w-1/2 text-xs mt-3 py-2 bg-green-500 text-white rounded-md font-semibold shadow-md hover:bg-green-600"
                        >
                          <FontAwesomeIcon icon={faPlay} className="mr-2" />{" "}
                          {labelConfig.favorites.buttonText.watchButton}
                        </button>
                      ) : cartStatus[fav.movieDTO.movieId] ? (
                        <button
                          disabled
                          className="w-1/2 text-xs mt-3 py-2 bg-gray-400 text-white rounded-md font-semibold shadow-md"
                        >
                          <FontAwesomeIcon
                            icon={faShoppingCart}
                            className="mr-2"
                          />{" "}
                          {labelConfig.favorites.buttonText.alreadyInCartButton}
                        </button>
                      ) : (
                        <button
                          onClick={() =>
                            handleAddToCart(userId, fav.movieDTO.movieId)
                          }
                          className="w-1/2 text-xs mt-3 py-2 bg-blue-500 text-white rounded-md font-semibold shadow-md hover:bg-blue-600"
                        >
                          <FontAwesomeIcon
                            icon={faShoppingCart}
                            className="mr-2"
                          />{" "}
                          {labelConfig.favorites.buttonText.addtoCartButton}
                        </button>
                      )}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </>
  );
};

export default Favorite;
