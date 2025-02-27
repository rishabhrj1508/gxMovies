package com.endava.example.utils;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

	// injecting required dependencies
	@Autowired
	private JwtUtils jwtUtils;

	// Store rate-limiting buckets per user ID
	private final Map<Integer, Bucket> buckets = new ConcurrentHashMap<>();

	// list of endPoints to exclude from rate limiting
	// Excludes all paths under api/users/auth/ and /notifications
	// I am excluding this entry level paths
	private static final List<String> EXCLUDE_ENDPOINTS = List.of("/api/users/auth/", "/notifications");

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		// getting the path from the request URI
		String path = request.getRequestURI();

		// Check if the request path starts with any excluded endPoints
		if (EXCLUDE_ENDPOINTS.stream().anyMatch(path::startsWith)) {
			// skip rate-limiting passing the control to the next filter
			chain.doFilter(request, response);
			return;
		}

		// Extract JWT token from Authorization header
		String token = request.getHeader("Authorization");
		if (token == null || !token.startsWith("Bearer ")) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("Unauthorized: Missing or invalid token");
			return;
		}

		// Extract user ID from the token
		Integer userId = extractUserIdFromJwt(token.substring(7));

		// Get or create a bucket for the user
		Bucket bucket = buckets.computeIfAbsent(userId, k -> createNewBucket());

		// Try consuming a token (user's request)
		if (bucket.tryConsume(1)) {
			chain.doFilter(request, response);
//			System.out.println(bucket.getAvailableTokens());
		} else {
			response.setStatus(429); // HTTP 429 Too Many Requests Error
			response.getWriter().write("Too many requests.");
		}
	}

	// helper function to createNewBucket
	private Bucket createNewBucket() {
		// creating a bucket of 10 request per minute per user..
		return Bucket.builder().addLimit(Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)))).build();
	}

	// helper function to extract userId from Jwt-token
	private Integer extractUserIdFromJwt(String token) {
		try {
			return jwtUtils.extractUserId(token);
		} catch (JwtException e) {
			return null; // Invalid or expired token
		}
	}
}
