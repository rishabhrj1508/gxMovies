package com.endava.example.service;

import java.util.List;

import com.endava.example.dto.MovieDTO;

public interface MovieService {

	MovieDTO addMovie(MovieDTO movieDTO);

	MovieDTO updateMovie(int movieId, MovieDTO movieDTO);

	void deleteMovie(int movieId);

	List<MovieDTO> getAllMovies();

	MovieDTO getMovieById(int movieId);

	// user side
	List<MovieDTO> getAllAvailableMovies();

	List<MovieDTO> getMoviesByGenre(String genre);

}