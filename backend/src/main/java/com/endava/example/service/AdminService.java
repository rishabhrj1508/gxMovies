package com.endava.example.service;

import java.util.Map;

import com.endava.example.dto.SummaryDTO;

public interface AdminService {
	
	SummaryDTO getSummary();

	Map<String, Object> getChartData(String type);
	

}
