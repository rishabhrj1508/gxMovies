package com.endava.example.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.endava.example.dto.FavoriteDTO;
import com.endava.example.exceptions.ResourceAlreadyExistsException;
import com.endava.example.exceptions.ResourceNotFoundException;
import com.endava.example.service.FavoriteService;
import com.endava.example.utils.JwtAuthenticationFilter;
import com.endava.example.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(FavoriteController.class)
@AutoConfigureMockMvc(addFilters = false)
class FavoriteControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private FavoriteService favoriteService;

	@MockitoBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@MockitoBean
	private JwtUtils jwtUtils;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void testAddFavorite_Success() throws Exception {
		FavoriteDTO dto = new FavoriteDTO();
		dto.setUserId(1);
		dto.setMovieId(100);

		when(favoriteService.createFavorite(dto)).thenReturn(dto);

		mockMvc.perform(post("/api/favorites").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Movie added to favorites"))
				.andExpect(jsonPath("$.data.userId").value(1)).andExpect(jsonPath("$.data.movieId").value(100));

		verify(favoriteService).createFavorite(dto);
	}

	@Test
	void testAddFavorite_AlreadyExists() throws Exception {
		FavoriteDTO dto = new FavoriteDTO();
		dto.setUserId(1);
		dto.setMovieId(100);

		when(favoriteService.createFavorite(dto))
				.thenThrow(new ResourceAlreadyExistsException("Movie already in favorites"));

		mockMvc.perform(post("/api/favorites").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isConflict())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("Movie already in favorites"));

		verify(favoriteService).createFavorite(dto);
	}

	@Test
	void testGetAllFavoritesByUser_Success() throws Exception {
		int userId = 1;

		FavoriteDTO favorite1 = new FavoriteDTO();
		favorite1.setUserId(userId);
		favorite1.setMovieId(100);

		FavoriteDTO favorite2 = new FavoriteDTO();
		favorite2.setUserId(userId);
		favorite2.setMovieId(101);

		List<FavoriteDTO> favorites = List.of(favorite1, favorite2);

		when(favoriteService.getFavoritesByUserId(userId)).thenReturn(favorites);

		mockMvc.perform(get("/api/favorites/user/{userId}", userId)).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Favorites fetched successfully"))
				.andExpect(jsonPath("$.data[0].movieId").value(100))
				.andExpect(jsonPath("$.data[1].movieId").value(101));

		verify(favoriteService).getFavoritesByUserId(userId);
	}

	@Test
	void testGetAllFavoritesByUser_NotFound() throws Exception {
		int userId = 99;

		when(favoriteService.getFavoritesByUserId(userId)).thenThrow(new ResourceNotFoundException("User not found"));

		mockMvc.perform(get("/api/favorites/user/{userId}", userId)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success").value(false)).andExpect(jsonPath("$.message").value("User not found"));

		verify(favoriteService).getFavoritesByUserId(userId);
	}

	@Test
	void testRemoveFromFavorites_Success() throws Exception {
		int userId = 1;
		int movieId = 100;

		doNothing().when(favoriteService).removeFromFavorites(userId, movieId);

		mockMvc.perform(delete("/api/favorites").param("userId", String.valueOf(userId))
				.param("movieId", String.valueOf(movieId)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Movie removed from favorites"));

		verify(favoriteService).removeFromFavorites(userId, movieId);
	}

	@Test
	void testRemoveFromFavorites_NotFound() throws Exception {
		int userId = 999;
		int movieId = 888;

		doThrow(new ResourceNotFoundException("Favorite not found")).when(favoriteService).removeFromFavorites(userId,
				movieId);

		mockMvc.perform(delete("/api/favorites").param("userId", String.valueOf(userId))
				.param("movieId", String.valueOf(movieId)).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("Favorite not found"));

		verify(favoriteService).removeFromFavorites(userId, movieId);
	}

	@Test
	void testCheckMovieInFavoriteOfUser_Success() throws Exception {
		int userId = 1;
		int movieId = 100;

		when(favoriteService.checkMovieInFavoriteOfUser(userId, movieId)).thenReturn(true);

		mockMvc.perform(get("/api/favorites/user/{userId}/movie/{movieId}", userId, movieId)).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Favorite status checked successfully"))
				.andExpect(jsonPath("$.data").value(true));

		verify(favoriteService).checkMovieInFavoriteOfUser(userId, movieId);
	}

	@Test
	void testCheckMovieInFavoriteOfUser_NotFound() throws Exception {
		int userId = 99;
		int movieId = 999;

		when(favoriteService.checkMovieInFavoriteOfUser(userId, movieId))
				.thenThrow(new ResourceNotFoundException("Movie not found in favorites."));

		mockMvc.perform(get("/api/favorites/user/{userId}/movie/{movieId}", userId, movieId))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("Movie not found in favorites."));

		verify(favoriteService).checkMovieInFavoriteOfUser(userId, movieId);
	}
}
