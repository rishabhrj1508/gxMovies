package com.endava.example.entity;

import java.time.LocalDate;

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
 * Purchase Entity representing a user's purchase in the system. This class maps
 * to the purchases table in the database. It contains details about the
 * purchase, such as the transaction ID, the user who made the purchase, the
 * payment method, the total price of the purchase, and the date the purchase
 * was made.
 */

@Entity
@Data
@Table(name = "purchases")
public class Purchase {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int purchaseId;

	@Column(nullable = false)
	private String transactionId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")
	private User user;

	@Column
	private String paymentMethod;

	@Column(nullable = false)
	private double totalPrice;

	@Column(nullable = false)
	private LocalDate purchaseDate;

//	@OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL)
//	private List<PurchaseDetail> purchaseDetails;

}
