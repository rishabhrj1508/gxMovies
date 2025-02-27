package com.endava.example.dto;

import lombok.Data;

/**
 * CartDTO holds information about a shopping cart, 
 * including the cart's unique ID, the user's ID, the movie's ID, and details about the movie.
 */
@Data
public class CartDTO {

	private int cartId;
	private int userId;
	private int movieId;
	private MovieDTO movieDTO;
}
