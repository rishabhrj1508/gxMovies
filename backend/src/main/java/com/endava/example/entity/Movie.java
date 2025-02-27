package com.endava.example.entity;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Movie Entity representing a movie in the system. This class maps to the
 * movies table in the database. It contains details about the movie, including
 * its title, description, genre, release date, average rating, price, and URLs
 * for the poster and trailer. The movie can have different statuses such as
 * "AVAILABLE" or "UNAVAILABLE." This entity also contains creation and update
 * date.
 */

@Entity
@Data
@Table(name = "movies")
public class Movie {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int movieId;

	@Column(nullable = false, unique = true)
	private String title;

	@Column(nullable = false)
	private String description;

	@Column(nullable = false)
	private String genre;

	@Column(nullable = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate releaseDate;

	@Column(nullable = false)
	private double averageRating;

	@Column(nullable = false)
	private double price;

	@Column(nullable = false)
	private String posterURL;

	@Column(nullable = false)
	private String trailerURL;

	@Column(nullable = false)
	private String status = "AVAILABLE"; // or UNAVAILABLE

	@Column(nullable = false)
	private LocalDate createdAt;

	@Column
	private LocalDate updatedAt;

}
