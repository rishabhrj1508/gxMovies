package com.endava.example.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.endava.example.dto.MovieDTO;
import com.endava.example.exceptions.ResourceAlreadyExistsException;
import com.endava.example.exceptions.ResourceNotFoundException;
import com.endava.example.service.MovieService;
import com.endava.example.utils.JwtAuthenticationFilter;
import com.endava.example.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(MovieController.class)
@AutoConfigureMockMvc(addFilters = false)
class MovieControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private MovieService movieService;

	@MockitoBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@MockitoBean
	private JwtUtils jwtUtils;

	@Test
	void testAddMovie_Success() throws Exception {
		MovieDTO movieDTO = new MovieDTO();
		movieDTO.setTitle("Movie 1");

		when(movieService.addMovie(any(MovieDTO.class))).thenReturn(movieDTO);

		mockMvc.perform(post("/api/movies/add").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(movieDTO))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Movie added successfully"))
				.andExpect(jsonPath("$.data.title").value("Movie 1"));

		verify(movieService, times(1)).addMovie(any(MovieDTO.class));
	}

	@Test
	void testAddMovie_Failure_DuplicateTitle() throws Exception {
		MovieDTO movieDTO = new MovieDTO();
		movieDTO.setTitle("Duplicate Movie");

		when(movieService.addMovie(any(MovieDTO.class)))
				.thenThrow(new ResourceAlreadyExistsException("Movie with this title already exists"));

		mockMvc.perform(post("/api/movies/add").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(movieDTO))).andExpect(status().isConflict())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("Movie with this title already exists"));

		verify(movieService).addMovie(any(MovieDTO.class));
	}

	@Test
	void testUpdateMovie_Success() throws Exception {
		int movieId = 1;
		MovieDTO movieDTO = new MovieDTO();
		movieDTO.setTitle("Updated Title");

		when(movieService.updateMovie(eq(movieId), any(MovieDTO.class))).thenReturn(null);

		mockMvc.perform(put("/api/movies/{movieId}", movieId).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(movieDTO))).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Movie updated successfully"))
				.andExpect(jsonPath("$.data").isEmpty());

		verify(movieService).updateMovie(eq(movieId), any(MovieDTO.class));
	}

	@Test
	void testUpdateMovie_Failure_NotFound() throws Exception {
		int movieId = 999;
		MovieDTO movieDTO = new MovieDTO();

		when(movieService.updateMovie(eq(movieId), any(MovieDTO.class)))
				.thenThrow(new ResourceNotFoundException("Movie not found with this id"));

		mockMvc.perform(put("/api/movies/{movieId}", movieId).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(movieDTO))).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("Movie not found with this id"));

		verify(movieService).updateMovie(eq(movieId), any(MovieDTO.class));
	}

	@Test
	void testDeleteMovie_Success() throws Exception {
		int movieId = 1;

		mockMvc.perform(post("/api/movies/{movieId}/delete", movieId).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Movie deleted successfully"));

		verify(movieService).deleteMovie(movieId);
	}

	@Test
	void testDeleteMovie_Failure_NotFound() throws Exception {
		int movieId = 999;

		doThrow(new ResourceNotFoundException("Movie not found with this id")).when(movieService).deleteMovie(movieId);

		mockMvc.perform(post("/api/movies/{movieId}/delete", movieId).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("Movie not found with this id"));

		verify(movieService).deleteMovie(movieId);
	}

	@Test
	void testGetMovieById_Success() throws Exception {
		int movieId = 1;

		MovieDTO movieDTO = new MovieDTO();
		movieDTO.setMovieId(movieId);
		movieDTO.setTitle("Movie 1");
		when(movieService.getMovieById(movieId)).thenReturn(movieDTO);

		mockMvc.perform(get("/api/movies/{movieId}", movieId).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Movie retrieved successfully"))
				.andExpect(jsonPath("$.data.movieId").value(movieId))
				.andExpect(jsonPath("$.data.title").value("Movie 1"));

		verify(movieService).getMovieById(movieId);
	}

	@Test
	void testGetMovieById_Failure_NotFound() throws Exception {
		int movieId = 999;

		doThrow(new ResourceNotFoundException("Movie not found with this id")).when(movieService).getMovieById(movieId);

		mockMvc.perform(get("/api/movies/{movieId}", movieId).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("Movie not found with this id"));

		verify(movieService).getMovieById(movieId);
	}

	@Test
	void testGetAllMovies_Success() throws Exception {
		List<MovieDTO> movies = new ArrayList<>();
		MovieDTO movie1 = new MovieDTO();
		movie1.setMovieId(1);
		movie1.setTitle("Movie 1");

		MovieDTO movie2 = new MovieDTO();
		movie2.setMovieId(2);
		movie2.setTitle("Movie 2");

		movies.add(movie1);
		movies.add(movie2);

		when(movieService.getAllMovies()).thenReturn(movies);

		mockMvc.perform(get("/api/movies/all").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Movies retrieved successfully"))
				.andExpect(jsonPath("$.data[0].movieId").value(1))
				.andExpect(jsonPath("$.data[0].title").value("Movie 1"))
				.andExpect(jsonPath("$.data[1].movieId").value(2))
				.andExpect(jsonPath("$.data[1].title").value("Movie 2"));

		verify(movieService).getAllMovies();
	}

	@Test
	void testGetAllMovies_EmptyList() throws Exception {
		when(movieService.getAllMovies()).thenThrow(new ResourceNotFoundException("No movies found"));

		mockMvc.perform(get("/api/movies/all").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("No movies found"));

		verify(movieService).getAllMovies();
	}

	@Test
	void testGetAllAvailableMovies_Success() throws Exception {
		List<MovieDTO> availableMovies = new ArrayList<>();
		MovieDTO movie1 = new MovieDTO();
		movie1.setMovieId(1);
		movie1.setTitle("Available Movie 1");

		MovieDTO movie2 = new MovieDTO();
		movie2.setMovieId(2);
		movie2.setTitle("Available Movie 2");

		availableMovies.add(movie1);
		availableMovies.add(movie2);

		when(movieService.getAllAvailableMovies()).thenReturn(availableMovies);

		mockMvc.perform(get("/api/movies/all/available").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Available movies retrieved successfully"))
				.andExpect(jsonPath("$.data[0].movieId").value(1))
				.andExpect(jsonPath("$.data[0].title").value("Available Movie 1"))
				.andExpect(jsonPath("$.data[1].movieId").value(2))
				.andExpect(jsonPath("$.data[1].title").value("Available Movie 2"));

		verify(movieService).getAllAvailableMovies();
	}

	@Test
	void testGetAllAvailableMovies_Failure_NoMoviesFound() throws Exception {
		when(movieService.getAllAvailableMovies())
				.thenThrow(new ResourceNotFoundException("No available movies found."));

		mockMvc.perform(get("/api/movies/all/available").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("No available movies found."));

		verify(movieService).getAllAvailableMovies();
	}

	@Test
	void testGetMoviesByGenre_Success_WithGenre() throws Exception {
		String genre = "Action";
		List<MovieDTO> moviesByGenre = new ArrayList<>();
		MovieDTO movie1 = new MovieDTO();
		movie1.setMovieId(1);
		movie1.setTitle("Action Movie 1");

		MovieDTO movie2 = new MovieDTO();
		movie2.setMovieId(2);
		movie2.setTitle("Action Movie 2");

		moviesByGenre.add(movie1);
		moviesByGenre.add(movie2);

		when(movieService.getMoviesByGenre(genre)).thenReturn(moviesByGenre);

		mockMvc.perform(get("/api/movies/recommended").param("genre", genre).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Movies retrieved successfully"))
				.andExpect(jsonPath("$.data[0].movieId").value(1))
				.andExpect(jsonPath("$.data[0].title").value("Action Movie 1"))
				.andExpect(jsonPath("$.data[1].movieId").value(2))
				.andExpect(jsonPath("$.data[1].title").value("Action Movie 2"));

		verify(movieService).getMoviesByGenre(genre);
	}

	@Test
	void testGetMoviesByGenre_Failure_NoMoviesFound() throws Exception {
		String genre = "NonExistentGenre";
		when(movieService.getMoviesByGenre(genre))
				.thenThrow(new ResourceNotFoundException("No movies found for this genre."));

		mockMvc.perform(get("/api/movies/recommended").param("genre", genre).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("No movies found for this genre."));

		verify(movieService).getMoviesByGenre(genre);
	}

}
