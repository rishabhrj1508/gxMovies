package com.endava.example.mapper;

import org.springframework.stereotype.Component;

import com.endava.example.dto.CartDTO;
import com.endava.example.entity.Cart;
import com.endava.example.entity.Movie;
import com.endava.example.entity.User;

@Component
public class CartMapper {

	private MovieMapper movieMapper;

	public CartMapper(MovieMapper movieMapper) {
		super();
		this.movieMapper = movieMapper;
	}

	public CartDTO toDto(Cart cart) {
		CartDTO dto = new CartDTO();
		dto.setCartId(cart.getCartId());
		dto.setUserId(cart.getUser().getUserId());
		dto.setMovieId(cart.getMovie().getMovieId());
		dto.setMovieDTO(movieMapper.toDto(cart.getMovie()));
		return dto;
	}

	public Cart toEntity(CartDTO dto, User user, Movie movie) {
		Cart cart = new Cart();
		cart.setUser(user);
		cart.setMovie(movie);
		return cart;
	}

}
