package com.endava.example.mapper;

import org.springframework.stereotype.Component;

import com.endava.example.dto.ReviewDTO;
import com.endava.example.entity.Movie;
import com.endava.example.entity.Review;
import com.endava.example.entity.User;

@Component
public class ReviewMapper {

	public Review toEntity(ReviewDTO dto, User user, Movie movie) {
		Review review = new Review();
		review.setUser(user);
		review.setMovie(movie);
		review.setReviewText(dto.getReviewText());
		review.setReported(dto.isReported());
		return review;
	}

	public ReviewDTO toDto(Review review) {
		ReviewDTO dto = new ReviewDTO();
		dto.setReviewId(review.getReviewId());
		dto.setUserId(review.getUser().getUserId());
		dto.setMovieId(review.getMovie().getMovieId());
		dto.setMoviename(review.getMovie().getTitle());
		dto.setUsername(review.getUser().getFullName());
		dto.setReviewText(review.getReviewText());
		dto.setReported(review.isReported());
		return dto;
	}

}
