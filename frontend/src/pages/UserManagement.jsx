/* eslint-disable react-hooks/exhaustive-deps */
/* eslint-disable no-unused-vars */
import { faBan, faUnlock } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import React, { useEffect, useState } from "react";
import ReactPaginate from "react-paginate";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import Swal from "sweetalert2";
import { blockUser, getAllUsers, unBlockUser } from "../services/UserService";

const UserManagement = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [usersPerPage] = useState(5);
  const [searchTerm, setSearchTerm] = useState("");
  const [sortByStatus, setSortByStatus] = useState("");
  const labelConfig = window.labelConfig;

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const response = await getAllUsers();
      setUsers(response);
    } catch (error) {
      setError("Failed to fetch Users.");
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const handleBlockUser = async (userId) => {
    try {
      const response = await blockUser(userId);
      fetchUsers();
      Swal.fire(
        labelConfig.userManagement.swal.blockSuccessTitle,
        labelConfig.userManagement.swal.blockSuccessText,
        labelConfig.userManagement.swal.successIcon
      );
      return response?.data?.message;
    } catch (error) {
      toast.error(error);
    }
  };

  const handleUnBlockUser = async (userId) => {
    try {
      const response = await unBlockUser(userId);
      fetchUsers();
      Swal.fire(
        labelConfig.userManagement.swal.unblockSuccessTitle,
        labelConfig.userManagement.swal.unblockSuccessText,
        labelConfig.userManagement.swal.successIcon
      );
      return response?.data?.message;
    } catch (error) {
      toast.error(labelConfig.userManagement.toastMessages.unblockError);
      throw error;
    }
  };

  const confirmBlockUser = (userId) => {
    Swal.fire({
      title: labelConfig.userManagement.swal.blockConfirmTitle,
      text: labelConfig.userManagement.swal.blockConfirmText,
      icon: labelConfig.userManagement.swal.blockConfirmIcon,
      showCancelButton: true,
      confirmButtonColor: "#d33",
      cancelButtonColor: "#3085d6",
      confirmButtonText: labelConfig.userManagement.swal.blockConfirmButtonText,
    }).then((result) => {
      if (result.isConfirmed) {
        handleBlockUser(userId);
      }
    });
  };

  const confirmUnBlockUser = (userId) => {
    Swal.fire({
      title: labelConfig.userManagement.swal.unblockConfirmTitle,
      text: labelConfig.userManagement.swal.unblockConfirmText,
      icon: labelConfig.userManagement.swal.unblockConfirmIcon,
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText:
        labelConfig.userManagement.swal.unblockConfirmButtonText,
    }).then((result) => {
      if (result.isConfirmed) {
        handleUnBlockUser(userId);
      }
    });
  };

  const handlePageClick = ({ selected }) => {
    setCurrentPage(selected);
  };

  const handleSearchChange = (e) => {
    setSearchTerm(e.target.value);
    setCurrentPage(0);
  };

  const handleSortChange = (e) => {
    setSortByStatus(e.target.value);
    setCurrentPage(0);
  };

  const offset = currentPage * usersPerPage;
  const filteredUsers = users
    .filter((user) =>
      user.fullName.toLowerCase().includes(searchTerm.toLowerCase())
    )
    .filter((user) => (sortByStatus ? user.status === sortByStatus : true));

  const currentUsers = filteredUsers.slice(offset, offset + usersPerPage);
  const pageCount = Math.ceil(filteredUsers.length / usersPerPage);

  return (
    <div className="min-h-screen container p-4 bg-gray-100 flex flex-col">
      <h2 className="text-3xl font-bold mb-2 text-center text-gray-900">
        {
          <span className="bg-clip-text text-transparent bg-gradient-to-r from-red-600 to-purple-600">
            {" "}
            {labelConfig.userManagement.title}
          </span>
        }
      </h2>
      <div className="mb-4 flex flex-col md:flex-row justify-center items-center p-2">
        <input
          type="text"
          placeholder={labelConfig.userManagement.searchPlaceholder}
          className="border border-gray-300 mr-2 px-3 py-2 rounded-md w-full md:w-1/4 bg-white text-gray-900 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
          value={searchTerm}
          onChange={handleSearchChange}
        />
        <select
          className="border border-gray-300 px-3 py-2.5 rounded-md w-full md:w-1/4 bg-white text-gray-900 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-blue-500 md:mt-0"
          value={sortByStatus}
          onChange={handleSortChange}
        >
          <option value="">{labelConfig.userManagement.sortPlaceholder}</option>
          <option value={labelConfig.userManagement.statusLabels.active}>
            {labelConfig.userManagement.statusLabels.active}
          </option>
          <option value={labelConfig.userManagement.statusLabels.blocked}>
            {labelConfig.userManagement.statusLabels.blocked}
          </option>
        </select>
      </div>
      {loading ? (
        <div className="text-center text-gray-600 flex-grow flex items-start justify-center">
          {labelConfig.userManagement.loadingText}
        </div>
      ) : error ? (
        <span className="text-center text-red-500">{error}</span>
      ) : filteredUsers.length === 0 ? (
        <p className="text-center text-gray-500 text-lg flex-grow flex items-start justify-center">
          {labelConfig.userManagement.noUsersText}
        </p>
      ) : (
        <div className="overflow-x-auto bg-white rounded-lg shadow-lg flex-grow">
          <table className="table-fixed w-full h-full text-gray-900 rounded-lg overflow-hidden">
            <thead className="bg-gray-200 text-gray-700">
              <tr>
                <th className="w-1/6 px-6 py-3 text-center text-sm font-semibold">
                  {labelConfig.userManagement.tableHeaders.userId}
                </th>
                <th className="w-1/6 px-6 py-3 text-center text-sm font-semibold">
                  {labelConfig.userManagement.tableHeaders.name}
                </th>
                <th className="w-2/6 px-6 py-3 text-center text-sm font-semibold">
                  {labelConfig.userManagement.tableHeaders.email}
                </th>
                <th className="w-1/6 px-6 py-3 text-center text-sm font-semibold">
                  {labelConfig.userManagement.tableHeaders.status}
                </th>
                <th className="w-1/6 px-6 py-3 text-center text-sm font-semibold">
                  {labelConfig.userManagement.tableHeaders.actions}
                </th>
              </tr>
            </thead>
            <tbody>
              {currentUsers.map((user) => (
                <tr
                  key={user.userId}
                  className="border-b border-gray-200 hover:bg-gray-50 transition"
                >
                  <td className="px-6 py-4 text-sm text-center text-gray-700">
                    {user.userId}
                  </td>
                  <td
                    className="px-6 py-4 text-sm text-center text-gray-700 truncate whitespace-nowrap overflow-hidden text-ellipsis"
                    title={user.fullName}
                  >
                    {user.fullName}
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-700 text-center">
                    {user.email}
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-700 text-center">
                    <span
                      className={`px-3 py-1 rounded-full text-sm font-semibold ${
                        user.status ===
                        labelConfig.userManagement.statusLabels.active
                          ? "bg-green-100 text-green-600"
                          : "bg-red-100 text-red-600"
                      }`}
                    >
                      {user.status}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-sm text-center">
                    {user.status ===
                      labelConfig.userManagement.statusLabels.active && (
                      <button
                        onClick={() => confirmBlockUser(user.userId)}
                        className="bg-red-500 text-white py-2 px-3 rounded-lg hover:bg-red-600 transition transform hover:scale-105"
                      >
                        <FontAwesomeIcon icon={faBan} className="mr-1" />
                        {labelConfig.userManagement.buttonText.blockButton}
                      </button>
                    )}
                    {user.status ===
                      labelConfig.userManagement.statusLabels.blocked && (
                      <button
                        onClick={() => confirmUnBlockUser(user.userId)}
                        className="bg-green-500 text-white py-2 px-3 rounded-lg hover:bg-green-600 transition transform hover:scale-105"
                      >
                        <FontAwesomeIcon icon={faUnlock} className="mr-1" />
                        {labelConfig.userManagement.buttonText.unblockButton}
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {filteredUsers.length > 0 && pageCount > 1 && (
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
      )}{" "}
      <ToastContainer />{" "}
    </div>
  );
};
export default UserManagement;
