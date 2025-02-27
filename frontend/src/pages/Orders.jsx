/* eslint-disable react-hooks/exhaustive-deps */
/* eslint-disable no-unused-vars */
import { faChevronDown, faDownload } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import UserNavbar from "../components/UserNavbar";
import { useAuth } from "../context/AuthContext";
import { getPurchaseDetailsOfPurchase } from "../services/PurchaseDetailsService";
import {
  downloadInvoice,
  getPurchasesOfUser,
} from "../services/PurchaseService";
import PacmanLoader from "react-spinners/PacmanLoader";
import toast, { Toaster } from "react-hot-toast";

const Orders = () => {
  const [purchases, setPurchases] = useState([]);
  const [expandedPurchaseId, setExpandedPurchaseId] = useState(null);
  const [purchaseDetails, setPurchaseDetails] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const { user } = useAuth();
  const userId = user.userId;
  const navigate = useNavigate();
  const labelConfig = window.labelConfig;

  useEffect(() => {
    fetchPurchases();
  }, [userId]);

  const fetchPurchases = async () => {
    try {
      const response = await getPurchasesOfUser(userId);
      setPurchases(response);
    } catch (err) {
      console.error("Error fetching purchases", err);
      setError(labelConfig.orders.errors.fetchPurchaseError);
    } finally {
      setLoading(false);
    }
  };

  const fetchPurchaseDetails = async (purchaseId) => {
    if (expandedPurchaseId === purchaseId) {
      // If the purchase is already expanded, close it
      setExpandedPurchaseId(null);
      return;
    }

    try {
      const response = await getPurchaseDetailsOfPurchase(purchaseId);
      console.log("fetched purchase details : ", response);

      setPurchaseDetails((prevDetails) => ({
        ...prevDetails,
        [purchaseId]: response,
      }));
      setExpandedPurchaseId(purchaseId);
    } catch (err) {
      toast.error(labelConfig.orders.errors.fetchPurchaseDetailError);
      console.error("Error fetching purchase details", err);
      // setError(labelConfig.orders.errors.fetchPurchaseDetailError);
    }
  };

  const handleDownloadInvoice = async (purchaseId, transactionId) => {
    try {
      await downloadInvoice(purchaseId, transactionId);
    } catch (err) {
      console.error("Error downloading invoice:", err);
      setError(labelConfig.orders.errors.downloadInvoiceError);
    }
  };

  return (
    <>
      <UserNavbar />
      <div className="bg-gray-100 min-h-screen text-gray-800">
        <div className="max-w-6xl mx-auto p-6">
          <h2 className="text-3xl font-semibold mb-4 text-center">
            🎟️{" "}
            {
              <span className="bg-clip-text text-transparent bg-gradient-to-r from-red-600 to-purple-600">
                {labelConfig.orders.title}
              </span>
            }{" "}
            🎥✨
          </h2>
          {!error && !loading && purchases.length ? (
            <p className="text-lg p-2 mb-4 text-center">
              {labelConfig.orders.subTitle} 👉🚀
              <button
                className="ml-2 inline-flex items-center text-white p-2 bg-red-600 hover:bg-red-700 transition-all duration-300 rounded-md"
                onClick={() => navigate(`/user/library`)}
              >
                {labelConfig.orders.buttonText.goToLibraryButton}
              </button>
            </p>
          ) : (
            ""
          )}

          {loading && (
            <div className="flex justify-center items-center flex-col">
              <PacmanLoader
                size={20}
                color="red"
                className="mt-10"
                loading={loading}
              />
            </div>
          )}

          {error && !loading && (
            <div className="text-red-500 text-center mt-10">{error}</div>
          )}

          {!loading && !error && purchases.length === 0 && (
            <div className="flex justify-center items-center flex-col h-full">
              <h2 className="text-xl font-semibold text-gray-600 mb-2">
                {labelConfig.orders.noOrdersText}
              </h2>
            </div>
          )}

          {!loading && !error && purchases.length > 0 && (
            <div className="space-y-6">
              {purchases.map((purchase) => (
                <div
                  key={purchase.purchaseId}
                  className="bg-white border border-gray-300 rounded-lg p-6 shadow-lg hover:shadow-xl transition-shadow duration-300"
                >
                  <div
                    className="flex justify-between items-center cursor-pointer"
                    onClick={() => fetchPurchaseDetails(purchase.purchaseId)}
                  >
                    <div>
                      <p className="text-xl font-semibold">
                        Transaction ID: {purchase.transactionId}
                      </p>
                      <p className="text-sm text-gray-600">
                        Date:{" "}
                        {new Date(purchase.purchaseDate).toLocaleDateString()}
                      </p>
                    </div>
                    <div className="flex items-center">
                      <p className="text-lg font-semibold text-red-600">
                        Total: ₹{purchase.totalPrice}
                      </p>
                      <FontAwesomeIcon
                        icon={faChevronDown}
                        className={`ml-4 transform transition-transform duration-300 ${
                          expandedPurchaseId === purchase.purchaseId
                            ? "rotate-180"
                            : ""
                        }`}
                      />
                    </div>
                  </div>

                  <div className="mt-4 flex justify-end">
                    <button
                      onClick={() =>
                        handleDownloadInvoice(
                          purchase.purchaseId,
                          purchase.transactionId
                        )
                      }
                      className="flex items-center gap-2 bg-blue-600 text-white px-4 py-2 rounded-md shadow-md hover:bg-blue-500 transition-all duration-300"
                    >
                      <FontAwesomeIcon icon={faDownload} />
                      {labelConfig.orders.buttonText.downloadInvoiceButton}
                    </button>
                  </div>

                  <div
                    className={`overflow-hidden transition-all duration-500 ease-in-out ${
                      expandedPurchaseId === purchase.purchaseId
                        ? "py-4 opacity-100"
                        : "max-h-auto py-0 opacity-0"
                    }`}
                  >
                    {expandedPurchaseId === purchase.purchaseId && (
                      <div className="space-y-4 mt-4">
                        {purchaseDetails[purchase.purchaseId]?.map((detail) => (
                          <div
                            key={detail.purchaseDetailId}
                            className="flex items-center gap-4 p-4 bg-gray-100 rounded-lg shadow-md transition-all duration-300"
                          >
                            <img
                              src={detail.movieDTO.posterURL}
                              alt={detail.movieDTO.title}
                              className="w-20 h-20 object-cover rounded-md"
                            />
                            <div>
                              <h3 className="text-lg font-semibold">
                                {detail.movieDTO.title}
                              </h3>
                              <p className="text-sm text-gray-500">
                                {detail.movieDTO.description}
                              </p>
                              <p className="text-sm font-semibold">
                                Price: ₹{detail.movieDTO.price}
                              </p>
                            </div>
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
      <Toaster position="top-right" />
    </>
  );
};

export default Orders;
