package com.endava.example.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.endava.example.utils.JwtAuthenticationFilter;
import com.endava.example.utils.JwtUtils;

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@MockitoBean
	private JwtUtils jwtUtils;

	@Test
	void testSubscribeToNotifications_Success() throws Exception {
		mockMvc.perform(get("/notifications")).andExpect(status().isOk());
	}

	@Test
	void testSubscribeToNotifications_NotFound() throws Exception {
		mockMvc.perform(get("/invalid-notifications")).andExpect(status().isInternalServerError());
	}
}
