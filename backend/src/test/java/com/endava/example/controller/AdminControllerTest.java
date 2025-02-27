package com.endava.example.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.endava.example.dto.SummaryDTO;
import com.endava.example.service.AdminService;
import com.endava.example.utils.JwtAuthenticationFilter;
import com.endava.example.utils.JwtUtils;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AdminService adminService;

	@MockitoBean
	private JwtUtils jwtUtils;

	@MockitoBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@Test
	void testGetSummaryData_Success() throws Exception {
		SummaryDTO summaryDTO = new SummaryDTO();
		summaryDTO.setNumberOfUsers(10);
		summaryDTO.setNumberOfMovies(20);
		summaryDTO.setTotalRevenue(4999);

		when(adminService.getSummary()).thenReturn(summaryDTO);

		mockMvc.perform(get("/api/admin/summary").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Summary Details fetched successfully"))
				.andExpect(jsonPath("$.data.numberOfUsers").value(10))
				.andExpect(jsonPath("$.data.numberOfMovies").value(20));
	}

	@Test
	void testGetSummaryData_Exception() throws Exception {
		when(adminService.getSummary()).thenThrow(new RuntimeException("Database error"));

		mockMvc.perform(get("/api/admin/summary").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError()).andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("An unexpected error occured"));
	}

	@Test
	void testGetChartData_Success() throws Exception {
		Map<String, Object> data = new HashMap<>();
		data.put("series", Arrays.asList(10, 20, 30));

		when(adminService.getChartData("moviesByGenre")).thenReturn(data);

		mockMvc.perform(get("/api/admin/chart").param("type", "moviesByGenre").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Chart Fetched successfully"))
				.andExpect(jsonPath("$.data.series[0]").value(10)).andExpect(jsonPath("$.data.series[1]").value(20))
				.andExpect(jsonPath("$.data.series[2]").value(30));
	}

	@Test
	void testGetChartData_InvalidType() throws Exception {
		when(adminService.getChartData("invalidType"))
				.thenThrow(new IllegalArgumentException("Invalid chart type: invalidType"));

		mockMvc.perform(get("/api/admin/chart").param("type", "invalidType").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError()).andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("Invalid chart type: invalidType"));
	}

}
