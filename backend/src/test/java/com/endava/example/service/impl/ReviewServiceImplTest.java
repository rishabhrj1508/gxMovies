package com.endava.example.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.endava.example.dto.ReviewDTO;
import com.endava.example.entity.Movie;
import com.endava.example.entity.Review;
import com.endava.example.entity.User;
import com.endava.example.exceptions.ResourceNotFoundException;
import com.endava.example.mapper.ReviewMapper;
import com.endava.example.repository.MovieRepository;
import com.endava.example.repository.ReviewRepository;
import com.endava.example.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private MovieRepository movieRepository;

	@Mock
	private ReviewMapper reviewMapper;

	@InjectMocks
	private ReviewServiceImpl reviewService;

	@Test
	void testCreateReview_Success() {
		ReviewDTO dto = new ReviewDTO();
		dto.setUserId(1);
		dto.setMovieId(1);

		User user = new User();
		Movie movie = new Movie();
		Review review = new Review();

		when(userRepository.findById(1)).thenReturn(Optional.of(user));
		when(movieRepository.findById(1)).thenReturn(Optional.of(movie));
		when(reviewMapper.toEntity(dto, user, movie)).thenReturn(review);
		when(reviewRepository.save(review)).thenReturn(review);
		when(reviewMapper.toDto(review)).thenReturn(dto);

		ReviewDTO result = reviewService.createReview(dto);

		assertNotNull(result);
		assertEquals(dto, result);
		verify(reviewRepository, times(1)).save(review);
	}

	@Test
	void testCreateReview_NullDTO() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			reviewService.createReview(null);
		});

		assertEquals("Review DTO can't be null", exception.getMessage());
	}

	@Test
	void testCreateReview_UserNotFound() {
		ReviewDTO dto = new ReviewDTO();
		dto.setUserId(999);
		dto.setMovieId(1);

		when(userRepository.findById(999)).thenReturn(Optional.empty());

		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
			reviewService.createReview(dto);
		});

		assertEquals("User with this ID does not exist: 999", exception.getMessage());
	}

	@Test
	void testCreateReview_MovieNotFound() {
		ReviewDTO dto = new ReviewDTO();
		dto.setUserId(1);
		dto.setMovieId(999);

		User user = new User();
		when(userRepository.findById(1)).thenReturn(Optional.of(user));
		when(movieRepository.findById(999)).thenReturn(Optional.empty());

		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
			reviewService.createReview(dto);
		});

		assertEquals("Movie with this ID does not exist: 999", exception.getMessage());
	}

	@Test
	void testDeleteReview_Success() {
		int reviewId = 1;
		when(reviewRepository.existsById(reviewId)).thenReturn(true);
		reviewService.deleteReview(reviewId);
		verify(reviewRepository).deleteById(reviewId);
	}

	@Test
	void testDeleteReview_ReviewNotFound() {
		int reviewId = 999;
		when(reviewRepository.existsById(reviewId)).thenReturn(false);
		assertThrows(ResourceNotFoundException.class, () -> reviewService.deleteReview(reviewId));
	}

	@Test
	void testGetAllReviewsOfMovie_Success() {
		int movieId = 1;

		Review review1 = new Review();
		review1.setReviewId(1);
		Review review2 = new Review();
		review2.setReviewId(2);
		List<Review> reviews = List.of(review1, review2);

		ReviewDTO dto1 = new ReviewDTO();
		dto1.setReviewId(1);
		ReviewDTO dto2 = new ReviewDTO();
		dto2.setReviewId(2);

		when(reviewRepository.findByMovie_MovieId(movieId)).thenReturn(reviews);
		when(reviewMapper.toDto(review1)).thenReturn(dto1);
		when(reviewMapper.toDto(review2)).thenReturn(dto2);

		List<ReviewDTO> expected = List.of(dto2, dto1);
		List<ReviewDTO> result = reviewService.getAllReviewsOfMovie(movieId);
		assertEquals(result, expected);
	}

	@Test
	void getAllReviewsOfMovie_EmptyList() {
		int movieId = 99;
		List<Review> emptyList = Collections.emptyList();
		when(reviewRepository.findByMovie_MovieId(movieId)).thenReturn(emptyList);

		ResourceNotFoundException thrownException = assertThrows(ResourceNotFoundException.class,
				() -> reviewService.getAllReviewsOfMovie(movieId));

		assertEquals("No reviews found for movie with ID: " + movieId, thrownException.getMessage());

	}

	@Test
	void testGetAllReportedReviews_Success() {
		Review review1 = new Review();
		review1.setReviewId(1);
		review1.setReported(true);

		Review review2 = new Review();
		review2.setReviewId(2);
		review2.setReported(true);

		List<Review> reportedReviews = List.of(review1, review2);

		ReviewDTO dto1 = new ReviewDTO();
		dto1.setReviewId(1);

		ReviewDTO dto2 = new ReviewDTO();
		dto2.setReviewId(2);

		when(reviewRepository.findAll()).thenReturn(reportedReviews);
		when(reviewMapper.toDto(review1)).thenReturn(dto1);
		when(reviewMapper.toDto(review2)).thenReturn(dto2);

		List<ReviewDTO> result = reviewService.getAllReportedReviews();

		List<ReviewDTO> expected = List.of(dto1, dto2);
		assertEquals(expected, result);
	}

	@Test
	void testReportReview_Success() {
		int reviewId = 1;
		Review review = new Review();
		review.setReviewId(reviewId);
		review.setReported(false);

		when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
		when(reviewRepository.save(any(Review.class))).thenReturn(review);

		String result = reviewService.reportReview(reviewId);

		assertEquals("Review reported successfully.", result);
		assertTrue(review.isReported());

	}

	@Test
	void testReportReview_AlreadyReported() {
		int reviewId = 1;
		Review review = new Review();
		review.setReviewId(reviewId);
		review.setReported(true);

		when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

		String result = reviewService.reportReview(reviewId);

		assertEquals("This review is already reported. We will take further action.", result);

	}

	@Test
	void testReportReview_NotFound() {
		int reviewId = 1;
		when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> reviewService.reportReview(reviewId));
	}

}
