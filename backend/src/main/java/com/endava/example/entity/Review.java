package com.endava.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Review Entity represents a review in the system. This class maps to the
 * reviews table in the database.
 * 
 * The review entity contains information related to the review, such as
 * reviewText, user who has written that review , movie in which that review is
 * written, reported field which shows that the review is reported or not.
 * 
 * --Mappings: A single user can write many reviews, but each review is tied to
 * one specific user. A single movie can have many reviews, but each review is
 * tied to one specific movie.
 */

@Entity
@Data
@Table(name = "reviews")
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int reviewId;

	private String reviewText;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "movieId", nullable = false)
	private Movie movie;

	@Column(nullable = false)
	private boolean reported;

}
