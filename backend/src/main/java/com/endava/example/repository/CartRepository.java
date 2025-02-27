package com.endava.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.endava.example.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

    // To find the list of cart items of the user
    List<Cart> findByUser_UserId(int userId);

    // To get multiple cart items by userId and list of movieIds
    List<Cart> findByUser_UserIdAndMovie_MovieIdIn(int userId, List<Integer> movieIds);

    // To check if a movie is already in the user's cart
    boolean existsByUser_UserIdAndMovie_MovieId(int userId, int movieId);

    // To find a specific cart item by userId and movieId
    Optional<Cart> findByUser_UserIdAndMovie_MovieId(int userId, int movieId);
}


