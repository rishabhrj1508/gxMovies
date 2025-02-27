/* eslint-disable no-unused-vars */
import React, { useEffect, useState } from "react";
import ReactPaginate from "react-paginate";
import MovieModal from "../components/MovieModal";
import { getMovies } from "../services/MovieService";
import { FaEdit, FaPlus } from "react-icons/fa";
import Swal from "sweetalert2";
import toast from "react-hot-toast";

const MovieManagement = () => {
  const [movies, setMovies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [search, setSearch] = useState("");
  const [filterGenre, setFilterGenre] = useState("");
  const [filterStatus, setFilterStatus] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [selectedMovieId, setSelectedMovieId] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [moviesPerPage] = useState(5);
  const labelConfig = window.labelConfig;

  useEffect(() => {
    fetchMovies();
  }, [search, filterGenre, filterStatus]);

  const fetchMovies = async () => {
    setLoading(true);

    try {
      const data = await getMovies();
      setMovies(data.reverse());
      setCurrentPage(0);
    } catch (error) {
      setError("Sorry we could'nt fetch the movies . Please try again..");
      toast.error(error);
    } finally {
      setLoading(false);
    }
  };

  const openModal = (movieId = null) => {
    setSelectedMovieId(movieId);
    setShowModal(true);
  };

  const closeModal = () => {
    setShowModal(false);
    setSelectedMovieId(null);
    fetchMovies();
  };

  const handlePageClick = ({ selected }) => {
    setCurrentPage(selected);
  };

  const handleSearchChange = (e) => {
    setSearch(e.target.value);
    setCurrentPage(0);
  };

  const handleFilterGenreChange = (e) => {
    setFilterGenre(e.target.value);
    setCurrentPage(0);
  };

  const handleFilterStatusChange = (e) => {
    setFilterStatus(e.target.value);
    setCurrentPage(0);
  };

  const handleUpdateMovie = (updatedMovie) => {
    if (selectedMovieId) {
      // Update an existing movie
      setMovies((prevMovies) =>
        prevMovies.map((movie) =>
          movie.movieId === updatedMovie.movieId ? updatedMovie : movie
        )
      );
      Swal.fire(
        labelConfig.movieManagement.swal.updateTitle,
        labelConfig.movieManagement.swal.updateSuccess,
        "success"
      );
    } else {
      // Add a new movie
      setMovies((prevMovies) => [...prevMovies, updatedMovie]);
      Swal.fire(
        labelConfig.movieManagement.swal.addTitle,
        labelConfig.movieManagement.swal.addSuccess,
        "success"
      );
    }

    closeModal();
  };

  const offset = currentPage * moviesPerPage;
  const filteredMovies = movies
    .filter((movie) => movie.title.toLowerCase().includes(search.toLowerCase()))
    .filter((movie) => (filterGenre ? movie.genre === filterGenre : true))
    .filter((movie) => (filterStatus ? movie.status === filterStatus : true));

  const currentMovies = filteredMovies.slice(offset, offset + moviesPerPage);
  const pageCount = Math.ceil(filteredMovies.length / moviesPerPage);

  return (
    <div className="min-h-screen container p-4 bg-gray-100 flex flex-col">
      <h2 className="text-3xl font-bold mb-4 text-center text-gray-900">
        <span className="bg-clip-text text-transparent bg-gradient-to-r from-red-600 to-purple-600">
          {labelConfig.movieManagement.title}
        </span>
      </h2>

      <div className="mb-4 flex flex-col md:flex-row justify-center items-center gap-4">
        <input
          type="text"
          placeholder={labelConfig.movieManagement.searchPlaceholder}
          className="border border-gray-300 px-3 py-2 rounded-md w-full md:w-1/4 bg-white text-gray-900 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
          value={search}
          onChange={handleSearchChange}
        />
        <select
          className="border border-gray-300 px-3 py-2 rounded-md w-full md:w-1/4 bg-white text-gray-900 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
          value={filterGenre}
          onChange={handleFilterGenreChange}
        >
          <option value="">
            {labelConfig.movieManagement.filterGenrePlaceholder}
          </option>
          {[...new Set(movies.map((movie) => movie.genre))].map((genre) => (
            <option key={genre} value={genre}>
              {genre}
            </option>
          ))}
        </select>
        <select
          className="border border-gray-300 px-3 py-2 rounded-md w-full md:w-1/4 bg-white text-gray-900 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
          value={filterStatus}
          onChange={handleFilterStatusChange}
        >
          <option value="">
            {labelConfig.movieManagement.filterStatusPlaceholder}
          </option>
          <option value={labelConfig.movieManagement.statusLabels.available}>
            {labelConfig.movieManagement.statusLabels.available}
          </option>
          <option value={labelConfig.movieManagement.statusLabels.unavailable}>
            {labelConfig.movieManagement.statusLabels.unavailable}
          </option>
        </select>

        <div className="relative group">
          <button
            onClick={() => openModal()}
            className="bg-blue-600 text-white px-3 py-3 rounded-md shadow-lg hover:bg-blue-500 transition transform hover:scale-105 flex items-center space-x-2"
          >
            <FaPlus />
            <div className="absolute left-full top-1/2 transform -translate-y-1/2 mb-2 hidden group-hover:block bg-gray-800 text-white text-xs rounded py-3 w-20 z-10">
              <p>{labelConfig.movieManagement.addMovieTooltip}</p>
            </div>
          </button>
        </div>
      </div>

      {loading ? (
        <p className="text-center text-gray-600 flex-grow flex items-center justify-center">
          {labelConfig.movieManagement.loadingText}
        </p>
      ) : error ? (
        <span className="text-center text-red-500">{error}</span>
      ) : filteredMovies.length === 0 ? (
        <p className="text-center text-gray-500 text-lg flex-grow flex items-start justify-center">
          {"No Movies to display."}
        </p>
      ) : (
        <div className="overflow-x-auto text-wrap bg-white rounded-lg shadow-lg flex-grow">
          <table className="table-fixed w-full h-full text-gray-900 rounded-lg overflow-hidden">
            <thead className="bg-gray-200 text-gray-700">
              <tr>
                <th className="px-6 py-3 text-center text-sm font-semibold uppercase w-2/12">
                  {labelConfig.movieManagement.tableHeaders.title}
                </th>
                <th className="px-6 py-3 text-center text-sm font-semibold uppercase w-2/12">
                  {labelConfig.movieManagement.tableHeaders.genre}
                </th>
                <th className="px-6 py-3 text-center text-sm font-semibold uppercase w-2/12">
                  {labelConfig.movieManagement.tableHeaders.rating}
                </th>
                <th className="px-6 py-3 text-center text-sm font-semibold uppercase w-2/12">
                  {labelConfig.movieManagement.tableHeaders.price}
                </th>
                <th className="px-6 py-3 text-center text-sm font-semibold uppercase w-2/12">
                  {labelConfig.movieManagement.tableHeaders.status}
                </th>
                <th className="px-6 py-3 text-center text-sm font-semibold uppercase w-2/12">
                  {labelConfig.movieManagement.tableHeaders.actions}
                </th>
              </tr>
            </thead>
            <tbody>
              {currentMovies.map((movie) => (
                <tr
                  key={movie.movieId}
                  className="border-b border-gray-200 hover:bg-gray-50 transition"
                >
                  {/* Title with ellipsis and tooltip */}
                  <td
                    className="px-6 py-4 text-center text-sm text-gray-700 truncate whitespace-nowrap overflow-hidden text-ellipsis"
                    title={movie.title}
                  >
                    {movie.title}
                  </td>

                  {/* Genre with ellipsis and tooltip */}
                  <td
                    className="px-6 py-4 text-center text-sm text-gray-700 truncate  whitespace-nowrap overflow-hidden text-ellipsis"
                    title={movie.genre}
                  >
                    {movie.genre}
                  </td>

                  {/* Rating */}
                  <td className="px-6 py-4 text-center text-sm text-gray-700">
                    {movie.averageRating}
                  </td>

                  {/* Price */}
                  <td className="px-6 py-4 text-sm text-center text-gray-700">
                    ₹{movie.price}
                  </td>

                  {/* Status with color indication */}
                  <td className="px-6 py-4 text-sm text-center text-gray-700">
                    <span
                      className={`px-3 py-1 rounded-full text-sm font-semibold ${
                        movie.status ===
                        labelConfig.movieManagement.statusLabels.available
                          ? "bg-green-100 text-green-600"
                          : "bg-red-100 text-red-600"
                      }`}
                    >
                      {movie.status}
                    </span>
                  </td>

                  {/* Edit Button */}
                  <td className="px-6 py-4 text-sm text-center flex justify-center items-center">
                    <button
                      onClick={() => openModal(movie.movieId)}
                      className="text-white py-2 px-4 rounded-lg bg-blue-500 hover:bg-blue-400 transition transform hover:scale-105 flex items-center space-x-2"
                    >
                      <FaEdit />
                      <span>
                        {labelConfig.movieManagement.buttonText.editButton}
                      </span>
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          {currentMovies.length > 0 && pageCount > 1 && (
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

      {showModal && (
        <MovieModal
          movieId={selectedMovieId}
          isEdit={!!selectedMovieId}
          closeModal={closeModal}
          handleUpdateMovie={handleUpdateMovie}
        />
      )}
    </div>
  );
};

export default MovieManagement;
