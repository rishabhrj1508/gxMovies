package com.endava.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.endava.example.entity.Favorite;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {

	//to find the list of favorites of the user
	List<Favorite> findByUser_UserId(int userId);

	// To check if that movie is already added as favorite by user
	Optional<Favorite> findByUser_UserIdAndMovie_MovieId(int userId, int movieId);

}
