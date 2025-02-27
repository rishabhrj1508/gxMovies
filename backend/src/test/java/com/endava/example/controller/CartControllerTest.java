package com.endava.example.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.endava.example.dto.CartDTO;
import com.endava.example.exceptions.ResourceNotFoundException;
import com.endava.example.service.CartService;
import com.endava.example.utils.JwtAuthenticationFilter;
import com.endava.example.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
class CartControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private CartService cartService;

	@MockitoBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@MockitoBean
	private JwtUtils jwtUtils;

	@Test
	void testAddToCart_Success() throws Exception {
		CartDTO dto = new CartDTO();
		when(cartService.addToCart(any(CartDTO.class))).thenReturn(dto);

		mockMvc.perform(post("/api/carts").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Item added to cart"));
	}

	@Test
	void testGetAllItemsInCart_Success() throws Exception {
		int userId = 1;
		List<CartDTO> cartItems = Arrays.asList(new CartDTO(), new CartDTO());
		when(cartService.getAllCartItemsOfUser(userId)).thenReturn(cartItems);

		mockMvc.perform(get("/api/carts/user/{userId}", userId)).andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Cart items fetched successfully"))
				.andExpect(jsonPath("$.success").value(true)).andExpect(jsonPath("$.data.length()").value(2));
	}

	@Test
	void testGetAllItemsInCart_UserNotFound() throws Exception {
		when(cartService.getAllCartItemsOfUser(999)).thenThrow(new ResourceNotFoundException("User not found"));

		mockMvc.perform(get("/api/carts/user/999")).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success").value(false)).andExpect(jsonPath("$.message").value("User not found"));
	}

	@Test
	void testRemoveFromCart_Success() throws Exception {
		doNothing().when(cartService).removeFromCartOfUser(1, 100);

		mockMvc.perform(delete("/api/carts/user/1/movie/100")).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Movie removed from cart"));
	}

	@Test
	void testRemoveFromCart_NotFound() throws Exception {
		doThrow(new ResourceNotFoundException("Item not found")).when(cartService).removeFromCartOfUser(1, 999);

		mockMvc.perform(delete("/api/carts/user/1/movie/999")).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success").value(false)).andExpect(jsonPath("$.message").value("Item not found"));
	}

	@Test
	void testRemoveMultipleMovies_Success() throws Exception {
		List<Integer> movieIds = Arrays.asList(100, 101);
		doNothing().when(cartService).removeMultipleMoviesFromCartOfUser(1, movieIds);

		mockMvc.perform(delete("/api/carts/user/remove-multiple-cartItems").param("userId", "1")
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(movieIds)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true));
	}

	@Test
	void testClearCart_Success() throws Exception {
		doNothing().when(cartService).clearCartOfUser(1);

		mockMvc.perform(delete("/api/carts/user/1/clear")).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Cart cleared successfully"));
	}

	@Test
	void testCheckMovieInCart_Success() throws Exception {
		when(cartService.checkMovieInCartOfUser(1, 100)).thenReturn(true);

		mockMvc.perform(get("/api/carts/user/1/movie/100")).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true)).andExpect(jsonPath("$.data").value(true));
	}
}
