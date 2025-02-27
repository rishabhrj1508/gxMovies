package com.endava.example.mapper;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.endava.example.dto.MovieDTO;
import com.endava.example.entity.Movie;

@Component
public class MovieMapper {

	public MovieDTO toDto(Movie movie) {
		MovieDTO dto = new MovieDTO();
		dto.setMovieId(movie.getMovieId());
		dto.setTitle(movie.getTitle());
		dto.setDescription(movie.getDescription());
		dto.setGenre(movie.getGenre());
		dto.setReleaseDate(movie.getReleaseDate());
		dto.setAverageRating(movie.getAverageRating());
		dto.setPrice(movie.getPrice());
		dto.setPosterURL(movie.getPosterURL());
		dto.setTrailerURL(movie.getTrailerURL());
		dto.setStatus(movie.getStatus());
		return dto;

	}

	public Movie toEntity(MovieDTO dto) {
		Movie movie = new Movie();
		movie.setTitle(dto.getTitle());
		movie.setDescription(dto.getDescription());
		movie.setGenre(dto.getGenre());
		movie.setReleaseDate(dto.getReleaseDate());
		movie.setAverageRating(dto.getAverageRating());
		movie.setPrice(dto.getPrice());
		movie.setPosterURL(dto.getPosterURL());
		movie.setTrailerURL(dto.getTrailerURL());
		movie.setStatus("AVAILABLE");
		movie.setCreatedAt(LocalDate.now());
		movie.setUpdatedAt(LocalDate.now());
		return movie;

	}

}
