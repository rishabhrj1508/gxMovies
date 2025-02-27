package com.endava.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.endava.example.entity.Purchase;
import com.endava.example.entity.PurchaseDetail;

@Repository
public interface PurchaseDetailRepository extends JpaRepository<PurchaseDetail, Integer> {

	// Retrieves a list of PurchaseDetails by the given purchase ID.
	List<PurchaseDetail> findByPurchase_PurchaseId(int purchaseId);

	// Retrieves a list of PurchaseDetails for a given purchase entity.
	List<PurchaseDetail> findByPurchase(Purchase purchase);

	// Retrieves a list of PurchaseDetails for a user based on the user's ID.
	List<PurchaseDetail> findByPurchase_User_UserId(int userId);

	// to check if movie is already purchased by user....
	boolean existsByPurchase_User_UserIdAndMovie_MovieId(int userId, int movieId);
}
