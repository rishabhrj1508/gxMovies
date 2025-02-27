/* eslint-disable no-unused-vars */
/* eslint-disable react/prop-types */
import React, { useState } from "react";

const PaymentModal = ({ totalPrice, onClose, onPayment }) => {
  const [paymentMethod, setPaymentMethod] = useState("");
  const [isProcessingPayment, setIsProcessingPayment] = useState(false);
  const [errors, setErrors] = useState({});
  const [formData, setFormData] = useState({
    cardNumber: "",
    cardholderName: "",
    expiryDate: "",
    cvv: "",
    upiId: "",
    bankName: "",
    accountNumber: "",
  });

  const handleInputChange = (e) => {
    const { id, value } = e.target;
    setFormData((prevData) => ({ ...prevData, [id]: value }));
  };

  const validateFields = () => {
    const newErrors = {};
    if (paymentMethod === "Credit Card") {
      if (!formData.cardNumber || formData.cardNumber.length !== 16) {
        newErrors.cardNumber = "Card number must be 16 digits.";
      }
      if (!formData.cardholderName) {
        newErrors.cardholderName = "Cardholder name is required.";
      }
      if (!formData.expiryDate) {
        newErrors.expiryDate = "Expiry date is required.";
      }
      if (!formData.cvv || formData.cvv.length !== 3) {
        newErrors.cvv = "CVV must be 3 digits.";
      }
    } else if (paymentMethod === "UPI") {
      if (!formData.upiId || !formData.upiId.endsWith("@paytm")) {
        newErrors.upiId = "UPI ID must end with @paytm.";
      } else if (formData.upiId.split("@")[0].length < 3) {
        newErrors.upiId =
          "UPI ID must have at least 3 characters before @paytm";
      }
    } else if (paymentMethod === "Net Banking") {
      if (!formData.bankName) {
        newErrors.bankName = "Bank name is required.";
      }
      if (!formData.accountNumber || formData.accountNumber.length !== 16) {
        newErrors.accountNumber = "Account number must be 16 digits.";
      }
    }
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = () => {
    if (!paymentMethod) {
      alert("Please select a payment method.");
      return;
    }
    if (!validateFields()) return;
    setIsProcessingPayment(true);
    setTimeout(() => {
      onPayment(paymentMethod);
      setIsProcessingPayment(false);
    }, 2000);
  };

  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
      <div className="bg-white p-2 rounded-xl shadow-lg w-96 relative">
        <div className="flex justify-between">
          <img src="/images/image.png" alt="Logo" className="h-12 ml-2" />
          <button
            className="text-xl text-gray-600 hover:text-black mr-2 absolute right-0 p-1"
            onClick={onClose}
          >
            &times;
          </button>
        </div>
        <div className="space-y-2 p-2">
          <h2 className="text-center text-2xl font-semibold text-gray-700 mb-6">
            Total Price:{" "}
            <span className="text-blue-600 text-2xl font-semibold">
              Rs.{totalPrice.toFixed(2)}
            </span>
          </h2>
          <h2 className="text-l font-semibold">Payment Options</h2>
          <div
            className={`flex items-center gap-4 p-3 border rounded-lg cursor-pointer transition ${
              paymentMethod === "Credit Card"
                ? "bg-blue-100 border-blue-500"
                : "hover:bg-gray-100"
            }`}
            onClick={() => setPaymentMethod("Credit Card")}
          >
            <img
              src="/images/mastercard.jpg"
              alt="Credit Card"
              className="w-10"
            />
            <span className="text-gray-700 font-medium">Credit Card</span>
          </div>
          <div
            className={`flex items-center gap-4 p-3 border rounded-lg cursor-pointer transition ${
              paymentMethod === "UPI"
                ? "bg-blue-100 border-blue-500"
                : "hover:bg-gray-100"
            }`}
            onClick={() => setPaymentMethod("UPI")}
          >
            <img src="/images/paytm.png" alt="UPI" className="w-10" />
            <span className="text-gray-700 font-medium">UPI</span>
          </div>
          <div
            className={`flex items-center gap-4 p-3 border rounded-lg cursor-pointer transition ${
              paymentMethod === "Net Banking"
                ? "bg-blue-100 border-blue-500"
                : "hover:bg-gray-100"
            }`}
            onClick={() => setPaymentMethod("Net Banking")}
          >
            <img src="/images/visa.jpg" alt="Net Banking" className="w-10" />
            <span className="text-gray-700 font-medium">Net Banking</span>
          </div>
        </div>
        {paymentMethod && (
          <div className="mt-2 space-y-2">
            {paymentMethod === "Credit Card" && (
              <>
                <input
                  type="text"
                  id="cardNumber"
                  value={formData.cardNumber}
                  onChange={handleInputChange}
                  placeholder="Card Number"
                  className="w-full p-2 border rounded-lg"
                  required
                  maxLength={16}
                />
                {errors.cardNumber && (
                  <span className="text-red-500 text-sm">
                    {errors.cardNumber}
                  </span>
                )}
                <input
                  type="text"
                  id="cardholderName"
                  value={formData.cardholderName}
                  onChange={handleInputChange}
                  placeholder="Cardholder Name"
                  className="w-full p-2 border rounded-lg"
                  required
                />
                {errors.cardholderName && (
                  <span className="text-red-500 text-sm">
                    {errors.cardholderName}
                  </span>
                )}
                <div className="flex gap-4">
                  <input
                    type="date"
                    id="expiryDate"
                    value={formData.expiryDate}
                    onChange={handleInputChange}
                    placeholder="Expiry Date (MM/YY)"
                    className="w-1/2 p-2 border rounded-lg"
                    required
                  />
                  {errors.expiryDate && (
                    <span className="text-red-500 text-sm">
                      {errors.expiryDate}
                    </span>
                  )}
                  <input
                    type="password"
                    id="cvv"
                    value={formData.cvv}
                    onChange={handleInputChange}
                    placeholder="CVV"
                    className="w-1/2 p-2 border rounded-lg"
                    required
                    maxLength={3}
                  />
                  {errors.cvv && (
                    <span className="text-red-500 text-sm">{errors.cvv}</span>
                  )}
                </div>
              </>
            )}
            {paymentMethod === "UPI" && (
              <>
                <input
                  type="text"
                  id="upiId"
                  value={formData.upiId}
                  onChange={handleInputChange}
                  placeholder="UPI ID"
                  className="w-full p-2 border rounded-lg"
                  required
                />
                {errors.upiId && (
                  <span className="text-red-500 text-sm ml-1">{errors.upiId}</span>
                )}
              </>
            )}
            {paymentMethod === "Net Banking" && (
              <>
                <input
                  type="text"
                  id="bankName"
                  value={formData.bankName}
                  onChange={handleInputChange}
                  placeholder="Bank Name"
                  className="w-full p-2 border rounded-lg mb-2"
                  required
                />
                {errors.bankName && (
                  <span className="text-red-500 text-sm">
                    {errors.bankName}
                  </span>
                )}
                <input
                  type="text"
                  id="accountNumber"
                  value={formData.accountNumber}
                  onChange={handleInputChange}
                  placeholder="Account Number"
                  className="w-full p-2 border rounded-lg"
                  required
                  maxLength={16}
                />
                {errors.accountNumber && (
                  <span className="text-red-500 text-sm">
                    {errors.accountNumber}
                  </span>
                )}
              </>
            )}
          </div>
        )}
        <button
          onClick={handleSubmit}
          className="bg-green-500 text-white py-2 px-6 rounded-lg w-full mb-6 mt-6 hover:bg-green-600 transition"
          disabled={isProcessingPayment}
        >
          {isProcessingPayment ? "Processing..." : "Pay Now"}
        </button>
      </div>
    </div>
  );
};

export default PaymentModal;
