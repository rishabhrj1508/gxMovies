package com.endava.example.service;

import java.util.List;

import com.endava.example.dto.PurchaseDetailDTO;

public interface PurchaseDetailService {

	List<PurchaseDetailDTO> getDetailsByPurchaseId(int purchaseId);

}
