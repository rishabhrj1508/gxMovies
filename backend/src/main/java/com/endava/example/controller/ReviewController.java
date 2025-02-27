package com.endava.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.endava.example.dto.ReviewDTO;
import com.endava.example.service.ReviewService;
import com.endava.example.utils.GenericResponse;

/**
 * ReviewController handles requests related to movie reviews.
 * Provides endpoints for creating, retrieving, deleting, and reporting reviews.
 */
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    // Injecting the ReviewService to handle business logic.
    private ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
		super();
		this.reviewService = reviewService;
	}

	/**
     * Creates a new review in the database whenever a user submits the review.
     * 
     * @param dto reviewDTO containing the review details.
     * @return ResponseEntity containing the created review and status CREATED 201.
     */
    @PostMapping("/add")
    public ResponseEntity<GenericResponse<ReviewDTO>> createReview(@RequestBody ReviewDTO dto) {
        ReviewDTO createdReview = reviewService.createReview(dto);
        return new ResponseEntity<>(new GenericResponse<>(true, "Review created successfully", createdReview), HttpStatus.CREATED);
    }

    /**
     * Retrieves all reviews for a specific movie.
     * 
     * @param movieId the ID of the movie whose reviews are being fetched.
     * @return ResponseEntity containing a list of ReviewDTO for the specified movie.
     */
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<GenericResponse<List<ReviewDTO>>> getAllReviewsOfMovie(@PathVariable int movieId) {
        List<ReviewDTO> reviews = reviewService.getAllReviewsOfMovie(movieId);
        return ResponseEntity.ok(new GenericResponse<>(true, "Reviews fetched successfully", reviews));
    }

    /**
     * Deletes a specific review from the database.
     * 
     * @param reviewId the ID of the review to be deleted.
     * @return ResponseEntity with status 204 No Content after successful deletion.
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<GenericResponse<Void>> deleteReview(@PathVariable int reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(new GenericResponse<>(true, "Review deleted successfully", null));
    }

    /**
     * Retrieves all reviews that have been reported for inappropriate content.
     * 
     * @return ResponseEntity containing a list of all reported reviews as ReviewDTO.
     */
    @GetMapping("/reported")
    public ResponseEntity<GenericResponse<List<ReviewDTO>>> getAllReportedReviews() {
        List<ReviewDTO> reportedReviews = reviewService.getAllReportedReviews();
        return ResponseEntity.ok(new GenericResponse<>(true, "Reported reviews fetched successfully", reportedReviews));
    }

    /**
     * Reports a review for inappropriate content.
     * 
     * @param reviewId the ID of the review to be reported.
     * @return ResponseEntity containing a message indicating the result of the operation.
     */
    @PatchMapping("/report/{reviewId}")
    public ResponseEntity<GenericResponse<String>> reportReview(@PathVariable int reviewId) {
        String message = reviewService.reportReview(reviewId);
        return ResponseEntity.ok(new GenericResponse<>(true, message, null));
    }
}
