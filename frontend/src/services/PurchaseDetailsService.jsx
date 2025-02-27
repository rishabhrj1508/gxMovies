import { handleApiRequest } from "./ApiService";

const PURCHASE_DETAIL_API_URL = "/purchasedetails";

// Get Purchase Details of a Specific Purchase
export const getPurchaseDetailsOfPurchase = async (purchaseId) => {
  const response = await handleApiRequest(
    "get",
    `${PURCHASE_DETAIL_API_URL}/purchase/${purchaseId}`
  );
  return response;
};
