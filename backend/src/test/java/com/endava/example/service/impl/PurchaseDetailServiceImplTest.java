package com.endava.example.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.endava.example.dto.PurchaseDetailDTO;
import com.endava.example.entity.Purchase;
import com.endava.example.entity.PurchaseDetail;
import com.endava.example.exceptions.ResourceNotFoundException;
import com.endava.example.mapper.PurchaseDetailMapper;
import com.endava.example.repository.PurchaseDetailRepository;
import com.endava.example.repository.PurchaseRepository;

@ExtendWith(MockitoExtension.class)
class PurchaseDetailServiceImplTest {

	@Mock
	private PurchaseDetailRepository purchaseDetailRepository;

	@Mock
	private PurchaseRepository purchaseRepository;

	@Mock
	private PurchaseDetailMapper purchaseDetailMapper;

	@InjectMocks
	private PurchaseDetailServiceImpl purchaseDetailService;

	@Test
	void testGetDetailsByPurchaseId_Success() {

		Purchase purchase = new Purchase();
		purchase.setPurchaseId(1);

		PurchaseDetail purchaseDetail = new PurchaseDetail();
		purchaseDetail.setPurchase(purchase);

		PurchaseDetailDTO dto = new PurchaseDetailDTO();

		when(purchaseRepository.findById(1)).thenReturn(Optional.of(purchase));
		when(purchaseDetailRepository.findByPurchase(purchase)).thenReturn(List.of(purchaseDetail));
		when(purchaseDetailMapper.toDto(purchaseDetail)).thenReturn(dto);

		List<PurchaseDetailDTO> expected = List.of(dto);
		List<PurchaseDetailDTO> result = purchaseDetailService.getDetailsByPurchaseId(1);

		assertNotNull(result);
		assertEquals(expected, result);
		assertEquals(1, result.size());
	}

	@Test
	void testGetDetailsByPurchaseId_InvalidPurchaseId() {
		int invalidPurchaseId = -1;

		assertThrows(IllegalArgumentException.class,
				() -> purchaseDetailService.getDetailsByPurchaseId(invalidPurchaseId));

	}

	@Test
	void getDetailsByPurchaseId_ShouldThrowException_WhenPurchaseNotFound() {
		when(purchaseRepository.findById(999)).thenReturn(Optional.empty());

		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
				() -> purchaseDetailService.getDetailsByPurchaseId(999));

		assertEquals("Purchase not found with ID: 999", exception.getMessage());

	}
}
