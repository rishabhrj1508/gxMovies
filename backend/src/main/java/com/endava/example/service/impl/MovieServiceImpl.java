package com.endava.example.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.endava.example.controller.NotificationController;
import com.endava.example.dto.MovieDTO;
import com.endava.example.entity.Movie;
import com.endava.example.exceptions.ResourceAlreadyExistsException;
import com.endava.example.exceptions.ResourceNotFoundException;
import com.endava.example.mapper.MovieMapper;
import com.endava.example.repository.MovieRepository;
import com.endava.example.service.MovieService;

/**
 * Implementation of the MovieService interface. Provides operations for
 * managing movies, including CRUD operations and filtering.
 */
@Service
public class MovieServiceImpl implements MovieService {

	// injecting required dependencies
	private MovieRepository movieRepository;

	private NotificationController notificationController;

	private MovieMapper movieMapper;

	public MovieServiceImpl(MovieRepository movieRepository, NotificationController notificationController,
			MovieMapper movieMapper) {
		super();
		this.movieRepository = movieRepository;
		this.notificationController = notificationController;
		this.movieMapper = movieMapper;
	}

	/**
	 * -Adds a new movie to the database. -Sends a notification about the new movie.
	 *
	 * @param movieDTO the movie data to be added.
	 * @return the added movie as a DTO.
	 * @throws IllegalArgumentException if the movieDTO is null
	 */
	@Override
	public MovieDTO addMovie(MovieDTO movieDTO) {

		if (movieDTO == null) {
			throw new IllegalArgumentException("Movie data cannot be null or empty.");
		}
		Optional<Movie> existingMovie = movieRepository.findByTitleIgnoreCase(movieDTO.getTitle());

		if (existingMovie.isPresent()) {
			throw new ResourceAlreadyExistsException("Movie with this title already exists.");
		}

		Movie movie = movieMapper.toEntity(movieDTO);

		movie = movieRepository.save(movie);

		notificationController.sendNotificationToAllClients("A new movie has been added: " + movie.getTitle());

		return movieMapper.toDto(movie);
	}

	/**
	 * Updates an existing movie.
	 *
	 * @param movieId  The ID of the movie to be updated.
	 * @param movieDTO The new movie data.
	 * @return The updated movie as a DTO.
	 * @throws ResourceNotFoundException if the movie is not found.
	 * @throws IllegalArgumentException  if movieDTO is null
	 */
	@Override
	public MovieDTO updateMovie(int movieId, MovieDTO movieDTO) {
		if (movieDTO == null) {
			throw new IllegalArgumentException("Movie data cannot be null or empty.");
		}

		Movie movie = movieRepository.findById(movieId)
				.orElseThrow(() -> new ResourceNotFoundException("Movie not found with ID: " + movieId));

		movieRepository.findByTitleIgnoreCase(movieDTO.getTitle()).ifPresent(existingMovie -> {
			if (existingMovie.getMovieId() != movieId) {
				throw new ResourceAlreadyExistsException("Movie with this title already exists.");
			}
		});

		// Update the movie details
		movie.setTitle(movieDTO.getTitle());
		movie.setDescription(movieDTO.getDescription());
		movie.setGenre(movieDTO.getGenre());
		movie.setReleaseDate(movieDTO.getReleaseDate());
		movie.setPrice(movieDTO.getPrice());
		movie.setAverageRating(movieDTO.getAverageRating());
		movie.setPosterURL(movieDTO.getPosterURL());
		movie.setTrailerURL(movieDTO.getTrailerURL());
		movie.setStatus(movieDTO.getStatus());
		movie.setUpdatedAt(LocalDate.now());

		// Save the updated movie and return the DTO
		return movieMapper.toDto(movieRepository.save(movie));
	}

	/**
	 * Toggles the availability status of a movie.- If the movie is AVAILABLE, marks
	 * it as UNAVAILABLE and vice versa.
	 *
	 * @param movieId The ID of the movie to be toggled.
	 * @throws ResourceNotFoundException if the movie is not found.
	 */
	@Override
	public void deleteMovie(int movieId) {
		Movie movie = movieRepository.findById(movieId)
				.orElseThrow(() -> new ResourceNotFoundException("Movie not found with ID: " + movieId));

		if ("AVAILABLE".equalsIgnoreCase(movie.getStatus())) {
			movie.setStatus("UNAVAILABLE");
		} else {
			movie.setStatus("AVAILABLE");
		}

		movieRepository.save(movie);
	}

	/**
	 * Retrieves all movies from the database..
	 *
	 * @return List of all movies as DTOs..
	 * @throws ResourceNotFoundException if the list returned is empty..
	 */
	@Override
	public List<MovieDTO> getAllMovies() {
		List<Movie> movies = movieRepository.findAll();
		if (movies.isEmpty()) {
			throw new ResourceNotFoundException("No movies found.");
		}
		return movies.stream().map(movieMapper::toDto).toList();
	}

	/**
	 * Retrieves a movie by its ID. - Fetches the movie by ID. - Converts the entity
	 * to DTO and returns it.
	 *
	 * @param movieId The ID of the movie to retrieve.
	 * @return The movie as a DTO.
	 * @throws ResourceNotFoundException if the movie is not found.
	 */
	@Override
	public MovieDTO getMovieById(int movieId) {
		Movie movie = movieRepository.findById(movieId)
				.orElseThrow(() -> new ResourceNotFoundException("Movie not found with ID: " + movieId));
		return movieMapper.toDto(movie);
	}

	/**
	 * Retrieves all available movies (movies marked as AVAILABLE)
	 *
	 * @return List of available movies as DTOs.
	 * @throws ResourceNotFoundException if no available movies are found.
	 */
	@Override
	public List<MovieDTO> getAllAvailableMovies() {
		List<Movie> movies = movieRepository.getAllAvailableMovies();
		if (movies.isEmpty()) {
			throw new ResourceNotFoundException("No available movies found.");
		}
		return movies.stream().map(movieMapper::toDto).toList();
	}

	/**
	 * Retrieves movies by genre. - Calls the repository method to find movies by
	 * genre. - Converts the movie list to DTOs. - Returns the list of movies
	 * matching the genre as DTOs.
	 *
	 * @param genre The genre of movies to retrieve.
	 * @return List of movies of the specified genre as DTOs.
	 * @throws IllegalArgumentException  if genre is null or empty.
	 * @throws ResourceNotFoundException if no movies are found for the given genre.
	 */
	@Override
	public List<MovieDTO> getMoviesByGenre(String genre) {

		if (genre == null || genre.isEmpty()) {
			throw new IllegalArgumentException("Genre cannot be null or empty");
		}

		List<Movie> movies = movieRepository.findByGenre(genre);
		List<MovieDTO> availableMovies = movies.stream()
				.filter(movie -> "AVAILABLE".equalsIgnoreCase(movie.getStatus())).map(movieMapper::toDto).toList();

		if (availableMovies.isEmpty()) {
			throw new ResourceNotFoundException("No available movies found for genre: " + genre);
		}

		return availableMovies;
	}
}
