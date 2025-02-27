package com.endava.example.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.endava.example.dto.ReviewDTO;
import com.endava.example.exceptions.ResourceAlreadyExistsException;
import com.endava.example.exceptions.ResourceNotFoundException;
import com.endava.example.service.ReviewService;
import com.endava.example.utils.JwtAuthenticationFilter;
import com.endava.example.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private ReviewService reviewService;

	@MockitoBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@MockitoBean
	private JwtUtils jwtUtils;

	@Test
	void testCreateReview_Success() throws Exception {
		ReviewDTO reviewDTO = new ReviewDTO();
		reviewDTO.setReviewText("Great movie!");
		when(reviewService.createReview(any(ReviewDTO.class))).thenReturn(reviewDTO);

		mockMvc.perform(post("/api/reviews/add").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(reviewDTO))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Review created successfully"))
				.andExpect(jsonPath("$.data.reviewText").value("Great movie!"));

		verify(reviewService).createReview(any(ReviewDTO.class));
	}

	@Test
	void testCreateReview_Failure_DuplicateReview() throws Exception {
		ReviewDTO reviewDTO = new ReviewDTO();
		reviewDTO.setReviewText("Duplicate review");
		when(reviewService.createReview(any(ReviewDTO.class)))
				.thenThrow(new ResourceAlreadyExistsException("Review already exists"));

		mockMvc.perform(post("/api/reviews/add").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(reviewDTO))).andExpect(status().isConflict())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("Review already exists"));
	}

	@Test
	void testGetAllReviewsOfMovie_Success() throws Exception {
		int movieId = 1;
		ReviewDTO review1 = new ReviewDTO();
		review1.setReviewId(1);
		review1.setReviewText("Great movie!");
		ReviewDTO review2 = new ReviewDTO();
		review2.setReviewId(2);
		review2.setReviewText("Awesome!");
		List<ReviewDTO> reviews = List.of(review1, review2);

		when(reviewService.getAllReviewsOfMovie(movieId)).thenReturn(reviews);

		mockMvc.perform(get("/api/reviews/movie/{movieId}", movieId)).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data[0].reviewText").value("Great movie!"))
				.andExpect(jsonPath("$.data[1].reviewText").value("Awesome!"));

		verify(reviewService).getAllReviewsOfMovie(movieId);
	}

	@Test
	void testGetAllReviewsOfMovie_NoReviews() throws Exception {
		int movieId = 1;
		when(reviewService.getAllReviewsOfMovie(movieId))
				.thenThrow(new ResourceNotFoundException("No reviews found for this movie."));

		mockMvc.perform(get("/api/reviews/movie/{movieId}", movieId)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("No reviews found for this movie."));

		verify(reviewService).getAllReviewsOfMovie(movieId);
	}

	@Test
	void testDeleteReview_Success() throws Exception {
		int reviewId = 1;

		mockMvc.perform(delete("/api/reviews/{reviewId}", reviewId)).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Review deleted successfully"));

		verify(reviewService).deleteReview(reviewId);
	}

	@Test
	void testDeleteReview_NotFound() throws Exception {
		int reviewId = 99;
		doThrow(new ResourceNotFoundException("Review with this ID doesn't exist: " + reviewId)).when(reviewService)
				.deleteReview(reviewId);

		mockMvc.perform(delete("/api/reviews/{reviewId}", reviewId)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("Review with this ID doesn't exist: " + reviewId));

		verify(reviewService).deleteReview(reviewId);
	}

	@Test
	void testGetAllReportedReviews_Success() throws Exception {
		ReviewDTO review1 = new ReviewDTO();
		review1.setReviewId(1);
		review1.setReviewText("Inappropriate content");
		review1.setReported(true);

		ReviewDTO review2 = new ReviewDTO();
		review2.setReviewId(2);
		review2.setReviewText("Spam");
		review2.setReported(true);

		List<ReviewDTO> reportedReviews = List.of(review1, review2);

		when(reviewService.getAllReportedReviews()).thenReturn(reportedReviews);

		mockMvc.perform(get("/api/reviews/reported")).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Reported reviews fetched successfully"))
				.andExpect(jsonPath("$.data[0].reviewText").value("Inappropriate content"))
				.andExpect(jsonPath("$.data[1].reviewText").value("Spam"));

		verify(reviewService).getAllReportedReviews();
	}

	@Test
	void testGetAllReportedReviews_NoReports() throws Exception {
		when(reviewService.getAllReportedReviews()).thenReturn(List.of());

		mockMvc.perform(get("/api/reviews/reported")).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Reported reviews fetched successfully"))
				.andExpect(jsonPath("$.data").isEmpty());

		verify(reviewService).getAllReportedReviews();
	}

	@Test
	void testReportReview_Success() throws Exception {
		int reviewId = 1;
		String successMessage = "Review reported successfully";

		when(reviewService.reportReview(reviewId)).thenReturn(successMessage);

		mockMvc.perform(patch("/api/reviews/report/{reviewId}", reviewId)).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Review reported successfully"))
				.andExpect(jsonPath("$.data").isEmpty());

		verify(reviewService).reportReview(reviewId);
	}

	@Test
	void testReportReview_ReviewNotFound() throws Exception {
		int reviewId = 99;
		when(reviewService.reportReview(reviewId)).thenThrow(new ResourceNotFoundException("Review not found"));

		mockMvc.perform(patch("/api/reviews/report/{reviewId}", reviewId)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("Review not found"));

		verify(reviewService).reportReview(reviewId);
	}

}
