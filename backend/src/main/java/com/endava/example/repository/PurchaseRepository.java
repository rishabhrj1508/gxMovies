package com.endava.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.endava.example.entity.Purchase;

/**
 * 
 */

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Integer> {

	List<Purchase> findByUser_UserId(int userId);

	@Query("SELECT SUM(p.totalPrice) FROM Purchase p")
	Double getTotalRevenue();
    
	@Query("SELECT m.genre, SUM(m.price) FROM PurchaseDetail pd JOIN pd.movie m GROUP BY m.genre")
	List<Object[]> getRevenueByGenre();
    
    @Query("SELECT u.fullName, COUNT(p) FROM Purchase p JOIN p.user u GROUP BY u ORDER BY COUNT(p) DESC LIMIT 5")
    List<Object[]> getTopUsers();
	
}
