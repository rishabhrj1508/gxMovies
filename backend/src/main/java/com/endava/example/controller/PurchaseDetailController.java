package com.endava.example.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.endava.example.dto.PurchaseDetailDTO;
import com.endava.example.service.PurchaseDetailService;
import com.endava.example.utils.GenericResponse;

/**
 * PurchaseDetailController provides endpoints to fetch details associated with
 * a specific purchase.
 */
@RestController
@RequestMapping("/api/purchasedetails")
public class PurchaseDetailController {

	// Injecting PurchaseDetailService to handle business logic..
	private PurchaseDetailService purchaseDetailService;

	public PurchaseDetailController(PurchaseDetailService purchaseDetailService) {
		super();
		this.purchaseDetailService = purchaseDetailService;
	}

	/**
	 * Retrieves all details associated with a specific purchase.
	 * 
	 * @param purchaseId the id of the purchase for which details are being fetched.
	 * @return ResponseEntity containing a list of PurchaseDetailDTOs wrapped in
	 *         GenericResponse.
	 */
	@GetMapping("/purchase/{purchaseId}")
	public ResponseEntity<GenericResponse<List<PurchaseDetailDTO>>> getPurchaseDetailsByPurchaseId(
			@PathVariable int purchaseId) {
		List<PurchaseDetailDTO> purchaseDetails = purchaseDetailService.getDetailsByPurchaseId(purchaseId);
		return ResponseEntity.ok(new GenericResponse<>(true, "Purchase details fetched successfully", purchaseDetails));
	}
}
