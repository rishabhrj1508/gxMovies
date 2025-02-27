package com.endava.example.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

	@Mock
	private CartRepository cartRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private MovieRepository movieRepository;

	@Mock
	private CartMapper cartMapper;

	@InjectMocks
	private CartServiceImpl cartService;

	private User user;
	private Movie movie;
	private Cart cart;
	private CartDTO cartDTO;

	@BeforeEach
	void setUp() {
		user = new User();
		user.setUserId(1);

		movie = new Movie();
		movie.setMovieId(1);
		movie.setStatus("AVAILABLE");

		cart = new Cart();
		cart.setUser(user);
		cart.setMovie(movie);

		cartDTO = new CartDTO();
		cartDTO.setUserId(1);
		cartDTO.setMovieId(1);
	}

	@Test
	void testAddToCart_Success() {
		when(userRepository.findById(1)).thenReturn(Optional.of(user));
		when(movieRepository.findById(1)).thenReturn(Optional.of(movie));
		when(cartRepository.existsByUser_UserIdAndMovie_MovieId(1, 1)).thenReturn(false);
		when(cartMapper.toEntity(cartDTO, user, movie)).thenReturn(cart);
		when(cartRepository.save(cart)).thenReturn(cart);
		when(cartMapper.toDto(cart)).thenReturn(cartDTO);

		CartDTO result = cartService.addToCart(cartDTO);

		assertEquals(cartDTO, result);
		verify(cartRepository).save(cart);
	}

	@Test
	void testAddToCart_MovieAlreadyInCart() {
		when(userRepository.findById(1)).thenReturn(Optional.of(user));
		when(movieRepository.findById(1)).thenReturn(Optional.of(movie));
		when(cartRepository.existsByUser_UserIdAndMovie_MovieId(1, 1)).thenReturn(true);

		assertThrows(ResourceAlreadyExistsException.class, () -> cartService.addToCart(cartDTO));
	}

	@Test
	void testAddToCart_UserNotFound() {
		when(userRepository.findById(1)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> cartService.addToCart(cartDTO));
	}

	@Test
	void testAddToCart_MovieNotFound() {
		when(userRepository.findById(1)).thenReturn(Optional.of(user));
		when(movieRepository.findById(1)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> cartService.addToCart(cartDTO));
	}

	@Test
	void testRemoveFromCartOfUser_Success() {
		when(cartRepository.findByUser_UserIdAndMovie_MovieId(1, 1)).thenReturn(Optional.of(cart));

		cartService.removeFromCartOfUser(1, 1);

		verify(cartRepository).delete(cart);
	}

	@Test
	void testRemoveFromCartOfUser_NotFound() {
		when(cartRepository.findByUser_UserIdAndMovie_MovieId(1, 1)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> cartService.removeFromCartOfUser(1, 1));
	}

	@Test
	void testRemoveMultipleMoviesFromCartOfUser_Success() {
		List<Integer> movieIds = Arrays.asList(1);
		when(cartRepository.findByUser_UserIdAndMovie_MovieIdIn(1, movieIds)).thenReturn(Arrays.asList(cart));
		cartService.removeMultipleMoviesFromCartOfUser(1, movieIds);
		verify(cartRepository).deleteAll(Arrays.asList(cart));
	}

	@Test
	void testRemoveMultipleMoviesFromCartOfUser_NotFound() {
		List<Integer> movieIds = Arrays.asList(1, 2);
		when(cartRepository.findByUser_UserIdAndMovie_MovieIdIn(1, movieIds)).thenReturn(Collections.emptyList());

		assertThrows(ResourceNotFoundException.class,
				() -> cartService.removeMultipleMoviesFromCartOfUser(1, movieIds));
	}

	@Test
	void testGetAllCartItemsOfUser() {

		Cart availableCart = new Cart();
		availableCart.setUser(user);
		availableCart.setMovie(movie);

		Movie unavailableMovie = new Movie();
		unavailableMovie.setMovieId(2);
		unavailableMovie.setStatus("UNAVAILABLE");

		Cart unavailableCart = new Cart();
		unavailableCart.setUser(user);
		unavailableCart.setMovie(unavailableMovie);

		when(cartRepository.findByUser_UserId(1)).thenReturn(Arrays.asList(availableCart, unavailableCart));

		when(cartMapper.toDto(availableCart)).thenReturn(cartDTO);

		List<CartDTO> result = cartService.getAllCartItemsOfUser(1);

		assertEquals(1, result.size());

	}

	@Test
	void testClearCartOfUser_Success() {
		when(cartRepository.findByUser_UserId(1)).thenReturn(Collections.singletonList(cart));

		cartService.clearCartOfUser(1);

		verify(cartRepository).deleteAll(Collections.singletonList(cart));
	}

	@Test
	void testClearCartOfUser_AlreadyEmpty() {
		when(cartRepository.findByUser_UserId(1)).thenReturn(Collections.emptyList());

		assertThrows(ResourceNotFoundException.class, () -> cartService.clearCartOfUser(1));
	}

	@Test
	void testCheckMovieInCartOfUser_True() {
		when(cartRepository.existsByUser_UserIdAndMovie_MovieId(1, 1)).thenReturn(true);

		boolean result = cartService.checkMovieInCartOfUser(1, 1);

		assertTrue(result);
	}

	@Test
	void testCheckMovieInCartOfUser_False() {
		when(cartRepository.existsByUser_UserIdAndMovie_MovieId(1, 1)).thenReturn(false);

		boolean result = cartService.checkMovieInCartOfUser(1, 1);

		assertFalse(result);
	}
}
