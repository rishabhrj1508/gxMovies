package com.endava.example.dto;

import lombok.Data;

/**
 * FavoriteDTO holds information about user favorites, including the favorite's
 * unique ID, the user's ID, the movie's ID, and details about the movie.
 */
@Data
public class FavoriteDTO {

	private int favoriteId;
	private int userId;
	private int movieId;
	private MovieDTO movieDTO;

}
