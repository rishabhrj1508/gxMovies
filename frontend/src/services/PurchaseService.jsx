import api from "./Api";
import { handleApiRequest } from "./ApiService";

const PURCHASE_API_URL = "/purchases";

// Create a Purchase
export const createPurchase = async (purchaseRequest) =>
  handleApiRequest("post", `${PURCHASE_API_URL}`, purchaseRequest);

// Get Purchases of a User
export const getPurchasesOfUser = async (userId) =>
  handleApiRequest("get", `${PURCHASE_API_URL}/users/${userId}`);

// Get All Movies Purchased by User
export const getAllMoviesPurchasedByUser = async (userId) =>
  handleApiRequest("get", `${PURCHASE_API_URL}/users/movies/${userId}`);

// Check if Movie is Already Purchased by the User
export const checkIfMoviePurchased = async (userId, movieId) =>
  handleApiRequest("get", `${PURCHASE_API_URL}/users/movies/check`, null, {
    userId,
    movieId,
  });

// Download Invoice for a Purchase
export const downloadInvoice = async (purchaseId, transactionId) => {
  try {
    const response = await api.get(
      `${PURCHASE_API_URL}/invoice/${purchaseId}`,
      {
        params: {
          transactionId,
        },
        responseType: "blob",
      } 
    );

    // Create a Blob URL for the downloaded PDF
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", `invoice_${transactionId}.pdf`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  } catch (error) {
    console.error("Error downloading invoice:", error);
    throw error;
  }
};
