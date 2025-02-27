/* eslint-disable no-unused-vars */
import { faTrash } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import React, { useEffect, useState } from "react";
import ReactPaginate from "react-paginate";
import Swal from "sweetalert2";
import { deleteReview, getReportedReviews } from "../services/ReviewService";

const ReviewManagement = () => {
  const [reportedReviews, setReportedReviews] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [reviewsPerPage] = useState(4);
  const [searchMovieTerm, setSearchMovieTerm] = useState("");
  const [searchUsernameTerm, setSearchUsernameTerm] = useState("");
  const labelConfig = window.labelConfig;

  useEffect(() => {
    const fetchReportedReviews = async () => {
      setLoading(true);
      try {
        const response = await getReportedReviews();
        setReportedReviews(response.reverse());
      } catch (error) {
        setError("Failed to fetch reported reviews.");
        console.error("Error fetching reported reviews:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchReportedReviews();
  }, []);

  const handleDeleteReview = async (reviewId) => {
    try {
      const result = await Swal.fire({
        title: labelConfig.reviewManagement.swal.deleteConfirmTitle,
        text: labelConfig.reviewManagement.swal.deleteConfirmText,
        icon: labelConfig.reviewManagement.swal.deleteConfirmIcon,
        showCancelButton: true,
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
        confirmButtonText:
          labelConfig.reviewManagement.swal.deleteConfirmButtonText,
      });

      if (result.isConfirmed) {
        await deleteReview(reviewId);
        Swal.fire(
          labelConfig.reviewManagement.swal.deleteSuccessTitle,
          labelConfig.reviewManagement.swal.deleteSuccessText,
          "success"
        );
        setReportedReviews(
          reportedReviews.filter((review) => review.reviewId !== reviewId)
        );
      } else {
        Swal.fire(
          labelConfig.reviewManagement.swal.deleteCancelledTitle,
          labelConfig.reviewManagement.swal.deleteCancelledText,
          "info"
        );
      }
    } catch (error) {
      console.error("Error deleting review:", error);
    }
  };

  const handlePageClick = ({ selected }) => {
    setCurrentPage(selected);
  };

  const offset = currentPage * reviewsPerPage;
  const filteredReviews = reportedReviews.filter(
    (review) =>
      review.moviename.toLowerCase().includes(searchMovieTerm.toLowerCase()) &&
      review.username.toLowerCase().includes(searchUsernameTerm.toLowerCase())
  );
  const currentReviews = filteredReviews.slice(offset, offset + reviewsPerPage);
  const pageCount = Math.ceil(filteredReviews.length / reviewsPerPage);

  return (
    <div className="container p-6 min-h-screen bg-gray-100 flex flex-col">
      <h2 className="text-3xl font-bold mb-6 text-center text-gray-900">
        {
          <span className="bg-clip-text text-transparent bg-gradient-to-r from-red-600 to-purple-600">
            {" "}
            {labelConfig.reviewManagement.title}
          </span>
        }
      </h2>

      <div className="mb-4 flex flex-col md:flex-row justify-center items-center p-2">
        <input
          type="text"
          placeholder={labelConfig.reviewManagement.searchMoviePlaceholder}
          className="border border-gray-300 mr-2 px-3 py-2 rounded-md w-full md:w-1/4 bg-white text-gray-900 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
          value={searchMovieTerm}
          onChange={(e) => setSearchMovieTerm(e.target.value)}
        />
        <input
          type="text"
          placeholder={labelConfig.reviewManagement.searchUsernamePlaceholder}
          className="border border-gray-300  mr-2 px-3 py-2 rounded-md w-full md:w-1/4 bg-white text-gray-900 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
          value={searchUsernameTerm}
          onChange={(e) => setSearchUsernameTerm(e.target.value)}
        />
      </div>

      {loading ? (
        <div className="text-center text-gray-700 flex-grow flex items-start justify-center">
          {labelConfig.reviewManagement.loadingText}
        </div>
      ) : error ? (
        <span className="text-center text-red-500">{error}</span>
      ) : filteredReviews.length === 0 ? (
        <p className="text-center text-red-500 text-lg flex-grow flex items-start justify-center">
          {labelConfig.reviewManagement.noReportsText}
        </p>
      ) : (
        <div className="overflow-x-auto bg-white rounded-lg shadow-lg flex-grow">
          <table className="table-fixed w-full h-full text-gray-900 rounded-lg overflow-hidden">
            <thead className="bg-gray-200 text-gray-700">
              <tr>
                <th className="px-6 py-3 text-center text-sm font-semibold w-1.5/12">
                  {labelConfig.reviewManagement.tableHeaders.reviewId}
                </th>
                <th className="px-6 py-3 text-center text-sm font-semibold w-2/12">
                  {labelConfig.reviewManagement.tableHeaders.username}
                </th>
                <th className="px-6 py-3 text-center text-sm font-semibold w-2/12">
                  {labelConfig.reviewManagement.tableHeaders.movieTitle}
                </th>
                <th className="px-6 py-3 text-center text-sm font-semibold w-4/12">
                  {labelConfig.reviewManagement.tableHeaders.reviewText}
                </th>
                <th className="px-6 py-3 text-center text-sm font-semibold w-2.5/12">
                  {labelConfig.reviewManagement.tableHeaders.actions}
                </th>
              </tr>
            </thead>
            <tbody>
              {currentReviews.map((review) => (
                <tr
                  key={review.reviewId}
                  className="border-b border-gray-200 hover:bg-gray-50 transition"
                >
                  <td className="px-6 py-4 text-sm text-center text-gray-700">
                    {review.reviewId}
                  </td>
                  <td
                    className="px-6 py-4 text-sm text-center text-gray-700 truncate whitespace-nowrap overflow-hidden text-ellipsis"
                    title={review.username}
                  >
                    {review.username}
                  </td>
                  <td
                    className="px-6 py-4 text-sm text-center text-gray-700 truncate whitespace-nowrap overflow-hidden text-ellipsis"
                    title={review.moviename}
                  >
                    {review.moviename}
                  </td>
                  <td
                    className="px-6 py-4 text-sm text-center text-gray-700 truncate whitespace-nowrap overflow-hidden text-ellipsis"
                    title={review.reviewText}
                  >
                    {review.reviewText}
                  </td>
                  <td className="px-6 py-4 text-sm text-center space-x-2">
                    <button
                      onClick={() => handleDeleteReview(review.reviewId)}
                      className="bg-red-500 text-white py-2 px-2 rounded-lg hover:bg-red-600 transition transform hover:scale-105 m-1 items-center justify-center"
                    >
                      <FontAwesomeIcon icon={faTrash} className="mr-1" />
                      {labelConfig.reviewManagement.buttonText.deleteButton}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          {currentReviews.length > 0 && pageCount > 1 && (
            <ReactPaginate
              previousLabel={"Previous"}
              nextLabel={"Next"}
              breakLabel={"..."}
              breakClassName={"text-gray-500"}
              pageCount={pageCount}
              marginPagesDisplayed={1}
              pageRangeDisplayed={3}
              onPageChange={handlePageClick}
              containerClassName={
                "flex items-center justify-center space-x-1 mt-2"
              }
              activeClassName={"bg-red-600 rounded-lg text-white"}
              pageLinkClassName={
                "text-gray-800 border border-gray-300 rounded-lg px-3 py-2 transition duration-300 hover:bg-red-500 hover:text-white block w-full text-center"
              }
              previousClassName={`border border-gray-300 rounded-lg px-2 hover:bg-red-500 py-2 transition duration-300 ${
                currentPage === 0
                  ? "cursor-not-allowed opacity-50"
                  : "hover:bg-gray-200"
              }`}
              nextClassName={`border border-gray-300 rounded-lg px-2 py-2 hover:bg-red-500 transition duration-300 ${
                currentPage === pageCount - 1
                  ? "cursor-not-allowed opacity-50"
                  : "hover:bg-gray-200"
              }`}
              disabledClassName={"pointer-events-none opacity-50"}
            />
          )}
        </div>
      )}
    </div>
  );
};

export default ReviewManagement;
