/* eslint-disable react-hooks/exhaustive-deps */
/* eslint-disable no-unused-vars */
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import MovieCard from "../components/MovieCard";
import { getAvailableMovies } from "../services/MovieService";
import ReactPaginate from "react-paginate";
import PacmanLoader from "react-spinners/PacmanLoader";

const UserHome = () => {
  const labelConfig = window.labelConfig;
  const navigate = useNavigate();

  const [movies, setMovies] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [genre, setGenre] = useState("");
  const [sortBy, setSortBy] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [currentPage, setCurrentPage] = useState(0);
  const [moviesPerPage] = useState(8);

  useEffect(() => {
    const fetchMovies = async () => {
      try {
        const fetchedMovies = await getAvailableMovies();
        setMovies(fetchedMovies.reverse());
      } catch (err) {
        console.error("Error fetching movies:", err);
        setError(labelConfig.userhome.fetchMoviesError);
      } finally {
        setTimeout(() => {
          setLoading(false);
        }, 1000);
      }
    };

    fetchMovies();
  }, []);

  const filteredMovies = movies
    .filter((movie) =>
      movie.title.toLowerCase().includes(searchTerm.toLowerCase())
    )
    .filter((movie) => (genre ? movie.genre === genre : true))
    .sort((a, b) => {
      if (sortBy === "rating") return b.averageRating - a.averageRating;
      if (sortBy === "price") return a.price - b.price;
      return 0;
    });

  const offset = currentPage * moviesPerPage;
  const currentMovies = filteredMovies.slice(offset, offset + moviesPerPage);
  const pageCount = Math.ceil(filteredMovies.length / moviesPerPage);

  const handlePageClick = ({ selected }) => {
    setCurrentPage(selected);
  };

  return (
    <div className="bg-gray-100 min-h-screen text-gray-800">
      <div className="container mx-auto px-6 py-6">
        <div className="text-center rounded-md mb-2">
          <h1 className="text-3xl font-bold text-center">
            🎉
            <span className="bg-clip-text text-transparent bg-gradient-to-r from-red-600 to-purple-600">
              {labelConfig.userhome.title}
            </span>{" "}
            🎥✨
          </h1>
          <p className="text-lg p-2 mb-4">
            {labelConfig.userhome.titleDescription}
          </p>
        </div>

        {/* Search, Genre, and Sorting Filters */}
        <div className="flex flex-col md:flex-row gap-4 mb-2 justify-center">
          <input
            type="text"
            placeholder={labelConfig.userhome.searchPlaceholder}
            className="border border-gray-300 rounded-md bg-white text-gray-800 placeholder-gray-400 p-3 focus:outline-none focus:ring-2 focus:ring-red-600 w-full md:w-1/4"
            value={searchTerm}
            maxLength={50}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
          <select
            className="border border-gray-300 rounded-md bg-white text-gray-800 p-3 focus:outline-none focus:ring-2 focus:ring-red-600 w-full md:w-1/4"
            value={genre}
            onChange={(e) => setGenre(e.target.value)}
          >
            <option value="">{labelConfig.userhome.genreLabel}</option>
            {[...new Set(movies.map((movie) => movie.genre))].map((genre) => (
              <option key={genre} value={genre}>
                {genre}
              </option>
            ))}
          </select>
          <select
            className="border border-gray-300 rounded-md bg-white text-gray-800 p-3 focus:outline-none focus:ring-2 focus:ring-red-600 w-full md:w-1/4"
            value={sortBy}
            onChange={(e) => setSortBy(e.target.value)}
          >
            <option value="">{labelConfig.userhome.sortDefaultLabel}</option>
            <option value="rating">
              {labelConfig.userhome.sortRatingLabel}
            </option>
            <option value="price">{labelConfig.userhome.sortPriceLabel}</option>
          </select>
        </div>

        {/* Loader */}
        {loading ? (
          <div className="flex justify-center items-center py-10 h-64">
            <PacmanLoader size={20} color="red" loading={loading} />
          </div>
        ) : error ? (
          <div className="flex justify-center items-center h-64">
            <p className="text-red-600 text-lg">{error}</p>
          </div>
        ) : (
          <>
            {/* Movie Cards */}
            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6 mb-8 mt-6">
              {currentMovies.length > 0 ? (
                currentMovies.map((movie) => (
                  <MovieCard
                    key={movie.movieId}
                    movie={movie}
                    onViewDetails={() => {
                      navigate(`/movies`, {
                        state: { movieId: movie.movieId },
                      });
                    }}
                  />
                ))
              ) : (
                <div className="flex justify-center items-center h-64 col-span-full">
                  <p className="text-gray-700 text-lg">
                    {labelConfig.userhome.noMoviesFoundText}
                  </p>
                </div>
              )}
            </div>

            {/* Pagination */}
            {filteredMovies.length > 0 && pageCount > 1 && (
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
                  "text-gray-800 border border-gray-300 rounded-lg px-3 py-2 transition duration-300 hover:bg-red-500 hover:text-white block text-center"
                }
                previousClassName={`text-gray-800 border border-gray-300 rounded-lg px-3 py-2 transition duration-300 hover:bg-red-500 hover:text-white inline-block text-center ${
                  currentPage === 0
                    ? "cursor-not-allowed opacity-50"
                    : "hover:bg-gray-200"
                }`}
                nextClassName={`text-gray-800 border border-gray-300 rounded-lg px-3 py-2 transition duration-300 hover:bg-red-500 hover:text-white inline-block text-center ${
                  currentPage === pageCount - 1
                    ? "cursor-not-allowed opacity-50"
                    : "hover:bg-gray-200"
                }`}
                disabledClassName={"pointer-events-none opacity-50"}
              />
            )}
          </>
        )}
      </div>
    </div>
  );
};

export default UserHome;
