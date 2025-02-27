package com.endava.example.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.endava.example.dto.CartDTO;
import com.endava.example.entity.Cart;
import com.endava.example.entity.Movie;
import com.endava.example.entity.User;
import com.endava.example.exceptions.ResourceAlreadyExistsException;
import com.endava.example.exceptions.ResourceNotFoundException;
import com.endava.example.mapper.CartMapper;
import com.endava.example.repository.CartRepository;
import com.endava.example.repository.MovieRepository;
import com.endava.example.repository.UserRepository;
import com.endava.example.service.CartService;

@Service
public class CartServiceImpl implements CartService {

	// Injecting dependencies required
	private CartRepository cartRepository;

	private UserRepository userRepository;

	private MovieRepository movieRepository;

	private CartMapper cartMapper;

	public CartServiceImpl(CartRepository cartRepository, UserRepository userRepository,
			MovieRepository movieRepository, CartMapper cartMapper) {
		super();
		this.cartRepository = cartRepository;
		this.userRepository = userRepository;
		this.movieRepository = movieRepository;
		this.cartMapper = cartMapper;
	}

	/**
	 * Adds the movie to the cart of the user
	 * 
	 * @param CartDTO containing details of user and movie
	 * @return CartDTO which is added in the repository
	 * @throws ResourceNotFoundException      if the movieId or userId is invalid
	 * @throws ResourceAlreadyExistsException if the movie is already in the cart of
	 *                                        the user
	 */
	@Override
	public CartDTO addToCart(CartDTO dto) {

		User user = userRepository.findById(dto.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + dto.getUserId()));

		Movie movie = movieRepository.findById(dto.getMovieId())
				.orElseThrow(() -> new ResourceNotFoundException("Movie not found with ID: " + dto.getMovieId()));

		if (cartRepository.existsByUser_UserIdAndMovie_MovieId(dto.getUserId(), dto.getMovieId())) {
			throw new ResourceAlreadyExistsException("Movie already in your cart");
		}

		Cart cart = cartMapper.toEntity(dto, user, movie);
		cart = cartRepository.save(cart);
		return cartMapper.toDto(cart);
	}

	/**
	 * Fetches all the cart items of the user and filters out unavailable movies..
	 * 
	 * @param userId the id of the user
	 * @return list of CartDTOs of the given id
	 */
	@Override
	public List<CartDTO> getAllCartItemsOfUser(int userId) {
		return cartRepository.findByUser_UserId(userId).stream()
				.filter(cart -> "AVAILABLE".equalsIgnoreCase(cart.getMovie().getStatus())).map(cartMapper::toDto)
				.toList();
	}

	/**
	 * Removes the specific movie from the user's cart.
	 * 
	 * @param userId  the id of the user from which we have to remove the movie..
	 * @param movieId the id of the movie to be removed
	 * @throws ResourceNotFoundException if the movieId or userId is invalid
	 */
	@Override
	public void removeFromCartOfUser(int userId, int movieId) {

		cartRepository.findByUser_UserIdAndMovie_MovieId(userId, movieId)
				.ifPresentOrElse(cartRepository::delete, () -> {
					throw new ResourceNotFoundException("Cart item not found for this user and movie.");
				});
	}

	/**
	 * Removes the list of movies from the user's cart
	 *
	 * @param userId   the id of the user from which we have to remove the list of
	 *                 movies
	 * @param movieIds the list of movieIds which have to be removed
	 */
	@Override
	@Transactional
	public void removeMultipleMoviesFromCartOfUser(int userId, List<Integer> movieIds) {

		List<Cart> cartItems = cartRepository.findByUser_UserIdAndMovie_MovieIdIn(userId, movieIds);

		if (cartItems.isEmpty()) {
			throw new ResourceNotFoundException("Selected movies not found in cart of user : " + userId);
		}

		if (cartItems.size() != movieIds.size()) {
			throw new ResourceNotFoundException("Some movies not found in the cart of the user..");
		}
		cartRepository.deleteAll(cartItems);
	}

	/**
	 * Clears all the items from the cart of the user
	 * 
	 * @param userId the id of the user whose cart has to be cleared..
	 */
	@Override
	@Transactional
	public void clearCartOfUser(int userId) {

		List<Cart> itemsInUserCarts = cartRepository.findByUser_UserId(userId);
		if (itemsInUserCarts.isEmpty()) {
			throw new ResourceNotFoundException("Cart is already empty for user: " + userId);
		}
		cartRepository.deleteAll(itemsInUserCarts);
	}

	/**
	 * Checks if the movie is already in the cart of the user or not
	 * 
	 * @param userId  the id of the user
	 * @param movieId the id of the movie
	 * @return true if the movie is in the user's cart, false otherwise.
	 */
	@Override
	public boolean checkMovieInCartOfUser(int userId, int movieId) {
		return cartRepository.existsByUser_UserIdAndMovie_MovieId(userId, movieId);
	}

}
