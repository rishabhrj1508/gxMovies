/* eslint-disable no-unused-vars */
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Swal from "sweetalert2";
import PaymentModal from "../components/PaymentModal";
import UserNavbar from "../components/UserNavbar";
import { useAuth } from "../context/AuthContext";
import {
  clearCartOfUser,
  getAllItemsInCartOfUser,
  removeFromCartOfUser,
  removeMultipleCartItemsOfUser,
} from "../services/CartService";
import { createPurchase } from "../services/PurchaseService";
import toast, { Toaster } from "react-hot-toast";
import PacmanLoader from "react-spinners/PacmanLoader";
import { IterationCcw } from "lucide-react";

const Cart = () => {
  const [cartItems, setCartItems] = useState([]);
  const [selectedItems, setSelectedItems] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [totalPrice, setTotalPrice] = useState(0);
  const [showPaymentModal, setShowPaymentModal] = useState(false);
  const { user } = useAuth();
  const userId = user.userId;
  const navigate = useNavigate();
  const labelConfig = window.labelConfig;

  const fetchCartItems = async () => {
    try {
      setLoading(true);
      const response = await getAllItemsInCartOfUser(userId);
      setCartItems(response.reverse());
      calculateTotalPrice(response);
    } catch (error) {
      setError("Error fetching cart items.");
      console.error("Error fetching cart items", error);
    } finally {
      setLoading(false);
    }
  };

  const calculateTotalPrice = (items) => {
    const total = items.reduce((sum, item) => sum + item.movieDTO.price, 0);
    setTotalPrice(total);
  };

  const handleClearCart = async (userId) => {
    await clearCartOfUser(userId);
    toast.success("Cart Cleared", {
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
    fetchCartItems();
  };

  const handlePurchase = async (paymentMethod) => {
    try {
      const movieIds = cartItems.map((item) => item.movieDTO.movieId);
      const requestData = {
        userId,
        movieIds,
        totalPrice,
        paymentMethod: paymentMethod,
      };

      const response = await createPurchase(requestData);

      if (response.transactionId) {
        Swal.fire(
          "Payment SuccessFull",
          `Your Transaction Id is ${response.transactionId}`,
          "success"
        );
        await clearCartOfUser(userId);
        navigate("/user/orders");
      }
    } catch (error) {
      Swal.fire("Payment Failed!", "Please try Again", "error");
      console.error("Error during payment:", error);
    }
  };

  const handleRemoveFromCart = async (userId, movieId) => {
    const response = await removeFromCartOfUser(userId, movieId);
    toast.success("Removed from Cart", {
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
    fetchCartItems();
  };

  const handleSelectItem = (movieId) => {
    setSelectedItems((prev) => ({
      ...prev,
      [movieId]: !prev[movieId],
    }));
  };

  const handleRemoveSelectedMovies = async () => {
    const selectedMovies = Object.keys(selectedItems).filter(
      (movieId) => selectedItems[movieId]
    );

    if (selectedMovies.length === 0) {
      toast.error("No Items selected");
      return;
    }

    try {
      await removeMultipleCartItemsOfUser(userId, selectedMovies);
      toast.success("Selected Items removed successfully!");
      setSelectedItems({});
      fetchCartItems();
    } catch (error) {
      toast.error("Error : ", error);
    }
  };

  useEffect(() => {
    fetchCartItems();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [userId]);

  return (
    <>
      <UserNavbar />
      <Toaster position="top-right" />

      <div className="min-h-screen bg-gray-100 p-6">
        <h1 className="text-3xl font-bold text-center text-gray-800 mr-2">
          {
            <span className="bg-clip-text text-transparent bg-gradient-to-r from-red-600 to-purple-600">
              {loading && error && cartItems.length === 0 ? (
                <span> {labelConfig.cart.title} 🛒</span>
              ) : cartItems.length === 0 ? (
                <span> {labelConfig.cart.title} 🛒</span>
              ) : (
                ""
              )}
            </span>
          }{" "}
        </h1>

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
          <div className="text-red-500 text-center mt-4">{error}</div>
        )}

        {!loading && !error && cartItems.length === 0 && (
          <div className="flex justify-center items-center flex-col h-full">
            <div className="text-center max-w-sm w-full">
              <img
                src="/images/cartnew.png"
                alt="Empty Cart"
                className="mb-2 max-w-xs ml-4"
              />
              <h2 className="text-xl font-semibold text-gray-600 mb-2">
                {labelConfig.cart.emptyCartText}
              </h2>
              <p className="text-gray-500 mb-2">
                {labelConfig.cart.emptyCartSubText}
              </p>
              <button
                onClick={() => navigate("/user/home")}
                className="bg-blue-600 text-white py-2 px-6 rounded-lg hover:bg-blue-700 focus:outline-none transition duration-300"
              >
                {labelConfig.cart.buttonText.continueShoppingButton}
              </button>
            </div>
          </div>
        )}

        {!loading && !error && cartItems.length > 0 && (
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 mt-4">
            <div className="lg:col-span-2">
              <div className="flex justify-between items-center mb-6">
                <h2 className="text-3xl font-semibold text-gray-800">
                  {
                    <span className="bg-clip-text text-transparent bg-gradient-to-r from-red-600 to-purple-600">
                      {labelConfig.cart.title}
                    </span>
                  }{" "}
                  🛒
                </h2>

                <div>
                  {Object.values(selectedItems).some(
                    (isSelected) => isSelected
                  ) && (
                    <button
                      onClick={handleRemoveSelectedMovies}
                      className="bg-red-600 text-white py-2 px-4 rounded-lg hover:bg-red-700 focus:outline-none transition duration-300 mr-5"
                    >
                      Remove Selected
                    </button>
                  )}
                  <button
                    onClick={() => handleClearCart(userId)}
                    className="bg-red-600 text-white py-2 px-4 rounded-lg hover:bg-red-700 focus:outline-none transition duration-300"
                  >
                    {labelConfig.cart.buttonText.clearCartButton}
                  </button>
                </div>
              </div>

              <div className="space-y-4">
                {cartItems.map((item) => (
                  <div
                    key={item.cartId}
                    className="flex items-center justify-between bg-white gap-4 p-4 shadow-md rounded-lg"
                  >
                    <div className="flex items-center space-x-4">
                      {/* Checkbox */}
                      <input
                        type="checkbox"
                        checked={!!selectedItems[item.movieId]}
                        onChange={() => handleSelectItem(item.movieId)}
                        className="w-3 h-3 text-blue-600 focus:ring-blue-500 cursor-pointer"
                      />

                      {/* Movie Poster */}
                      <img
                        src={item.movieDTO.posterURL}
                        alt={item.movieDTO.title}
                        className="w-20 h-auto object-cover rounded-md"
                      />

                      {/* Movie Details */}
                      <div>
                        <h3 className="text-lg font-semibold text-gray-800 text-wrap">
                          {item.movieDTO.title}
                        </h3>
                        <p className="text-sm text-gray-500 text-wrap">
                          {item.movieDTO.description}
                        </p>
                        <span className="text-md font-semibold text-blue-600">
                          Rs.{item.movieDTO.price}
                        </span>
                      </div>
                    </div>

                    {/*Delete Button */}
                    <button
                      onClick={() =>
                        handleRemoveFromCart(item.userId, item.movieId)
                      }
                      className="p-2 rounded-full hover:bg-gray-100 transition"
                      title="Remove"
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        x="0px"
                        y="0px"
                        width="40"
                        height="40"
                        viewBox="0 0 100 100"
                      >
                        <path
                          fill="#f37e98"
                          d="M25,30l3.645,47.383C28.845,79.988,31.017,82,33.63,82h32.74c2.613,0,4.785-2.012,4.985-4.617L75,30"
                        ></path>
                        <path
                          fill="#f15b6c"
                          d="M65 38v35c0 1.65-1.35 3-3 3s-3-1.35-3-3V38c0-1.65 1.35-3 3-3S65 36.35 65 38zM53 38v35c0 1.65-1.35 3-3 3s-3-1.35-3-3V38c0-1.65 1.35-3 3-3S53 36.35 53 38zM41 38v35c0 1.65-1.35 3-3 3s-3-1.35-3-3V38c0-1.65 1.35-3 3-3S41 36.35 41 38zM77 24h-4l-1.835-3.058C70.442 19.737 69.14 19 67.735 19h-35.47c-1.405 0-2.707.737-3.43 1.942L27 24h-4c-1.657 0-3 1.343-3 3s1.343 3 3 3h54c1.657 0 3-1.343 3-3S78.657 24 77 24z"
                        ></path>
                        <path
                          fill="#1f212b"
                          d="M66.37 83H33.63c-3.116 0-5.744-2.434-5.982-5.54l-3.645-47.383 1.994-.154 3.645 47.384C29.801 79.378 31.553 81 33.63 81H66.37c2.077 0 3.829-1.622 3.988-3.692l3.645-47.385 1.994.154-3.645 47.384C72.113 80.566 69.485 83 66.37 83zM56 20c-.552 0-1-.447-1-1v-3c0-.552-.449-1-1-1h-8c-.551 0-1 .448-1 1v3c0 .553-.448 1-1 1s-1-.447-1-1v-3c0-1.654 1.346-3 3-3h8c1.654 0 3 1.346 3 3v3C57 19.553 56.552 20 56 20z"
                        ></path>
                        <path
                          fill="#1f212b"
                          d="M77,31H23c-2.206,0-4-1.794-4-4s1.794-4,4-4h3.434l1.543-2.572C28.875,18.931,30.518,18,32.265,18h35.471c1.747,0,3.389,0.931,4.287,2.428L73.566,23H77c2.206,0,4,1.794,4,4S79.206,31,77,31z M23,25c-1.103,0-2,0.897-2,2s0.897,2,2,2h54c1.103,0,2-0.897,2-2s-0.897-2-2-2h-4c-0.351,0-0.677-0.185-0.857-0.485l-1.835-3.058C69.769,20.559,68.783,20,67.735,20H32.265c-1.048,0-2.033,0.559-2.572,1.457l-1.835,3.058C27.677,24.815,27.351,25,27,25H23z"
                        ></path>
                        <path
                          fill="#1f212b"
                          d="M61.5 25h-36c-.276 0-.5-.224-.5-.5s.224-.5.5-.5h36c.276 0 .5.224.5.5S61.776 25 61.5 25zM73.5 25h-5c-.276 0-.5-.224-.5-.5s.224-.5.5-.5h5c.276 0 .5.224.5.5S73.776 25 73.5 25zM66.5 25h-2c-.276 0-.5-.224-.5-.5s.224-.5.5-.5h2c.276 0 .5.224.5.5S66.776 25 66.5 25zM50 76c-1.654 0-3-1.346-3-3V38c0-1.654 1.346-3 3-3s3 1.346 3 3v25.5c0 .276-.224.5-.5.5S52 63.776 52 63.5V38c0-1.103-.897-2-2-2s-2 .897-2 2v35c0 1.103.897 2 2 2s2-.897 2-2v-3.5c0-.276.224-.5.5-.5s.5.224.5.5V73C53 74.654 51.654 76 50 76zM62 76c-1.654 0-3-1.346-3-3V47.5c0-.276.224-.5.5-.5s.5.224.5.5V73c0 1.103.897 2 2 2s2-.897 2-2V38c0-1.103-.897-2-2-2s-2 .897-2 2v1.5c0 .276-.224.5-.5.5S59 39.776 59 39.5V38c0-1.654 1.346-3 3-3s3 1.346 3 3v35C65 74.654 63.654 76 62 76z"
                        ></path>
                        <path
                          fill="#1f212b"
                          d="M59.5 45c-.276 0-.5-.224-.5-.5v-2c0-.276.224-.5.5-.5s.5.224.5.5v2C60 44.776 59.776 45 59.5 45zM38 76c-1.654 0-3-1.346-3-3V38c0-1.654 1.346-3 3-3s3 1.346 3 3v35C41 74.654 39.654 76 38 76zM38 36c-1.103 0-2 .897-2 2v35c0 1.103.897 2 2 2s2-.897 2-2V38C40 36.897 39.103 36 38 36z"
                        ></path>
                      </svg>
                    </button>
                  </div>
                ))}
              </div>
            </div>

            {/* Order summary container */}
            <div className="lg:col-span-1 bg-white p-6 h-fit rounded-lg shadow-md sticky top-20">
              <h3 className="text-2xl font-semibold text-gray-800 mb-4">
                {labelConfig.cart.orderSummaryText}
              </h3>
              <div className="text-lg font-semibold mb-4">
                {labelConfig.cart.totalPriceText}:{" "}
                <span className="text-blue-600">
                  Rs.{totalPrice.toFixed(2)}
                </span>
              </div>
              <button
                onClick={() => setShowPaymentModal(true)}
                className="bg-green-600 text-white py-2 px-6 rounded-lg hover:bg-green-700 focus:outline-none transition duration-300 w-full mb-4"
              >
                {labelConfig.cart.buttonText.proceedToCheckOutButton}
              </button>
              <button
                onClick={() => navigate("/user/home")}
                className="bg-blue-600 text-white py-2 px-6 rounded-lg hover:bg-blue-700 focus:outline-none transition duration-300 w-full"
              >
                {labelConfig.cart.buttonText.continueShoppingButton}
              </button>
            </div>
          </div>
        )}
      </div>

      {/* Payment Modal */}
      {showPaymentModal && (
        <PaymentModal
          totalPrice={totalPrice}
          onClose={() => setShowPaymentModal(false)}
          onPayment={handlePurchase}
        />
      )}
    </>
  );
};

export default Cart;
