import { handleApiRequest } from "./ApiService";

const MOVIE_API_URL = "/movies";

// Add Movie
export const addMovie = async (data) =>
  handleApiRequest("post", `${MOVIE_API_URL}/add`, data);

// Update Movie
export const updateMovie = async (movieId, data) =>
  handleApiRequest("put", `${MOVIE_API_URL}/${movieId}`, data);

// Get Movie by ID
export const getMovieById = async (movieId) =>
  handleApiRequest("get", `${MOVIE_API_URL}/${movieId}`);

// Delete Movie
export const deleteMovie = async (movieId) =>
  handleApiRequest("post", `${MOVIE_API_URL}/${movieId}/delete`);

// Get All Movies with Filters, Sorting
export const getMovies = async () =>
  handleApiRequest("get", `${MOVIE_API_URL}/all`);

// Get All Available Movies
export const getAvailableMovies = async () =>
  handleApiRequest("get", `${MOVIE_API_URL}/all/available`);

// Get Recommended Movies by Genre
export const getRecommendedMovies = async (genre) =>
  handleApiRequest("get", `${MOVIE_API_URL}/recommended`, null, { genre });
