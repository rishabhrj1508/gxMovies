package com.endava.example.service;

import java.util.List;

import com.endava.example.dto.ReviewDTO;

public interface ReviewService {

	ReviewDTO createReview(ReviewDTO dto);

	void deleteReview(int reviewId);

	List<ReviewDTO> getAllReviewsOfMovie(int movieId);

	List<ReviewDTO> getAllReportedReviews();
	
	String reportReview(int reviewId);

}
