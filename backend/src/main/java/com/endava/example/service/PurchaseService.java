package com.endava.example.service;

import java.util.List;

import com.endava.example.dto.PurchaseDTO;
import com.endava.example.dto.PurchaseRequestDTO;
import com.endava.example.dto.PurchasedMovieDTO;

public interface PurchaseService {

	PurchaseDTO createPurchase(PurchaseRequestDTO dto);

	List<PurchaseDTO> getPurchaseByUserId(int userId);

	List<PurchasedMovieDTO> getPurchasedMovieByUser(int userId);

	boolean isMoviePurchasedByUser(int userId, int movieId);
	
	byte[] generateInvoicePdf(int purchaseId);

}
