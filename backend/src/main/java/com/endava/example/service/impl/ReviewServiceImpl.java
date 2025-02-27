package com.endava.example.service.impl;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.endava.example.dto.ReviewDTO;
import com.endava.example.entity.Movie;
import com.endava.example.entity.Review;
import com.endava.example.entity.User;
import com.endava.example.exceptions.ResourceNotFoundException;
import com.endava.example.mapper.ReviewMapper;
import com.endava.example.repository.MovieRepository;
import com.endava.example.repository.ReviewRepository;
import com.endava.example.repository.UserRepository;
import com.endava.example.service.ReviewService;

/**
 * Implementation of the ReviewService interface that handles business logic
 * related to reviews. This service provides methods for creating, deleting,
 * fetching, and reporting reviews associated with movies and users.
 */
@Service
public class ReviewServiceImpl implements ReviewService {

	private ReviewRepository reviewRepository;

	private UserRepository userRepository;

	private MovieRepository movieRepository;

	private ReviewMapper reviewMapper;

	public ReviewServiceImpl(ReviewRepository reviewRepository, UserRepository userRepository,
			MovieRepository movieRepository, ReviewMapper reviewMapper) {
		super();
		this.reviewRepository = reviewRepository;
		this.userRepository = userRepository;
		this.movieRepository = movieRepository;
		this.reviewMapper = reviewMapper;
	}

	/**
	 * Creates a new review for a movie by a user.
	 * 
	 * @param dto The details of the review to be created.
	 * @return The created review's details as a ReviewDTO.
	 * @throws IllegalArgumentException  if the review DTO is null or invalid.
	 * @throws ResourceNotFoundException if the user or movie does not exist.
	 */
	@Override
	public ReviewDTO createReview(ReviewDTO dto) {

		if (dto == null) {
			throw new IllegalArgumentException("Review DTO can't be null");
		}

		User user = userRepository.findById(dto.getUserId()).orElseThrow(
				() -> new ResourceNotFoundException("User with this ID does not exist: " + dto.getUserId()));

		Movie movie = movieRepository.findById(dto.getMovieId()).orElseThrow(
				() -> new ResourceNotFoundException("Movie with this ID does not exist: " + dto.getMovieId()));

		Review review = reviewMapper.toEntity(dto, user, movie);
		review = reviewRepository.save(review);

		return reviewMapper.toDto(review);
	}

	/**
	 * Deletes the review from the repository based on reviewId.
	 * 
	 * @param reviewId The ID of the review to be deleted.
	 * @throws ResourceNotFoundException if the review does not exist.
	 */
	@Override
	public void deleteReview(int reviewId) {
		if (!reviewRepository.existsById(reviewId)) {
			throw new ResourceNotFoundException("Review with this ID doesn't exist: " + reviewId);
		}
		reviewRepository.deleteById(reviewId);
	}

	/**
	 * Fetches all reviews for a specific movie by movieId.
	 * 
	 * @param movieId The ID of the movie for which reviews are fetched.
	 * @return A list of ReviewDTO containing all reviews for the specified movie.
	 * @throws ResourceNotFoundException if no reviews exist for the provided movie.
	 */
	@Override
	public List<ReviewDTO> getAllReviewsOfMovie(int movieId) {
		List<ReviewDTO> reviews = reviewRepository.findByMovie_MovieId(movieId).stream().map(reviewMapper::toDto)
				.sorted(Comparator.comparing(ReviewDTO::getReviewId).reversed()).toList();

		if (reviews.isEmpty()) {
			throw new ResourceNotFoundException("No reviews found for movie with ID: " + movieId);
		}

		return reviews;
	}

	/**
	 * Fetches all reported reviews.
	 * 
	 * @return A list of ReviewDTO containing all reported reviews.
	 */
	@Override
	public List<ReviewDTO> getAllReportedReviews() {
		return reviewRepository.findAll().stream().filter(Review::isReported).map(reviewMapper::toDto).toList();
	}

	/**
	 * Reports a review by marking it as reported.
	 * 
	 * @param reviewId The ID of the review to be reported.
	 * @return A message indicating whether the review was reported or already
	 *         reported.
	 * @throws ResourceNotFoundException if the review does not exist.
	 */
	@Override
	public String reportReview(int reviewId) {

		Review review = reviewRepository.findById(reviewId)
				.orElseThrow(() -> new ResourceNotFoundException("Review with this ID doesn't exist: " + reviewId));

		if (review.isReported()) {
			return "This review is already reported. We will take further action.";
		}

		review.setReported(true);
		reviewRepository.save(review);

		return "Review reported successfully.";
	}
}
