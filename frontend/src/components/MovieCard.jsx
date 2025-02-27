/* eslint-disable react/prop-types */
/* eslint-disable no-unused-vars */
import React from "react";

const MovieCard = ({ movie, onViewDetails }) => {
  return (
    <div
      className="bg-white rounded-lg overflow-hidden shadow-xl transition-transform transform hover:scale-105 hover:shadow-2xl"
      title={movie.title}
    >
      <img
        src={movie.posterURL}
        alt={movie.title}
        className="w-full h-40 object-cover"
      />
      <div className="p-4 flex flex-col justify-between flex-grow">
        <div>
          <h3 className="text-lg font-bold text-black mb-2 truncate">
            {movie.title}
          </h3>
          <p className="text-sm text-light-blue mb-1 truncate">
            Genre: {movie.genre}
          </p>
          <p className="text-sm text-light-blue mb-1">
            Release Date: {new Date(movie.releaseDate).toLocaleDateString()}
          </p>
          <p className="text-sm text-light-blue mb-1">
            Price: Rs. {movie.price}
          </p>
          <p className="text-sm text-light-blue">
            Rating: {movie.averageRating} ⭐
          </p>
        </div>
        <button
          onClick={() => onViewDetails(movie.movieId)}
          className="mt-3 py-2 text-sm font-semibold text-white bg-red-500 rounded-md hover:bg-red-600 transition-colors"
        >
          View Details
        </button>
      </div>
    </div>
  );
};

export default MovieCard;
