package com.endava.example.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

	@Value("${jwt-secret}")
	private String secret;
	
	private Key secretKey;

	private static final long EXPIRATION_TIME =(long) 1000 * 60 * 60 * 24; // 24 hours

	private Key getSecretKey() {
		if (secretKey == null) {
			this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		}
		return secretKey;
	}

	/**
	 * Generates a JWT token with userId as subject and role as claim
	 */
	public String generateToken(int userId, String role) {
		return Jwts.builder().setSubject(Integer.toString(userId)).claim("userId", userId).claim("role", role).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)).signWith(getSecretKey())
				.compact();
	}

	/**
	 * Validates the JWT token
	 */
	public boolean validateToken(String token) {
		try {
			parseToken(token);
			return true;
		} catch (ExpiredJwtException e) {
			throw new JwtException("Token expired.");
		} catch (JwtException | IllegalArgumentException e) {
			throw new JwtException("Invalid token.");
		}
	}

	/**
	 * Extracts Role from token
	 */
	public String extractRole(String token) {
		return parseToken(token).getBody().get("role", String.class);
	}

	/**
	 * Extracts UserId from token (since it is the subject)
	 */
	public int extractUserId(String token) {
		String userId = parseToken(token).getBody().getSubject();
		return Integer.parseInt(userId);
	}

	/**
	 * Private method to parse the token and get Claims
	 */
	private Jws<Claims> parseToken(String token) {
		return Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(token);
	}
}
