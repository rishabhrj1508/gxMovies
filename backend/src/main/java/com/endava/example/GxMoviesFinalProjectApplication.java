package com.endava.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * @SpringbootApplication is a combination of three annotations:
 * 1.@Configuration - source of bean definition
 * 2.@EnableAutoConfiguration - based on added dependencies...created beans necessary like embedded web server
 * 3.@ComponentScan - scans for components to register them as a bean
 */

@SpringBootApplication
public class GxMoviesFinalProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(GxMoviesFinalProjectApplication.class, args);
	}
}
