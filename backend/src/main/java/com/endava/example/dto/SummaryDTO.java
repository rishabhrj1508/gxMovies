package com.endava.example.dto;

import lombok.Data;

/**
 * SummaryDTO contains summary information, including the total number of users,
 * movies, and the total revenue.
 */
@Data
public class SummaryDTO {
	long numberOfUsers;
	long numberOfMovies;
	double totalRevenue;
}
