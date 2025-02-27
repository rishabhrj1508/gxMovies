package com.endava.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.endava.example.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

	// Retrieves a list of reviews for a given movie based on the movie's ID.
	List<Review> findByMovie_MovieId(int movieId);

}
