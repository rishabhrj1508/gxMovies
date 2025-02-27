package com.endava.example.dto;

import lombok.Data;

/**
 * ReviewDTO contains the details of a movie review, including user information
 * - id and userName , movie info - movieId and movieName ,the review content,
 * and its report status.
 */
@Data
public class ReviewDTO {

	private int reviewId;
	private int userId;
	private int movieId;
	private String username;
	private String moviename;
	private String reviewText;
	private boolean reported;

}
