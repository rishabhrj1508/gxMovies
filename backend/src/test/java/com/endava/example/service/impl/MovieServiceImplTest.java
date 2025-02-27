package com.endava.example.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.endava.example.controller.NotificationController;
import com.endava.example.dto.MovieDTO;
import com.endava.example.entity.Movie;
import com.endava.example.exceptions.ResourceAlreadyExistsException;
import com.endava.example.exceptions.ResourceNotFoundException;
import com.endava.example.mapper.MovieMapper;
import com.endava.example.repository.MovieRepository;

@ExtendWith(MockitoExtension.class)
class MovieServiceImplTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private NotificationController notificationController;

    @Mock
    private MovieMapper movieMapper;

    @InjectMocks
    private MovieServiceImpl movieService;

    private MovieDTO movieDTO;
    private Movie movie;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        movieDTO = new MovieDTO();
        movieDTO.setTitle("Inception");
        movieDTO.setGenre("Sci-Fi");
        movieDTO.setStatus("AVAILABLE");

        movie = new Movie();
        movie.setTitle("Inception");
        movie.setGenre("Sci-Fi");
        movie.setStatus("AVAILABLE");
    }

    @Test
    void testAddMovie_Success() {
        when(movieRepository.findByTitleIgnoreCase(movieDTO.getTitle())).thenReturn(Optional.empty());
        when(movieMapper.toEntity(movieDTO)).thenReturn(movie);
        when(movieRepository.save(movie)).thenReturn(movie);
        when(movieMapper.toDto(movie)).thenReturn(movieDTO);

        MovieDTO result = movieService.addMovie(movieDTO);

        assertEquals(movieDTO.getTitle(), result.getTitle());
        verify(notificationController, times(1))
                .sendNotificationToAllClients("A new movie has been added: Inception");
    }

    @Test
    void testAddMovie_DuplicateTitle() {
        when(movieRepository.findByTitleIgnoreCase(movieDTO.getTitle())).thenReturn(Optional.of(movie));

        assertThrows(ResourceAlreadyExistsException.class, () -> movieService.addMovie(movieDTO));
    }

    @Test
    void testAddMovie_NullMovieDTO() {
        assertThrows(IllegalArgumentException.class, () -> movieService.addMovie(null));
    }

    @Test
    void testUpdateMovie_Success() {
        int movieId = 1;
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(movieRepository.findByTitleIgnoreCase(movieDTO.getTitle())).thenReturn(Optional.empty());
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);
        when(movieMapper.toDto(movie)).thenReturn(movieDTO);

        MovieDTO result = movieService.updateMovie(movieId, movieDTO);

        assertEquals(movieDTO.getTitle(), result.getTitle());
    }

    @Test
    void testUpdateMovie_NotFound() {
        int movieId = 1;
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> movieService.updateMovie(movieId, movieDTO));
    }

    @Test
    void testUpdateMovie_DuplicateTitle() {
        int movieId = 1;
        Movie existingMovie = new Movie();
        existingMovie.setMovieId(2);

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(movieRepository.findByTitleIgnoreCase(movieDTO.getTitle())).thenReturn(Optional.of(existingMovie));

        assertThrows(ResourceAlreadyExistsException.class, () -> movieService.updateMovie(movieId, movieDTO));
    }

    @Test
    void testUpdateMovie_NullMovieDTO() {
        assertThrows(IllegalArgumentException.class, () -> movieService.updateMovie(1, null));
    }

    // deleteMovie() Test Cases
    @Test
    void testDeleteMovie_ToggleToUnavailable() {
        int movieId = 1;
        movie.setStatus("AVAILABLE");

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));

        movieService.deleteMovie(movieId);

        assertEquals("UNAVAILABLE", movie.getStatus());
        verify(movieRepository, times(1)).save(movie);
    }

    @Test
    void testDeleteMovie_ToggleToAvailable() {
        int movieId = 1;
        movie.setStatus("UNAVAILABLE");

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));

        movieService.deleteMovie(movieId);

        assertEquals("AVAILABLE", movie.getStatus());
        verify(movieRepository, times(1)).save(movie);
    }

    @Test
    void testDeleteMovie_NotFound() {
        int movieId = 1;
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> movieService.deleteMovie(movieId));
    }

    // getAllMovies() Test Cases
    @Test
    void testGetAllMovies_Success() {
        when(movieRepository.findAll()).thenReturn(List.of(movie));
        when(movieMapper.toDto(movie)).thenReturn(movieDTO);

        List<MovieDTO> result = movieService.getAllMovies();

        assertEquals(1, result.size());
    }

    @Test
    void testGetAllMovies_NoMovies() {
        when(movieRepository.findAll()).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> movieService.getAllMovies());
    }
    
 // getMovieById() Test Cases
    @Test
    void testGetMovieById_Found() {
        int movieId = 1;
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(movieMapper.toDto(movie)).thenReturn(movieDTO);

        MovieDTO result = movieService.getMovieById(movieId);

        assertEquals(movieDTO.getTitle(), result.getTitle());
    }

    @Test
    void testGetMovieById_NotFound() {
        int movieId = 1;
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> movieService.getMovieById(movieId));
    }

    // getAllAvailableMovies() Test Cases
    @Test
    void testGetAllAvailableMovies_Found() {
        movie.setStatus("AVAILABLE");
        when(movieRepository.getAllAvailableMovies()).thenReturn(List.of(movie));
        when(movieMapper.toDto(movie)).thenReturn(movieDTO);

        List<MovieDTO> result = movieService.getAllAvailableMovies();

        assertEquals(1, result.size());
        assertEquals("AVAILABLE", result.get(0).getStatus());
    }


    @Test
    void testGetAllAvailableMovies_NotFound() {
        when(movieRepository.getAllAvailableMovies()).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> movieService.getAllAvailableMovies());
    }

    // getMoviesByGenre() Test Cases
    @Test
    void testGetMoviesByGenre_Found() {
        String genre = "Sci-Fi";
        when(movieRepository.findByGenre(genre)).thenReturn(List.of(movie));
        when(movieMapper.toDto(movie)).thenReturn(movieDTO);

        List<MovieDTO> result = movieService.getMoviesByGenre(genre);

        assertEquals(1, result.size());
        assertEquals(genre, result.get(0).getGenre());
    }

    @Test
    void testGetMoviesByGenre_NotFound() {
        String genre = "Fantasy";
        when(movieRepository.findByGenre(genre)).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> movieService.getMoviesByGenre(genre));
    }

    @Test
    void testGetMoviesByGenre_NullOrEmpty() {
        assertThrows(IllegalArgumentException.class, () -> movieService.getMoviesByGenre(null));
        assertThrows(IllegalArgumentException.class, () -> movieService.getMoviesByGenre(""));
    }

}
