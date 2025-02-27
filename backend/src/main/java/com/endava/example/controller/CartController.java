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

import com.endava.example.dto.CartDTO;
import com.endava.example.service.CartService;
import com.endava.example.utils.GenericResponse;

/**
 * CartController handles the API endpoints related to cart operations,
 * including adding, removing, retrieving, and checking items in the user's
 * cart.
 */
@RestController
@RequestMapping("/api/carts")
public class CartController {

	// Injecting the required dependencies
	private final CartService cartService;

	public CartController(CartService cartService) {
		this.cartService = cartService;
	}

	/**
	 * Adds an item to the user's cart.
	 *
	 * @param dto the CartDTO containing the details of the item to add.
	 * @return ResponseEntity with the created CartDTO wrapped in GenericResponse.
	 */
	@PostMapping
	public ResponseEntity<GenericResponse<CartDTO>> addToCart(@RequestBody CartDTO dto) {
		CartDTO addedItem = cartService.addToCart(dto);
		return ResponseEntity.ok(new GenericResponse<>(true, "Item added to cart", addedItem));
	}

	/**
	 * Retrieves all the items in the user's cart.
	 *
	 * @param userId the userId for which cart items are to be fetched.
	 * @return ResponseEntity containing a list of CartDTOs wrapped in
	 *         GenericResponse.
	 */
	@GetMapping("/user/{userId}")
	public ResponseEntity<GenericResponse<List<CartDTO>>> getAllItemsInCart(@PathVariable int userId) {
		List<CartDTO> cartItems = cartService.getAllCartItemsOfUser(userId);
		return ResponseEntity.ok(new GenericResponse<>(true, "Cart items fetched successfully", cartItems));
	}

	/**
	 * Removes the movie from the cart of the user.
	 *
	 * @param userId  the userId from whose cart the movie will be removed.
	 * @param movieId the movieId to be removed from the cart.
	 * @return ResponseEntity with a success message wrapped in GenericResponse.
	 */
	@DeleteMapping("/user/{userId}/movie/{movieId}")
	public ResponseEntity<GenericResponse<Void>> removeFromCartOfUser(@PathVariable int userId,
			@PathVariable int movieId) {
		cartService.removeFromCartOfUser(userId, movieId);
		return ResponseEntity.ok(new GenericResponse<>(true, "Movie removed from cart", null));
	}

	/**
	 * Removes multiple movies from the user's cart.
	 *
	 * @param userId   the userId whose cart items will be removed.
	 * @param movieIds the list of movieIds to be removed.
	 * @return ResponseEntity with a success message wrapped in GenericResponse.
	 */
	@DeleteMapping("/user/remove-multiple-cartItems")
	public ResponseEntity<GenericResponse<Void>> removeMultipleMovies(@RequestParam int userId,
			@RequestBody List<Integer> movieIds) {
		cartService.removeMultipleMoviesFromCartOfUser(userId, movieIds);
		return ResponseEntity.ok(new GenericResponse<>(true, "Selected movies removed from cart", null));
	}

	/**
	 * Clears all cart items for a specific user.
	 *
	 * @param userId the userId whose cart will be cleared.
	 * @return ResponseEntity with a success message wrapped in GenericResponse.
	 */
	@DeleteMapping("/user/{userId}/clear")
	public ResponseEntity<GenericResponse<Void>> clearCartOfUser(@PathVariable int userId) {
		cartService.clearCartOfUser(userId);
		return ResponseEntity.ok(new GenericResponse<>(true, "Cart cleared successfully", null));
	}

	/**
	 * Checks if the specific movie is present in the user's cart.
	 *
	 * @param userId  the userId whose cart will be checked.
	 * @param movieId the movieId to check in the user's cart.
	 * @return ResponseEntity containing boolean wrapped in GenericResponse.
	 */
	@GetMapping("/user/{userId}/movie/{movieId}")
	public ResponseEntity<GenericResponse<Boolean>> checkMovieInCartOfUser(@PathVariable int userId,
			@PathVariable int movieId) {
		boolean isInCart = cartService.checkMovieInCartOfUser(userId, movieId);
		return ResponseEntity.ok(new GenericResponse<>(true, "Movie check in cart successful", isInCart));
	}
}
