package com.endava.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.endava.example.entity.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {

	// get the movie by their title
	Optional<Movie> findByTitleIgnoreCase(String title);

	// get all available movies which is available
	@Query("SELECT m FROM Movie m WHERE m.status = 'AVAILABLE'")
	List<Movie> getAllAvailableMovies();

	// get all movies with the given status
	List<Movie> findByStatus(String status);

	// get all movies with the given genre
	List<Movie> findByGenre(String genre);

	// get all movies with the given title
	List<Movie> findByTitleContainingIgnoreCase(String title);

	// get count of movies based on genre , returns a object of genre and its
	// respective count of movies
	@Query("SELECT m.genre , COUNT(m) FROM Movie m GROUP BY m.genre")
	List<Object[]> countMoviesByGenre();

}
