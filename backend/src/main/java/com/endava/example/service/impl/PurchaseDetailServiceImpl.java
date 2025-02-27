package com.endava.example.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.endava.example.dto.PurchaseDetailDTO;
import com.endava.example.entity.Purchase;
import com.endava.example.exceptions.ResourceNotFoundException;
import com.endava.example.mapper.PurchaseDetailMapper;
import com.endava.example.repository.PurchaseDetailRepository;
import com.endava.example.repository.PurchaseRepository;
import com.endava.example.service.PurchaseDetailService;

/**
 * Implementation of the PurchaseDetailService interface. Provides operations
 * for retrieving purchase details by purchase ID.
 */
@Service
public class PurchaseDetailServiceImpl implements PurchaseDetailService {

	private PurchaseDetailRepository purchaseDetailRepository;

	private PurchaseRepository purchaseRepository;

	private PurchaseDetailMapper purchaseDetailMapper;

	public PurchaseDetailServiceImpl(PurchaseDetailRepository purchaseDetailRepository,
			PurchaseRepository purchaseRepository, PurchaseDetailMapper purchaseDetailMapper) {
		this.purchaseDetailRepository = purchaseDetailRepository;
		this.purchaseRepository = purchaseRepository;
		this.purchaseDetailMapper = purchaseDetailMapper;
	}

	/**
	 * Retrieves the purchase details associated with a given purchase ID.
	 * 
	 * @param purchaseId The ID of the purchase.
	 * @return List of purchase details as DTOs.
	 * @throws IllegalArgumentException  if the purchase ID is invalid.
	 * @throws ResourceNotFoundException if no purchase or purchase details are
	 *                                   found.
	 */
	@Transactional(readOnly = true)
	@Override
	public List<PurchaseDetailDTO> getDetailsByPurchaseId(int purchaseId) {

		if (purchaseId <= 0) {
			throw new IllegalArgumentException("Invalid purchase ID: " + purchaseId);
		}

		Purchase purchase = purchaseRepository.findById(purchaseId)
				.orElseThrow(() -> new ResourceNotFoundException("Purchase not found with ID: " + purchaseId));

		return purchaseDetailRepository.findByPurchase(purchase).stream().map(purchaseDetailMapper::toDto).toList();
	}

}
