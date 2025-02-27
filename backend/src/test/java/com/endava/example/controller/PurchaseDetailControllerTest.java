package com.endava.example.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.endava.example.dto.MovieDTO;
import com.endava.example.dto.PurchaseDetailDTO;
import com.endava.example.exceptions.ResourceNotFoundException;
import com.endava.example.service.PurchaseDetailService;
import com.endava.example.utils.JwtAuthenticationFilter;
import com.endava.example.utils.JwtUtils;

@WebMvcTest(PurchaseDetailController.class)
@AutoConfigureMockMvc(addFilters = false)
class PurchaseDetailControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private PurchaseDetailService purchaseDetailService;

	@MockitoBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@MockitoBean
	private JwtUtils jwtUtils;

	@Test
	void testGetPurchaseDetailsByPurchaseId_Success() throws Exception {
		int purchaseId = 1;

		MovieDTO movie1 = new MovieDTO();
		movie1.setTitle("Movie 1");

		MovieDTO movie2 = new MovieDTO();
		movie2.setTitle("Movie 2");

		PurchaseDetailDTO detail1 = new PurchaseDetailDTO();
		detail1.setPurchaseDetailId(1);
		detail1.setPurchaseId(purchaseId);
		detail1.setMovieId(1);
		detail1.setMovieDTO(movie1);

		PurchaseDetailDTO detail2 = new PurchaseDetailDTO();
		detail2.setPurchaseDetailId(2);
		detail2.setPurchaseId(purchaseId);
		detail2.setMovieId(2);
		detail2.setMovieDTO(movie2);

		List<PurchaseDetailDTO> purchaseDetails = List.of(detail1, detail2);

		when(purchaseDetailService.getDetailsByPurchaseId(purchaseId)).thenReturn(purchaseDetails);

		mockMvc.perform(get("/api/purchasedetails/purchase/{purchaseId}", purchaseId)).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Purchase details fetched successfully"))
				.andExpect(jsonPath("$.data[0].movieDTO.title").value("Movie 1"))
				.andExpect(jsonPath("$.data[1].movieDTO.title").value("Movie 2"));

		verify(purchaseDetailService).getDetailsByPurchaseId(purchaseId);
	}

	@Test
	void testGetPurchaseDetailsByPurchaseId_NotFound() throws Exception {
		int purchaseId = 99;

		when(purchaseDetailService.getDetailsByPurchaseId(purchaseId))
				.thenThrow(new ResourceNotFoundException("No purchase details found for this purchase."));

		mockMvc.perform(get("/api/purchasedetails/purchase/{purchaseId}", purchaseId)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("No purchase details found for this purchase."));

		verify(purchaseDetailService).getDetailsByPurchaseId(purchaseId);
	}
}
