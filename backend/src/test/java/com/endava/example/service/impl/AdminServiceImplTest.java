package com.endava.example.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.endava.example.dto.SummaryDTO;
import com.endava.example.repository.MovieRepository;
import com.endava.example.repository.PurchaseRepository;
import com.endava.example.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private MovieRepository movieRepository;

	@Mock
	private PurchaseRepository purchaseRepository;

	@InjectMocks
	private AdminServiceImpl adminService;

	@Test
	void testGetSummary() {
		when(userRepository.count()).thenReturn(100L);
		when(movieRepository.count()).thenReturn(50L);
		when(purchaseRepository.getTotalRevenue()).thenReturn(2000.0);

		SummaryDTO summary = adminService.getSummary();

		assertNotNull(summary);
		assertEquals(100L, summary.getNumberOfUsers());
		assertEquals(50L, summary.getNumberOfMovies());
		assertEquals(2000.0, summary.getTotalRevenue());
	}

	@Test
	void testGetSummaryWithNullRevenue() {
		when(userRepository.count()).thenReturn(100L);
		when(movieRepository.count()).thenReturn(50L);
		when(purchaseRepository.getTotalRevenue()).thenReturn(null);

		SummaryDTO summary = adminService.getSummary();

		assertNotNull(summary);
		assertEquals(100L, summary.getNumberOfUsers());
		assertEquals(50L, summary.getNumberOfMovies());
		assertEquals(0.0, summary.getTotalRevenue());
	}

	@Test
	void testGetChartDataMoviesByGenre() {
		List<Object[]> moviesByGenre = List.of(new Object[] { "Action", 10L }, new Object[] { "Comedy", 5L });
		when(movieRepository.countMoviesByGenre()).thenReturn(moviesByGenre);

		Map<String, Object> chartData = adminService.getChartData("moviesByGenre");

		assertNotNull(chartData);
		assertTrue(chartData.containsKey("series"));
		assertEquals(2, ((List<?>) chartData.get("series")).size());
	}

	@Test
	void testGetChartDataRevenueByGenre() {
		List<Object[]> revenueByGenre = List.of(new Object[] { "Action", 200.0 }, new Object[] { "Comedy", 150.0 });
		when(purchaseRepository.getRevenueByGenre()).thenReturn(revenueByGenre);

		Map<String, Object> chartData = adminService.getChartData("revenueByGenre");

		assertNotNull(chartData);
		assertTrue(chartData.containsKey("series"));
		assertEquals(2, ((List<?>) chartData.get("series")).size());
	}

	@Test
	void testGetChartDataTopUsers() {
		List<Object[]> topUsers = List.of(new Object[] { "John", 5L }, new Object[] { "Jane", 3L });
		when(purchaseRepository.getTopUsers()).thenReturn(topUsers);

		Map<String, Object> chartData = adminService.getChartData("topUsers");

		assertNotNull(chartData);
		assertTrue(chartData.containsKey("series"));
		assertEquals(2, ((List<?>) chartData.get("series")).size());
	}

	@Test
	void testGetChartDataInvalidType() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			adminService.getChartData("invalidType");
		});

		assertEquals("Invalid chart type: invalidType", exception.getMessage());
	}

	@Test
	void testGetChartDataNullType() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			adminService.getChartData(null);
		});

		assertEquals("Chart type cannot be null or empty.", exception.getMessage());
	}
}
