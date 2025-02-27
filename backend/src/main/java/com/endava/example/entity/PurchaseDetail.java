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
 * PurchaseDetail Entity representing the details of a specific movie in a
 * purchase. This class maps to the purchase_details table in the database. It
 * contains two many-to-one relationships: 1. A reference to the Purchase
 * entity, indicating which purchase this detail belongs to. 2. A reference to
 * the Movie entity, indicating which movie is part of the purchase.
 */
@Entity
@Data
@Table(name = "purchase_details")
public class PurchaseDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int purchaseDetailId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "purchaseId")
	private Purchase purchase;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "movieId")
	private Movie movie;

}
