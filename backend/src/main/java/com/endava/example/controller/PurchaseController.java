package com.endava.example.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.endava.example.dto.PurchaseDTO;
import com.endava.example.dto.PurchaseRequestDTO;
import com.endava.example.dto.PurchasedMovieDTO;
import com.endava.example.service.PurchaseService;
import com.endava.example.utils.GenericResponse;

/**
 * PurchaseController provides endpoints related to movie purchases, including
 * creating purchases, fetching user purchases, checking if a movie is purchased
 * by a user, and generating invoices.
 */
@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

	// Injecting the PurchaseService to handle business logic..
	private PurchaseService purchaseService;

	public PurchaseController(PurchaseService purchaseService) {
		super();
		this.purchaseService = purchaseService;
	}

	/**
	 * Creates a new Purchase in the database. This method takes a
	 * PurchaseRequestDTO and returns the PurchaseDTO.
	 * 
	 * @param dto purchase request details (userId, totalPrice, payment info).
	 * @return ResponseEntity containing the created PurchaseDTO wrapped in
	 *         GenericResponse.
	 */
	@PostMapping
	public ResponseEntity<GenericResponse<PurchaseDTO>> createPurchase(@RequestBody PurchaseRequestDTO dto) {
		PurchaseDTO createdPurchase = purchaseService.createPurchase(dto);
		return ResponseEntity.ok(new GenericResponse<>(true, "Purchase created successfully", createdPurchase));
	}

	/**
	 * Retrieves all purchases made by a specific user.
	 * 
	 * @param userId the ID of the user whose purchases are to be fetched.
	 * @return ResponseEntity containing a list of PurchaseDTOs wrapped in
	 *         GenericResponse.
	 */
	@GetMapping("/users/{userId}")
	public ResponseEntity<GenericResponse<List<PurchaseDTO>>> getUserPurchases(@PathVariable int userId) {
		List<PurchaseDTO> purchases = purchaseService.getPurchaseByUserId(userId);
		return ResponseEntity.ok(new GenericResponse<>(true, "User purchases fetched successfully", purchases));
	}

	/**
	 * Retrieves all movies purchased by a specific user.
	 * 
	 * @param userId The ID of the user whose purchased movies are to be fetched.
	 * @return ResponseEntity containing a list of PurchasedMovieDTOs wrapped in
	 *         GenericResponse.
	 */
	@GetMapping("/users/movies/{userId}")
	public ResponseEntity<GenericResponse<List<PurchasedMovieDTO>>> getPurchasedMovies(@PathVariable int userId) {
		List<PurchasedMovieDTO> purchasedMovies = purchaseService.getPurchasedMovieByUser(userId);
		return ResponseEntity.ok(new GenericResponse<>(true, "Purchased movies fetched successfully", purchasedMovies));
	}

	/**
	 * Checks if a particular movie is already purchased by the user.
	 * 
	 * @param userId  the ID of the user.
	 * @param movieId the ID of the movie.
	 * @return ResponseEntity containing a Boolean wrapped in GenericResponse
	 *         indicating whether the movie is purchased or not.
	 */
	@GetMapping("/users/movies/check")
	public ResponseEntity<GenericResponse<Boolean>> isMoviePurchased(@RequestParam int userId,
			@RequestParam int movieId) {
		boolean isPurchased = purchaseService.isMoviePurchasedByUser(userId, movieId);
		return ResponseEntity.ok(new GenericResponse<>(true, "Movie purchase check successful", isPurchased));
	}

	/**
	 * Generates an invoice for a given purchase and returns it as a
	 * downloadable-PDF.
	 * 
	 * @param purchaseId    the ID of the purchase for which the invoice is being
	 *                      generated.
	 * @param transactionId the transaction ID to be included in the invoice
	 *                      filename.
	 * @return ResponseEntity containing the PDF invoice byte array with appropriate
	 *         headers for download.
	 */

	@GetMapping("/invoice/{purchaseId}")
	public ResponseEntity<byte[]> generateInvoice(@PathVariable int purchaseId, @RequestParam String transactionId) {
		byte[] invoicePdf = purchaseService.generateInvoicePdf(purchaseId);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDispositionFormData("attachment", "invoice_" + transactionId + ".pdf");

		return ResponseEntity.ok().headers(headers).body(invoicePdf);
	}

}
