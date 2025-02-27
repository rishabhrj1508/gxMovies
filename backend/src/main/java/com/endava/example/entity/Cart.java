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
 * The Cart entity represents a shopping cart in the system and is mapped to the
 * carts table in the database.
 * 
 * The cart is uniquely identified by the cartId, and both the userId and
 * movieId are used to represent relation between the cart, the user, and the
 * movie.
 * 
 * This entity also establishes two many-to-one relationships: - A User can have
 * multiple carts. - A Movie can appear in multiple carts.
 * 
 * The relationships use FetchType.LAZY to load the entities (User and Movie)
 * only when necessary, helps to optimize database queries.
 */
@Entity
@Data
@Table(name = "carts")
public class Cart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int cartId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "movieId", nullable = false)
	private Movie movie;
}
