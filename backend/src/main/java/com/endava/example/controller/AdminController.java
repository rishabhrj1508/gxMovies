
package com.endava.example.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.endava.example.dto.SummaryDTO;
import com.endava.example.service.AdminService;
import com.endava.example.utils.GenericResponse;

/**
 * AdminController provided endPoints for fetching summary data and chart data
 * for admin-dashboard..
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

	// Injecting required service
	private AdminService adminService;

	public AdminController(AdminService adminService) {
		this.adminService = adminService;
	}

	/**
	 * fetches the summary data for admin-dashboard
	 * 
	 * @return ResponseEntity containing the summary data or error response..
	 */
	@GetMapping("/summary")
	public ResponseEntity<GenericResponse<SummaryDTO>> getSummaryData() {

		return ResponseEntity
				.ok(new GenericResponse<>(true, "Summary Details fetched successfully", adminService.getSummary()));
	}

	/**
	 * fetches chart data based on specified chart type
	 * 
	 * @param type the type of the chart data to fetch (bar,line)
	 * @return ResponseEntity containing the chart data or error response
	 */
	@GetMapping("/chart")
	public ResponseEntity<GenericResponse<Map<String, Object>>> getChartData(@RequestParam String type) {
		return ResponseEntity
				.ok(new GenericResponse<>(true, "Chart Fetched successfully", adminService.getChartData(type)));
	}
}