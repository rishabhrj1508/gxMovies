package com.endava.example.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.endava.example.dto.FavoriteDTO;
import com.endava.example.service.FavoriteService;
import com.endava.example.utils.GenericResponse;

/**
 * FavoriteController handles the API endpoints related to favorites operations,
 * including adding, removing, retrieving, and checking movies in the user's
 * favorites.
 */
@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

	// Injecting required dependencies
	private FavoriteService favoriteService;

	public FavoriteController(FavoriteService favoriteService) {
		super();
		this.favoriteService = favoriteService;
	}

	/**
	 * Adds a movie to the user's favorites list.
	 *
	 * @param dto the FavoriteDTO containing the movie and user details to be added.
	 * @return ResponseEntity containing the created FavoriteDTO wrapped in
	 *         GenericResponse.
	 */
	@PostMapping
	public ResponseEntity<GenericResponse<FavoriteDTO>> addFavorite(@RequestBody FavoriteDTO dto) {
		FavoriteDTO createdFavorite = favoriteService.createFavorite(dto);
		return ResponseEntity.ok(new GenericResponse<>(true, "Movie added to favorites", createdFavorite));
	}

	/**
	 * Retrieves all the favorite movies of a specific user.
	 *
	 * @param userId the ID of the user whose favorites are being fetched.
	 * @return ResponseEntity containing the list of FavoriteDTOs wrapped in
	 *         GenericResponse.
	 */
	@GetMapping("/user/{userId}")
	public ResponseEntity<GenericResponse<List<FavoriteDTO>>> getAllFavoritesByUser(@PathVariable int userId) {
		List<FavoriteDTO> favorites = favoriteService.getFavoritesByUserId(userId);
		return ResponseEntity.ok(new GenericResponse<>(true, "Favorites fetched successfully", favorites));
	}

	/**
	 * Removes a movie from the user's favorites list.
	 *
	 * @param userId  the ID of the user for which the movie has to be removed.
	 * @param movieId the ID of the movie which has to be removed.
	 * @return ResponseEntity with a success message wrapped in GenericResponse.
	 */
	@DeleteMapping
	public ResponseEntity<GenericResponse<Void>> removeFromFavorites(@RequestParam int userId,
			@RequestParam int movieId) {
		favoriteService.removeFromFavorites(userId, movieId);
		return ResponseEntity.ok(new GenericResponse<>(true, "Movie removed from favorites", null));
	}

	/**
	 * Checks if a specific movie is present in the user's favorites list.
	 *
	 * @param userId  the ID of the user for which the movie has to be checked.
	 * @param movieId the ID of the movie which has to be checked.
	 * @return ResponseEntity containing a boolean result wrapped in
	 *         GenericResponse.
	 */
	@GetMapping("/user/{userId}/movie/{movieId}")
	public ResponseEntity<GenericResponse<Boolean>> checkMovieInFavoriteOfUser(@PathVariable int userId,
			@PathVariable int movieId) {
		boolean isFavorite = favoriteService.checkMovieInFavoriteOfUser(userId, movieId);
		return ResponseEntity.ok(new GenericResponse<>(true, "Favorite status checked successfully", isFavorite));
	}
}
