package com.endava.example.mapper;

import org.springframework.stereotype.Component;

import com.endava.example.dto.PurchaseDTO;
import com.endava.example.dto.PurchaseRequestDTO;
import com.endava.example.entity.Purchase;
import com.endava.example.entity.User;

@Component
public class PurchaseMapper {

	public Purchase toEntity(PurchaseRequestDTO dto, User user) {
		Purchase purchase = new Purchase();
		purchase.setUser(user);
		purchase.setTotalPrice(dto.getTotalPrice());
		purchase.setPaymentMethod(dto.getPaymentMethod());
		return purchase;

	}

	public PurchaseDTO toDto(Purchase purchase) {
		PurchaseDTO dto = new PurchaseDTO();
		dto.setPurchaseId(purchase.getPurchaseId());
		dto.setTransactionId(purchase.getTransactionId());
		dto.setUserId(purchase.getUser().getUserId());
		dto.setTotalPrice(purchase.getTotalPrice());
		dto.setPurchaseDate(purchase.getPurchaseDate());
		dto.setPaymentMethod(purchase.getPaymentMethod());
		return dto;
	}

}
