package com.endava.example.entity;

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
 * The Favorite entity represents a user's favorite movie in the system, and is
 * mapped to the favorites table in the database.
 * 
 * The entity is uniquely identified by the favoriteId. The userId and movieId
 * are used to represent the associations between a user and their favorite
 * movie.
 * 
 * Many-to-One from Favorite to User --- Multiple Favorite entities can reference
 * the same User (i.e., one user can have many favorite movies). 
 * 
 * Many-to-One from Favorite to Movie--- Multiple Favorite entities can reference the same
 * Movie (i.e., one movie can be marked as a favorite by many users).
 * 
 * Both relationships use FetchType.LAZY to load the entities (User and Movie)
 * only when needed, optimizing database performance.
 */
@Entity
@Data
@Table(name = "favorites")
public class Favorite {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int favoriteId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "movieId", nullable = false)
	private Movie movie;

}
