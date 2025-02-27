import { toast } from "react-hot-toast";
import api from "./Api";
import { handleApiRequest } from "./ApiService";

const REVIEW_API_URL = "/reviews";

// Add Review
export const addReview = async (reviewDTO) =>
  handleApiRequest("post", `${REVIEW_API_URL}/add`, reviewDTO);

// Fetch Reviews for a Specific Movie
export const getMovieReviews = async (movieId) =>
  handleApiRequest("get", `${REVIEW_API_URL}/movie/${movieId}`);

// Get All Reported Reviews
export const getReportedReviews = async () =>
  handleApiRequest("get", `${REVIEW_API_URL}/reported`);

// Delete a Review
export const deleteReview = async (reviewId) =>
  handleApiRequest("delete", `${REVIEW_API_URL}/${reviewId}`);

// Report a Review
export const handleReportReview = async (reviewId) => {
  try {
    const response = await api.patch(`${REVIEW_API_URL}/report/${reviewId}`);
    toast.success(response.data.message);
  } catch (error) {
    console.error(
      "Error reporting review:",
      error.response?.data || error.message
    );
    toast.error("Failed to report the review. Please try again later.");
  }
};
