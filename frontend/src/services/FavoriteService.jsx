import { handleApiRequest } from "./ApiService";

const FAVORITE_API_URL = "/favorites";

// Add a Movie to the User's Favorites
export const addToFavorites = async (favoriteDTO) =>
  handleApiRequest("post", `${FAVORITE_API_URL}`, favoriteDTO);

// Get Favorites of a User
export const getFavoritesOfUser = async (userId) =>
  handleApiRequest("get", `${FAVORITE_API_URL}/user/${userId}`);

// Delete Favorite by Favorite ID
export const deleteFavorites = async (favId) =>
  handleApiRequest("delete", `${FAVORITE_API_URL}/${favId}`);

// Check if a Movie is in the User's Favorites
export const checkFavoriteStatus = async (userId, movieId) =>
  handleApiRequest(
    "get",
    `${FAVORITE_API_URL}/user/${userId}/movie/${movieId}`
  );

// Remove from Favorites by User ID and Movie ID
export const removeFromFavorites = async (userId, movieId) =>
  handleApiRequest("delete", `${FAVORITE_API_URL}`,null, {
    userId,
    movieId,
  });
