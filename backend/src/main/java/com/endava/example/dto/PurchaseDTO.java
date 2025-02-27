package com.endava.example.dto;

import java.time.LocalDate;

import lombok.Data;

/**
 * PurchaseDTO holds information about a purchase, including the purchase ID,
 * transaction details, payment method, user ID, total price, and the date of
 * the purchase.
 */
@Data
public class PurchaseDTO {

	private int purchaseId;
	private String transactionId;
	private String paymentMethod;
	private int userId;
	private double totalPrice;
	private LocalDate purchaseDate;

}
