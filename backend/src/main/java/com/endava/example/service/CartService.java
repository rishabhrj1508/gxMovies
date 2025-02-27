package com.endava.example.service;

import java.util.List;

import com.endava.example.dto.CartDTO;

public interface CartService {

	CartDTO addToCart(CartDTO dto);

	List<CartDTO> getAllCartItemsOfUser(int userId);

	void removeFromCartOfUser(int userId, int movieId);
	
    void removeMultipleMoviesFromCartOfUser(int userId, List<Integer> movieIds); 

	void clearCartOfUser(int userId);

	boolean checkMovieInCartOfUser(int userId, int movieId);

}
