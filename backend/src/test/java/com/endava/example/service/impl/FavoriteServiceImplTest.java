package com.endava.example.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.endava.example.dto.FavoriteDTO;
import com.endava.example.entity.Favorite;
import com.endava.example.entity.Movie;
import com.endava.example.entity.User;
import com.endava.example.exceptions.ResourceNotFoundException;
import com.endava.example.mapper.FavoriteMapper;
import com.endava.example.repository.FavoriteRepository;
import com.endava.example.repository.MovieRepository;
import com.endava.example.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceImplTest {

	@Mock
	private FavoriteRepository favoriteRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private MovieRepository movieRepository;

	@Mock
	private FavoriteMapper favoriteMapper;

	@InjectMocks
	private FavoriteServiceImpl favoriteService;

	@Test
	void testGetFavoritesByUserId_Success() {
		int userId = 1;

		Movie movie1 = new Movie();
		movie1.setStatus("AVAILABLE");

		Movie movie2 = new Movie();
		movie2.setStatus("AVAILABLE");

		Favorite favorite1 = new Favorite();
		favorite1.setMovie(movie1);

		Favorite favorite2 = new Favorite();
		favorite2.setMovie(movie2);

		List<Favorite> favorites = List.of(favorite1, favorite2);

		when(favoriteRepository.findByUser_UserId(userId)).thenReturn(favorites);
		FavoriteDTO favoriteDTO1 = new FavoriteDTO();
		FavoriteDTO favoriteDTO2 = new FavoriteDTO();

		when(favoriteMapper.toDto(favorite1)).thenReturn(favoriteDTO1);
		when(favoriteMapper.toDto(favorite2)).thenReturn(favoriteDTO2);

		List<FavoriteDTO> expectedFavoriteDTOs = List.of(favoriteDTO1, favoriteDTO2);
		List<FavoriteDTO> actualFavoriteDTOs = favoriteService.getFavoritesByUserId(userId);

		assertEquals(expectedFavoriteDTOs, actualFavoriteDTOs);
	}

	@Test
	void testCreateFavorite_Positive() {
		FavoriteDTO dto = new FavoriteDTO();
		dto.setUserId(1);
		dto.setMovieId(1);

		User user = new User();
		user.setUserId(1);

		Movie movie = new Movie();
		movie.setMovieId(1);

		when(userRepository.findById(dto.getUserId())).thenReturn(Optional.of(user));
		when(movieRepository.findById(dto.getMovieId())).thenReturn(Optional.of(movie));
		when(favoriteRepository.findByUser_UserIdAndMovie_MovieId(user.getUserId(), movie.getMovieId()))
				.thenReturn(Optional.empty());

		Favorite favorite = new Favorite();
		when(favoriteMapper.toEntity(user, movie)).thenReturn(favorite);
		when(favoriteRepository.save(any(Favorite.class))).thenReturn(favorite);

		FavoriteDTO expectedFavoriteDTO = new FavoriteDTO();
		when(favoriteMapper.toDto(favorite)).thenReturn(expectedFavoriteDTO);

		FavoriteDTO actualFavoriteDTO = favoriteService.createFavorite(dto);

		assertEquals(expectedFavoriteDTO, actualFavoriteDTO);
	}

	@Test
	void testCreateFavorite_UserNotFound() {
		FavoriteDTO dto = new FavoriteDTO();
		dto.setUserId(1);
		dto.setMovieId(1);

		when(userRepository.findById(dto.getUserId())).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> favoriteService.createFavorite(dto));
	}

	@Test
	void testCreateFavorite_MovieNotFound() {
		FavoriteDTO dto = new FavoriteDTO();
		dto.setUserId(1);
		dto.setMovieId(1);

		User user = new User();
		user.setUserId(1);

		when(userRepository.findById(dto.getUserId())).thenReturn(Optional.of(user));
		when(movieRepository.findById(dto.getMovieId())).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> favoriteService.createFavorite(dto));
	}


	@Test
	void testRemoveFavoriteById_Success() {
		int favoriteId = 1;
		Favorite favorite = new Favorite();

		when(favoriteRepository.findById(favoriteId)).thenReturn(Optional.of(favorite));

		favoriteService.removeFavoriteById(favoriteId);

		verify(favoriteRepository, times(1)).delete(favorite);
	}

	@Test
	void testRemoveFavoriteById_NotFound() {
		int favoriteId = 1;

		when(favoriteRepository.findById(favoriteId)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> favoriteService.removeFavoriteById(favoriteId));
	}

	@Test
	void testCheckMovieInFavoriteOfUser() {
		int userId = 1;
		int movieId = 2;

		when(favoriteRepository.findByUser_UserIdAndMovie_MovieId(userId, movieId))
				.thenReturn(Optional.of(new Favorite()));

		boolean result = favoriteService.checkMovieInFavoriteOfUser(userId, movieId);

		assertTrue(result);
	}

	@Test
	void testRemoveFromFavorites_Success() {
		int userId = 1;
		int movieId = 2;
		Favorite favorite = new Favorite();

		when(favoriteRepository.findByUser_UserIdAndMovie_MovieId(userId, movieId)).thenReturn(Optional.of(favorite));

		favoriteService.removeFromFavorites(userId, movieId);

		verify(favoriteRepository, times(1)).delete(favorite);
	}

	@Test
	void testRemoveFromFavorites_NotFound() {
		int userId = 1;
		int movieId = 2;

		when(favoriteRepository.findByUser_UserIdAndMovie_MovieId(userId, movieId)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> favoriteService.removeFromFavorites(userId, movieId));
	}
}
