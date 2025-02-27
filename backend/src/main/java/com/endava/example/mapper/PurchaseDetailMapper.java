package com.endava.example.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.endava.example.dto.PurchaseDetailDTO;
import com.endava.example.entity.Movie;
import com.endava.example.entity.Purchase;
import com.endava.example.entity.PurchaseDetail;

@Component
public class PurchaseDetailMapper {
	
	@Autowired
	private MovieMapper movieMapper;

	public PurchaseDetail toEntity(PurchaseDetailDTO dto, Purchase purchase, Movie movie) {
		PurchaseDetail purchaseDetail = new PurchaseDetail();
		purchaseDetail.setMovie(movie);
		purchaseDetail.setPurchase(purchase);
		return purchaseDetail;
	}

	public PurchaseDetailDTO toDto(PurchaseDetail purchaseDetail) {
		PurchaseDetailDTO dto = new PurchaseDetailDTO();
		dto.setPurchaseDetailId(purchaseDetail.getPurchaseDetailId());
		dto.setMovieId(purchaseDetail.getMovie().getMovieId());
		dto.setPurchaseId(purchaseDetail.getPurchase().getPurchaseId());
		dto.setMovieDTO(movieMapper.toDto(purchaseDetail.getMovie()));
		return dto;
	}

}
