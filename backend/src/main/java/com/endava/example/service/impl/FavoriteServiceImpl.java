package com.endava.example.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.endava.example.dto.FavoriteDTO;
import com.endava.example.entity.Favorite;
import com.endava.example.entity.Movie;
import com.endava.example.entity.User;
import com.endava.example.exceptions.ResourceAlreadyExistsException;
import com.endava.example.exceptions.ResourceNotFoundException;
import com.endava.example.mapper.FavoriteMapper;
import com.endava.example.repository.FavoriteRepository;
import com.endava.example.repository.MovieRepository;
import com.endava.example.repository.UserRepository;
import com.endava.example.service.FavoriteService;

/**
 * Implementation of FavoriteService, responsible for managing users' favorite
 * movies. Provides functionality to: - Add movies to a user's favorites. -
 * Retrieve a list of a user's favorite movies. - Remove movies from a user's
 * favorites.
 */
@Service
public class FavoriteServiceImpl implements FavoriteService {

	private FavoriteRepository favoriteRepository;

	private UserRepository userRepository;

	private MovieRepository movieRepository;

	private FavoriteMapper favoriteMapper;

	public FavoriteServiceImpl(FavoriteRepository favoriteRepository, UserRepository userRepository,
			MovieRepository movieRepository, FavoriteMapper favoriteMapper) {
		super();
		this.favoriteRepository = favoriteRepository;
		this.userRepository = userRepository;
		this.movieRepository = movieRepository;
		this.favoriteMapper = favoriteMapper;
	}

	/**
	 * Retrieves a list of all favorite movies for a specific user and filters out
	 * based on availability..
	 * 
	 * @param userId the ID of the user whose favorites are to be fetched..
	 * @return List of FavoriteDTOs representing the user's favorite movies..
	 */
	@Override
	public List<FavoriteDTO> getFavoritesByUserId(int userId) {
		return favoriteRepository.findByUser_UserId(userId).stream()
				.filter(fav -> "AVAILABLE".equalsIgnoreCase(fav.getMovie().getStatus())).map(favoriteMapper::toDto)
				.toList();
	}

	/**
	 * Adds a movie to the user's list of favorites.
	 * 
	 * @param dto FavoriteDTO containing the userId and movieId.
	 * @return FavoriteDTO that represents the newly added favorite movie.
	 * @throws ResourceNotFoundException      if the user or movie does not exist.
	 * @throws ResourceAlreadyExistsException if the movie is already in the user's
	 *                                        favorites.
	 */
	@Override
	public FavoriteDTO createFavorite(FavoriteDTO dto) {
		User user = userRepository.findById(dto.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + dto.getUserId()));

		Movie movie = movieRepository.findById(dto.getMovieId())
				.orElseThrow(() -> new ResourceNotFoundException("Movie not found: " + dto.getMovieId()));

		if (favoriteRepository.findByUser_UserIdAndMovie_MovieId(user.getUserId(), movie.getMovieId()).isPresent()) {
			throw new ResourceAlreadyExistsException("Movie already added to favorites.");
		}

		Favorite favorite = favoriteMapper.toEntity(user, movie);
		favorite = favoriteRepository.save(favorite);
		return favoriteMapper.toDto(favorite);
	}

	/**
	 * Removes a specific favorite movie from the user's favorites.
	 * 
	 * @param favoriteId the ID of the favorite item to be removed..
	 * @throws ResourceNotFoundException if the favorite with the given ID is not
	 *                                   found.
	 */
	@Override
	public void removeFavoriteById(int favoriteId) {
		favoriteRepository.findById(favoriteId).ifPresentOrElse(favorite -> favoriteRepository.delete(favorite), () -> {
			throw new ResourceNotFoundException("Favorite with this id doesn't exist..");
		});
	}

	/**
	 * Checks if a specific movie is in the user's list of favorites..
	 * 
	 * @param userId  the ID of the user whose favorites we need to check..
	 * @param movieId the ID of the movie for which we have to check..
	 * @return true if the movie is in the user's favorites, false otherwise..
	 */
	@Override
	public boolean checkMovieInFavoriteOfUser(int userId, int movieId) {
		return favoriteRepository.findByUser_UserIdAndMovie_MovieId(userId, movieId).isPresent();
	}

	/**
	 * Removes a specific movie from the user's favorites.
	 * 
	 * @param userId  the ID of the user for which we have to remove favorite..
	 * @param movieId the ID of the movie which we have to remove..
	 * @throws ResourceNotFoundException if the movie is not found in the user's
	 *                                   favorites.
	 */
	public void removeFromFavorites(int userId, int movieId) {
		Favorite favorite = favoriteRepository.findByUser_UserIdAndMovie_MovieId(userId, movieId)
				.orElseThrow(() -> new ResourceNotFoundException("Movie not found in user's favorites"));
		favoriteRepository.delete(favorite);
	}

}
