package com.endava.example.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.endava.example.dto.PurchaseDTO;
import com.endava.example.dto.PurchaseRequestDTO;
import com.endava.example.dto.PurchasedMovieDTO;
import com.endava.example.exceptions.ResourceNotFoundException;
import com.endava.example.service.PurchaseService;
import com.endava.example.utils.JwtAuthenticationFilter;
import com.endava.example.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(PurchaseController.class)
@AutoConfigureMockMvc(addFilters = false)
class PurchaseControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private PurchaseService purchaseService;

	@MockitoBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@MockitoBean
	private JwtUtils jwtUtils;

	@Test
	void testCreatePurchaseSuccess() throws Exception {
		PurchaseRequestDTO requestDTO = new PurchaseRequestDTO();
		requestDTO.setUserId(1);
		requestDTO.setTotalPrice(49.99);

		PurchaseDTO createdPurchase = new PurchaseDTO();
		createdPurchase.setPurchaseId(1);
		createdPurchase.setUserId(requestDTO.getUserId());
		createdPurchase.setTotalPrice(requestDTO.getTotalPrice());

		when(purchaseService.createPurchase(any(PurchaseRequestDTO.class))).thenReturn(createdPurchase);

		mockMvc.perform(post("/api/purchases").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDTO))).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Purchase created successfully"))
				.andExpect(jsonPath("$.data.purchaseId").value(1)).andExpect(jsonPath("$.data.userId").value(1))
				.andExpect(jsonPath("$.data.totalPrice").value(49.99));

		verify(purchaseService).createPurchase(any(PurchaseRequestDTO.class));
	}

	@Test
	void testCreatePurchase_Exception() throws Exception {
		PurchaseRequestDTO requestDTO = new PurchaseRequestDTO();
		requestDTO.setUserId(999);
		requestDTO.setTotalPrice(19.99);

		when(purchaseService.createPurchase(any(PurchaseRequestDTO.class)))
				.thenThrow(new ResourceNotFoundException("User not found for purchase."));

		mockMvc.perform(post("/api/purchases").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDTO))).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("User not found for purchase."));

		verify(purchaseService).createPurchase(any(PurchaseRequestDTO.class));
	}

	@Test
	void testGetUserPurchasesSuccess() throws Exception {
		int userId = 1;

		PurchaseDTO purchase1 = new PurchaseDTO();
		purchase1.setPurchaseId(1);
		purchase1.setUserId(userId);
		purchase1.setTotalPrice(19.99);

		PurchaseDTO purchase2 = new PurchaseDTO();
		purchase2.setPurchaseId(2);
		purchase2.setUserId(userId);
		purchase2.setTotalPrice(29.99);

		List<PurchaseDTO> purchases = List.of(purchase1, purchase2);

		when(purchaseService.getPurchaseByUserId(userId)).thenReturn(purchases);

		mockMvc.perform(get("/api/purchases/users/{userId}", userId)).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("User purchases fetched successfully"))
				.andExpect(jsonPath("$.data[0].purchaseId").value(1))
				.andExpect(jsonPath("$.data[0].userId").value(userId))
				.andExpect(jsonPath("$.data[0].totalPrice").value(19.99))
				.andExpect(jsonPath("$.data[1].purchaseId").value(2))
				.andExpect(jsonPath("$.data[1].userId").value(userId))
				.andExpect(jsonPath("$.data[1].totalPrice").value(29.99));

		verify(purchaseService).getPurchaseByUserId(userId);
	}

	@Test
	void testGetUserPurchases_NoPurchasesFound() throws Exception {
		int userId = 999;

		when(purchaseService.getPurchaseByUserId(userId)).thenReturn(List.of());

		mockMvc.perform(get("/api/purchases/users/{userId}", userId)).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("User purchases fetched successfully"))
				.andExpect(jsonPath("$.data").isArray()).andExpect(jsonPath("$.data").isEmpty());

		verify(purchaseService).getPurchaseByUserId(userId);
	}

	@Test
	void testGetPurchasedMoviesSuccess() throws Exception {
		int userId = 1;

		PurchasedMovieDTO movie1 = new PurchasedMovieDTO();
		movie1.setMovieId(1);
		movie1.setTitle("Movie 1");

		PurchasedMovieDTO movie2 = new PurchasedMovieDTO();
		movie2.setMovieId(2);
		movie2.setTitle("Movie 2");

		List<PurchasedMovieDTO> purchasedMovies = List.of(movie1, movie2);

		when(purchaseService.getPurchasedMovieByUser(userId)).thenReturn(purchasedMovies);

		mockMvc.perform(get("/api/purchases/users/movies/{userId}", userId)).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Purchased movies fetched successfully"))
				.andExpect(jsonPath("$.data[0].movieId").value(1))
				.andExpect(jsonPath("$.data[0].title").value("Movie 1"))
				.andExpect(jsonPath("$.data[1].movieId").value(2))
				.andExpect(jsonPath("$.data[1].title").value("Movie 2"));

		verify(purchaseService).getPurchasedMovieByUser(userId);
	}

	@Test
	void testGetPurchasedMoviesNotFound() throws Exception {
		int userId = 99;

		when(purchaseService.getPurchasedMovieByUser(userId))
				.thenThrow(new ResourceNotFoundException("No purchased movies found for this user."));

		mockMvc.perform(get("/api/purchases/users/movies/{userId}", userId)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("No purchased movies found for this user."));

		verify(purchaseService).getPurchasedMovieByUser(userId);
	}

	@Test
	void testIsMoviePurchasedSuccess() throws Exception {
		int userId = 1;
		int movieId = 1;

		when(purchaseService.isMoviePurchasedByUser(userId, movieId)).thenReturn(true);

		mockMvc.perform(get("/api/purchases/users/movies/check").param("userId", String.valueOf(userId))
				.param("movieId", String.valueOf(movieId))).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Movie purchase check successful"))
				.andExpect(jsonPath("$.data").value(true));

		verify(purchaseService).isMoviePurchasedByUser(userId, movieId);
	}

	@Test
	void testIsMoviePurchasedNotPurchased() throws Exception {
		int userId = 1;
		int movieId = 99;

		when(purchaseService.isMoviePurchasedByUser(userId, movieId)).thenReturn(false);

		mockMvc.perform(get("/api/purchases/users/movies/check").param("userId", String.valueOf(userId))
				.param("movieId", String.valueOf(movieId))).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Movie purchase check successful"))
				.andExpect(jsonPath("$.data").value(false));

		verify(purchaseService).isMoviePurchasedByUser(userId, movieId);
	}

	@Test
	void testGenerateInvoiceSuccess() throws Exception {
		int purchaseId = 1;
		String transactionId = "TXN123";
		byte[] pdfData = "Sample PDF Data".getBytes();

		when(purchaseService.generateInvoicePdf(purchaseId)).thenReturn(pdfData);

		mockMvc.perform(get("/api/purchases/invoice/{purchaseId}", purchaseId).param("transactionId", transactionId))
				.andExpect(status().isOk()).andExpect(header().string("Content-Type", MediaType.APPLICATION_PDF_VALUE))
				.andExpect(header().string("Content-Disposition",
						"form-data; name=\"attachment\"; filename=\"invoice_TXN123.pdf\""));

		verify(purchaseService).generateInvoicePdf(purchaseId);
	}

	@Test
	void testGenerateInvoiceNotFound() throws Exception {
		int purchaseId = 99;
		String transactionId = "TXN999";

		when(purchaseService.generateInvoicePdf(purchaseId))
				.thenThrow(new ResourceNotFoundException("Purchase not found."));

		mockMvc.perform(get("/api/purchases/invoice/{purchaseId}", purchaseId).param("transactionId", transactionId))
				.andExpect(status().isNotFound()).andExpect(header().doesNotExist("Content-Disposition"));

		verify(purchaseService).generateInvoicePdf(purchaseId);
	}
}
