package com.endava.example.dto;

import java.time.LocalDate;

import lombok.Data;

/**
 * MovieDTO holds information about movies, including the movie's unique ID,
 * title , description , genre , releaseDate , averageRating , price , url's of
 * poster and trailer and its availability status..
 */
@Data
public class MovieDTO {

	private int movieId;
	private String title;
	private String description;
	private String genre;
	private LocalDate releaseDate;
	private double averageRating;
	private double price;
	private String posterURL;
	private String trailerURL;
	private String status;

}
