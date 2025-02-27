package com.endava.example.service;

import java.util.List;

import com.endava.example.dto.FavoriteDTO;

public interface FavoriteService {

	List<FavoriteDTO> getFavoritesByUserId(int userId);

	FavoriteDTO createFavorite(FavoriteDTO dto);

	void removeFavoriteById(int favoriteId);
	
	void removeFromFavorites(int userId ,int movieId);

	boolean checkMovieInFavoriteOfUser(int userId, int movieId);

}
