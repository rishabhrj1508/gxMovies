package com.endava.example.dto;

import java.util.List;

import lombok.Data;

/**
 * PurchaseRequestDTO contains the details of a purchase request made by a user,
 * including the user ID, list of selected movie IDs, total price, transactionId
 * and payment method..
 */
@Data
public class PurchaseRequestDTO {

	private int userId;
	private List<Integer> movieIds;
	private double totalPrice;
	private String transactionId;
	private String paymentMethod;
}
