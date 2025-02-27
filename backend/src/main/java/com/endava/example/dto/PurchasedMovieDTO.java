package com.endava.example.dto;

import lombok.Data;

/**
 * PurchasedMovieDTO holds the details of the movie purchased by the user..
 * includes the id of the user who bought the movie , the id of the movie which
 * is bought , title of the movie , url's of poster and trailer of that movie ,
 * and the status of the movie purchased..
 */
@Data
public class PurchasedMovieDTO {

	private int userId;
	private int movieId;
	private String title;
	private String posterURL;
	private String trailerURL;
	private String status;

}
