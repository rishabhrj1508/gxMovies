import { handleApiRequest } from "./ApiService";

const CART_API_URL = "/carts";

// Add a movie to the user's cart
export const addToCart = async (cartDTO) =>
  handleApiRequest("post", `${CART_API_URL}`, cartDTO);

// Get all cart items of a specific user
export const getAllItemsInCartOfUser = async (userId) =>
  handleApiRequest("get", `${CART_API_URL}/user/${userId}`);

// Remove an item from the cart by cart ID
export const removeFromCart = async (cartId) =>
  handleApiRequest("delete", `${CART_API_URL}/${cartId}`);

// Remove a movie from the cart by user ID and movie ID
export const removeFromCartOfUser = async (userId, movieId) =>
  handleApiRequest("delete", `${CART_API_URL}/user/${userId}/movie/${movieId}`);

// Remove selected movies from cart of user
export const removeMultipleCartItemsOfUser = async (userId, movieIds) =>
  handleApiRequest(
    "delete",
    `${CART_API_URL}/user/remove-multiple-cartItems`,
    movieIds,
    { userId }
  );

// Clear all cart items for a specific user
export const clearCartOfUser = async (userId) =>
  handleApiRequest("delete", `${CART_API_URL}/user/${userId}/clear`);

// Check if a movie is in the user's cart
export const checkMovieInCartOfUser = async (userId, movieId) =>
  handleApiRequest("get", `${CART_API_URL}/user/${userId}/movie/${movieId}`);
