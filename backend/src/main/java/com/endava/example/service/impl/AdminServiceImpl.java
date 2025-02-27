package com.endava.example.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.endava.example.dto.SummaryDTO;
import com.endava.example.repository.MovieRepository;
import com.endava.example.repository.PurchaseRepository;
import com.endava.example.repository.UserRepository;
import com.endava.example.service.AdminService;

/**
 * Implementation of the AdminService interface that provides administrative
 * functionalities. This service handles operations related to: - Retrieving a
 * summary of the platform's users, movies, and total revenue. - Fetching chart
 * data for different types of reports (e.g., movies by genre, revenue by genre,
 * top users).
 * 
 */
@Service
public class AdminServiceImpl implements AdminService {

	private UserRepository userRepository;

	private MovieRepository movieRepository;

	private PurchaseRepository purchaseRepository;

	public AdminServiceImpl(UserRepository userRepository, MovieRepository movieRepository,
			PurchaseRepository purchaseRepository) {
		this.userRepository = userRepository;
		this.movieRepository = movieRepository;
		this.purchaseRepository = purchaseRepository;
	}

	/**
	 * Retrieves a summary -- count of users,count of movies, and total revenue
	 * generated in the platform.
	 * 
	 * @return summary details containing the number of users, movies, and total
	 *         revenue
	 */
	@Override
	public SummaryDTO getSummary() {
		SummaryDTO dto = new SummaryDTO();
		dto.setNumberOfUsers(userRepository.count());
		dto.setNumberOfMovies(movieRepository.count());
		Double totalRevenue = purchaseRepository.getTotalRevenue();
		dto.setTotalRevenue(totalRevenue != null ? totalRevenue : 0.0);
		return dto;
	}

	/**
	 * Retrieves chart data based on the specified type.
	 *
	 * @param type of chart data to retrieve (e.g., "moviesByGenre",
	 *             "revenueByGenre", "topUsers")
	 * @return a map containing the chart data including a "series" key holding the
	 *         data.
	 * @throws IllegalArgumentException if the chart type is invalid
	 */
	@Override
	public Map<String, Object> getChartData(String type) {
		if (type == null || type.isBlank()) {
			throw new IllegalArgumentException("Chart type cannot be null or empty.");
		}

		Map<String, Object> chartData = new HashMap<>();

		switch (type) {
		case "moviesByGenre" -> chartData.put("series", movieRepository.countMoviesByGenre());
		case "revenueByGenre" -> chartData.put("series", purchaseRepository.getRevenueByGenre());
		case "topUsers" -> chartData.put("series", purchaseRepository.getTopUsers());
		default -> throw new IllegalArgumentException("Invalid chart type: " + type);
		}

		return chartData;
	}

}
