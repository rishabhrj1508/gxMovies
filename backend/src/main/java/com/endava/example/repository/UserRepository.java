package com.endava.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.endava.example.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	// gets the list of users with the status - active
	@Query("SELECT u FROM User u WHERE u.status = 'ACTIVE'")
	List<User> getAllActiveUsers();

	// gets the user by their email
	Optional<User> findByEmail(String email);

	// check if any user exists for the given role
	boolean existsByRole(String role);

	// gets the count of the user based on given role
	int countByRole(String role);

}
