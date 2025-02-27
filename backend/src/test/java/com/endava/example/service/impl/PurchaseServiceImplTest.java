package com.endava.example.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.endava.example.dto.PurchaseDTO;
import com.endava.example.dto.PurchaseRequestDTO;
import com.endava.example.dto.PurchasedMovieDTO;
import com.endava.example.entity.Movie;
import com.endava.example.entity.Purchase;
import com.endava.example.entity.PurchaseDetail;
import com.endava.example.entity.User;
import com.endava.example.exceptions.PaymentFailedException;
import com.endava.example.exceptions.ResourceNotFoundException;
import com.endava.example.mapper.PurchaseMapper;
import com.endava.example.repository.MovieRepository;
import com.endava.example.repository.PurchaseDetailRepository;
import com.endava.example.repository.PurchaseRepository;
import com.endava.example.repository.UserRepository;
import com.endava.example.utils.EmailService;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceImplTest {

	@Mock
	private PurchaseRepository purchaseRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private MovieRepository movieRepository;

	@Mock
	private PurchaseDetailRepository purchaseDetailRepository;

	@Mock
	private EmailService emailService;

	@Mock
	private PurchaseMapper purchaseMapper;

	@InjectMocks
	private PurchaseServiceImpl purchaseService;

	@Test
	void testCreatePurchase_Success() {
		PurchaseRequestDTO dto = new PurchaseRequestDTO();
		dto.setUserId(1);
		dto.setMovieIds(List.of(1, 2));

		User user = new User();
		user.setUserId(1);
		user.setEmail("rishabh@gmail.com");

		Movie movie1 = new Movie();
		movie1.setMovieId(1);
		movie1.setPrice(100.0);

		Movie movie2 = new Movie();
		movie2.setMovieId(2);
		movie2.setPrice(200.0);

		List<Movie> movies = List.of(movie1, movie2);

		Purchase purchase = new Purchase();
		purchase.setPurchaseId(1);

		PurchaseDTO purchaseDTO = new PurchaseDTO();
		purchaseDTO.setPurchaseId(1);
		purchaseDTO.setTotalPrice(300.0);

		Purchase mappedPurchase = new Purchase();
		mappedPurchase.setPurchaseDate(LocalDate.now());
		mappedPurchase.setTransactionId("txn-123");
		mappedPurchase.setTotalPrice(300.0);
		when(purchaseMapper.toEntity(dto, user)).thenReturn(mappedPurchase);

		when(userRepository.findById(1)).thenReturn(Optional.of(user));
		when(movieRepository.findAllById(dto.getMovieIds())).thenReturn(movies);
		when(purchaseRepository.save(any(Purchase.class))).thenReturn(purchase);
		when(purchaseMapper.toDto(purchase)).thenReturn(purchaseDTO);
		purchaseService = Mockito.spy(purchaseService);
		doReturn("txn-123").when(purchaseService).simulatePayment();

		PurchaseDTO result = purchaseService.createPurchase(dto);

		assertNotNull(result);
		assertEquals(1, result.getPurchaseId());
		assertEquals(300.0, result.getTotalPrice());
		verify(purchaseRepository, times(1)).save(any(Purchase.class));
	}

	@Test
	void testCreatePurchase_UserNotFound() {
		PurchaseRequestDTO dto = new PurchaseRequestDTO();
		dto.setUserId(999);

		when(userRepository.findById(dto.getUserId())).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> purchaseService.createPurchase(dto));
	}

	@Test
	void testCreatePurchase_MovieNotFound() {
		PurchaseRequestDTO requestDTO = new PurchaseRequestDTO();
		requestDTO.setUserId(1);
		requestDTO.setMovieIds(Arrays.asList(101, 102));

		User user = new User();
		user.setUserId(1);

		Movie movie1 = new Movie();
		movie1.setMovieId(101);

		List<Movie> foundMovies = Arrays.asList(movie1);

		when(userRepository.findById(requestDTO.getUserId())).thenReturn(Optional.of(user));
		when(movieRepository.findAllById(requestDTO.getMovieIds())).thenReturn(foundMovies);

		assertThrows(ResourceNotFoundException.class, () -> purchaseService.createPurchase(requestDTO));
		verify(purchaseRepository, never()).save(any(Purchase.class));
		verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
	}

	@Test
	void testCreatePurchase_PaymentFailed() {
		PurchaseRequestDTO dto = new PurchaseRequestDTO();
		dto.setUserId(1);
		dto.setMovieIds(List.of(1, 2));

		User user = new User();
		user.setUserId(1);

		Movie movie1 = new Movie();
		movie1.setMovieId(1);
		movie1.setPrice(100.0);

		Movie movie2 = new Movie();
		movie2.setMovieId(2);
		movie2.setPrice(200.0);

		when(userRepository.findById(dto.getUserId())).thenReturn(Optional.of(user));
		when(movieRepository.findAllById(dto.getMovieIds())).thenReturn(List.of(movie1, movie2));

		purchaseService = Mockito.spy(purchaseService);
		doReturn(null).when(purchaseService).simulatePayment();

		assertThrows(PaymentFailedException.class, () -> purchaseService.createPurchase(dto));
	}

	@Test
	void testGetPurchaseByUserId_Success() {

		int userId = 1;

		Purchase purchase1 = new Purchase();
		purchase1.setPurchaseId(1);

		Purchase purchase2 = new Purchase();
		purchase2.setPurchaseId(2);

		List<Purchase> purchases = List.of(purchase1, purchase2);

		PurchaseDTO dto1 = new PurchaseDTO();
		dto1.setPurchaseId(1);

		PurchaseDTO dto2 = new PurchaseDTO();
		dto2.setPurchaseId(2);

		when(purchaseRepository.findByUser_UserId(userId)).thenReturn(purchases);
		when(purchaseMapper.toDto(purchase1)).thenReturn(dto1);
		when(purchaseMapper.toDto(purchase2)).thenReturn(dto2);

		List<PurchaseDTO> expected = List.of(dto2, dto1);

		List<PurchaseDTO> result = purchaseService.getPurchaseByUserId(userId);

		assertEquals(expected, result);

	}

	@Test
	void testGetPurchasedMovieByUser_Success() {
		int userId = 1;

		Movie movie1 = new Movie();
		movie1.setMovieId(1);
		movie1.setTitle("Movie 1");
		movie1.setPosterURL("poster1.jpg");
		movie1.setTrailerURL("trailer1.mp4");
		movie1.setStatus("AVAILABLE");

		Movie movie2 = new Movie();
		movie2.setMovieId(2);
		movie2.setTitle("Movie 2");
		movie2.setPosterURL("poster2.jpg");
		movie2.setTrailerURL("trailer2.mp4");
		movie2.setStatus("AVAILABLE");

		Purchase purchase1 = new Purchase();
		purchase1.setPurchaseId(2);

		Purchase purchase2 = new Purchase();
		purchase2.setPurchaseId(1);

		PurchaseDetail detail1 = new PurchaseDetail();
		detail1.setPurchase(purchase1);
		detail1.setMovie(movie1);

		PurchaseDetail detail2 = new PurchaseDetail();
		detail2.setPurchase(purchase2);
		detail2.setMovie(movie2);

		List<PurchaseDetail> purchaseDetails = List.of(detail1, detail2);

		when(purchaseDetailRepository.findByPurchase_User_UserId(userId)).thenReturn(purchaseDetails);

		List<PurchasedMovieDTO> result = purchaseService.getPurchasedMovieByUser(userId);

		assertNotNull(result);
		assertEquals(2, result.size());

		verify(purchaseDetailRepository, times(1)).findByPurchase_User_UserId(userId);
	}

	@Test
	void testIsMoviePurchasedByUser_MoviePurchased_ReturnsTrue() {
		int userId = 1;
		int movieId = 2;

		when(purchaseDetailRepository.existsByPurchase_User_UserIdAndMovie_MovieId(userId, movieId)).thenReturn(true);

		boolean result = purchaseService.isMoviePurchasedByUser(userId, movieId);

		assertTrue(result);
	}

	@Test
	void testGenerateInvoicePdf_Success() {
		int purchaseId = 1;

		// Mocking User
		User user = new User();
		user.setUserId(1);
		user.setFullName("John Doe");
		user.setEmail("johndoe@example.com");

		// Mocking Purchase
		Purchase purchase = new Purchase();
		purchase.setPurchaseId(purchaseId);
		purchase.setTotalPrice(300.0);
		purchase.setTransactionId("txn-123");
		purchase.setPurchaseDate(LocalDate.now());
		purchase.setUser(user); // Setting User to avoid NullPointerException

		// Mocking Purchase Details
		PurchaseDetail detail1 = new PurchaseDetail();
		Movie movie1 = new Movie();
		movie1.setTitle("Movie 1");
		movie1.setPrice(100.0);
		detail1.setMovie(movie1);

		PurchaseDetail detail2 = new PurchaseDetail();
		Movie movie2 = new Movie();
		movie2.setTitle("Movie 2");
		movie2.setPrice(200.0);
		detail2.setMovie(movie2);

		List<PurchaseDetail> purchaseDetails = List.of(detail1, detail2);

		// Mocking the repository calls
		when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(purchase));
		when(purchaseDetailRepository.findByPurchase_PurchaseId(purchaseId)).thenReturn(purchaseDetails);

		// Calling the method under test
		byte[] pdfBytes = purchaseService.generateInvoicePdf(purchaseId);

		// Assertions
		assertNotNull(pdfBytes);
		assertTrue(pdfBytes.length > 0);

		// Verifications
		verify(purchaseRepository, times(1)).findById(purchaseId);
		verify(purchaseDetailRepository, times(1)).findByPurchase_PurchaseId(purchaseId);
	}

}
