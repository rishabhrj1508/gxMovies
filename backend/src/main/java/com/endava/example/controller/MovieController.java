package com.endava.example.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.endava.example.dto.MovieDTO;
import com.endava.example.service.MovieService;
import com.endava.example.utils.GenericResponse;

/**
 * MovieController handles API endpoints related to movie operations, such as
 * adding, updating, deleting, and retrieving movies based on various criteria.
 */
@RestController
@RequestMapping("/api/movies")
public class MovieController {

	private MovieService movieService;

	public MovieController(MovieService movieService) {
		super();
		this.movieService = movieService;
	}

	/**
	 * Adds a new movie to the database.
	 *
	 * @param movieDTO the movie details to be added.
	 * @return ResponseEntity containing GenericResponse with created MovieDTO.
	 */
	@PostMapping("/add")
	public ResponseEntity<GenericResponse<MovieDTO>> addMovie(@RequestBody MovieDTO movieDTO) {
		MovieDTO createdMovie = movieService.addMovie(movieDTO);
		return ResponseEntity.status(201).body(new GenericResponse<>(true, "Movie added successfully", createdMovie));
	}

	/**
	 * Updates an existing movie's details.
	 *
	 * @param movieId  the ID of the movie to be updated.
	 * @param movieDTO the updated movie details.
	 * @return ResponseEntity with GenericResponse indicating the update result.
	 */
	@PutMapping("/{movieId}")
	public ResponseEntity<GenericResponse<String>> updateMovie(@PathVariable int movieId,
			@RequestBody MovieDTO movieDTO) {
		movieService.updateMovie(movieId, movieDTO);
		return ResponseEntity.ok(new GenericResponse<>(true, "Movie updated successfully", null));
	}

	/**
	 * Deletes a movie from the database.
	 *
	 * @param movieId the ID of the movie to be deleted.
	 * @return ResponseEntity with GenericResponse indicating successful deletion.
	 */
	@PostMapping("/{movieId}/delete")
	public ResponseEntity<GenericResponse<String>> deleteMovie(@PathVariable int movieId) {
		movieService.deleteMovie(movieId);
		return ResponseEntity.ok(new GenericResponse<>(true, "Movie deleted successfully", null));
	}

	/**
	 * Retrieves a movie's details by its ID.
	 *
	 * @param movieId the ID of the movie to retrieve.
	 * @return ResponseEntity containing GenericResponse with the requested
	 *         MovieDTO.
	 */
	@GetMapping("/{movieId}")
	public ResponseEntity<GenericResponse<MovieDTO>> getMovieById(@PathVariable int movieId) {
		MovieDTO movieDTO = movieService.getMovieById(movieId);
		return ResponseEntity.ok(new GenericResponse<>(true, "Movie retrieved successfully", movieDTO));
	}

	/**
	 * Retrieves all movies from the database.
	 *
	 * @return ResponseEntity containing GenericResponse with a list of movies.
	 */
	@GetMapping("/all")
	public ResponseEntity<GenericResponse<List<MovieDTO>>> getAllMovies() {
		List<MovieDTO> movies = movieService.getAllMovies();
		return ResponseEntity.ok(new GenericResponse<>(true, "Movies retrieved successfully", movies));
	}

	/**
	 * Retrieves all available movies from the database.
	 *
	 * @return ResponseEntity containing GenericResponse with a list of available
	 *         movies.
	 */
	@GetMapping("/all/available")
	public ResponseEntity<GenericResponse<List<MovieDTO>>> getAllAvailableMovies() {
		List<MovieDTO> availableMovies = movieService.getAllAvailableMovies();
		return ResponseEntity
				.ok(new GenericResponse<>(true, "Available movies retrieved successfully", availableMovies));
	}

	/**
	 * Retrieves movies based on genre.
	 *
	 * @param genre the genre to filter movies by (optional).
	 * @return ResponseEntity containing GenericResponse with a list of movies.
	 */
	@GetMapping("/recommended")
	public ResponseEntity<GenericResponse<List<MovieDTO>>> getMoviesByGenre(
			@RequestParam(required = false) String genre) {
		List<MovieDTO> moviesByGenre = movieService.getMoviesByGenre(genre);
		return ResponseEntity.ok(new GenericResponse<>(true, "Movies retrieved successfully", moviesByGenre));
	}
}
