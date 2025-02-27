package com.endava.example.service.impl;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.endava.example.service.PurchaseService;
import com.endava.example.utils.EmailService;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the PurchaseService interface that handles operations
 * related to movie purchases, including payment simulation, invoice generation,
 * and sending confirmation email.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseServiceImpl implements PurchaseService {

	private final PurchaseRepository purchaseRepository;
	private final UserRepository userRepository;
	private final MovieRepository movieRepository;
	private final PurchaseDetailRepository purchaseDetailRepository;
	private final EmailService emailService;
	private final PurchaseMapper purchaseMapper;

	/**
	 * Creates a new purchase, saves the details, and sends a confirmation email.
	 * 
	 * @param dto The purchase request data transfer object.
	 * @return A DTO representation of the created purchase.
	 * @throws ResourceNotFoundException If user or any movie is not found.
	 * @throws RuntimeException          If payment fails.
	 */
	@Transactional
	@Override
	public PurchaseDTO createPurchase(PurchaseRequestDTO dto) {

		User user = userRepository.findById(dto.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + dto.getUserId()));

		List<Movie> movies = movieRepository.findAllById(dto.getMovieIds());
		if (movies.size() != dto.getMovieIds().size()) {
			throw new ResourceNotFoundException("Some movies not found.");
		}

		double totalPrice = movies.stream().mapToDouble(Movie::getPrice).sum();

		String transactionId = simulatePayment();
		if (transactionId == null) {
			throw new PaymentFailedException("Payment failed. No purchase record created.");
		}

		Purchase purchase = createPurchaseRecord(dto, user, totalPrice, transactionId);

		savePurchaseDetails(movies, purchase);

		sendConfirmationEmail(user, transactionId);

		return purchaseMapper.toDto(purchase);
	}

	/**
	 * Retrieves a list of purchases for a given user, sorted by purchase ID in
	 * descending order.
	 * 
	 * @param userId The ID of the user.
	 * @return A list of purchase DTOs for the user.
	 */
	@Override
	public List<PurchaseDTO> getPurchaseByUserId(int userId) {
		return purchaseRepository.findByUser_UserId(userId).stream().map(purchaseMapper::toDto)
				.sorted(Comparator.comparing(PurchaseDTO::getPurchaseId).reversed()).toList();
	}

	/**
	 * Retrieves a list of movies purchased by a user, sorted by most recent
	 * purchases.
	 * 
	 * @param userId The ID of the user.
	 * @return A list of purchased movie DTOs for the user.
	 */
	@Override
	public List<PurchasedMovieDTO> getPurchasedMovieByUser(int userId) {

		List<PurchaseDetail> purchaseDetails = purchaseDetailRepository.findByPurchase_User_UserId(userId);

		// Sorting purchase details based on purchase ID in descending order
		return purchaseDetails.stream()
				.sorted((purchaseDetail1, purchaseDetail2) -> Integer.compare(
						purchaseDetail2.getPurchase().getPurchaseId(), purchaseDetail1.getPurchase().getPurchaseId()))
				.map(purchase -> {
					Movie movie = purchase.getMovie();
					PurchasedMovieDTO purchasedMovieDTO = new PurchasedMovieDTO();
					purchasedMovieDTO.setMovieId(movie.getMovieId());
					purchasedMovieDTO.setTitle(movie.getTitle());
					purchasedMovieDTO.setUserId(userId);
					purchasedMovieDTO.setPosterURL(movie.getPosterURL());
					purchasedMovieDTO.setTrailerURL(movie.getTrailerURL());
					purchasedMovieDTO.setStatus(movie.getStatus());
					return purchasedMovieDTO;
				}).toList();
	}

	/**
	 * Checks if a particular movie has been purchased by a user.
	 * 
	 * @param userId  The ID of the user.
	 * @param movieId The ID of the movie.
	 * @return true if the movie has been purchased, false otherwise.
	 */
	@Override
	public boolean isMoviePurchasedByUser(int userId, int movieId) {
		return purchaseDetailRepository.existsByPurchase_User_UserIdAndMovie_MovieId(userId, movieId);
	}

	/**
	 * Generates an invoice for a given purchase ID in PDF format.
	 * 
	 * @param purchaseId The ID of the purchase.
	 * @return A byte array representing the generated PDF.
	 * @throws ResourceNotFoundException If no purchase is found for the given ID.
	 * @throws RuntimeException          If there is an error while generating the
	 *                                   PDF.
	 */
	@Override
	public byte[] generateInvoicePdf(int purchaseId) {

		Purchase purchase = getPurchase(purchaseId);
		List<PurchaseDetail> purchaseDetails = getPurchaseDetails(purchaseId);

		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Document document = new Document(PageSize.A4);
			PdfWriter writer = PdfWriter.getInstance(document, out);
			setFooterImage(writer);

			document.open();
			addHeaderContent(purchase, document);
			addTableContent(purchaseDetails, document);
			addTermsAndConditions(document);
			document.close();
			return out.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException("Error generating invoice PDF", e);
		}
	}

	/**
	 * Simulates a payment process with an 80% success rate.
	 * 
	 * @return A transaction ID if payment is successful, null otherwise.
	 */
	String simulatePayment() {
		boolean paymentSuccess = Math.random() < 0.8; // 80% chance of payment success
		return paymentSuccess ? "TXN-" + System.currentTimeMillis() : null;
	}

	/**
	 * Creates and saves a new purchase record.
	 * 
	 * @param dto           The purchase request data transfer object.
	 * @param user          The user making the purchase.
	 * @param totalPrice    The total price of the purchase.
	 * @param transactionId The transaction ID for the purchase.
	 * @return The created purchase entity.
	 */
	private Purchase createPurchaseRecord(PurchaseRequestDTO dto, User user, double totalPrice, String transactionId) {
		Purchase purchase = purchaseMapper.toEntity(dto, user);
		purchase.setPurchaseDate(LocalDate.now());
		purchase.setTransactionId(transactionId);
		purchase.setTotalPrice(totalPrice);
		purchase = purchaseRepository.save(purchase);
		return purchase;
	}

	/**
	 * Saves the details of the movies purchased.
	 * 
	 * @param movies   The list of movies purchased.
	 * @param purchase The purchase entity associated with the details.
	 */
	private void savePurchaseDetails(List<Movie> movies, Purchase purchase) {
		movies.forEach(movie -> {
			PurchaseDetail purchaseDetail = new PurchaseDetail();
			purchaseDetail.setPurchase(purchase);
			purchaseDetail.setMovie(movie);
			purchaseDetailRepository.save(purchaseDetail);
		});
	}

	/**
	 * Sends a confirmation email to the user after a successful purchase.
	 * 
	 * @param user          The user to send the email to.
	 * @param transactionId The transaction ID of the purchase.
	 */
	private void sendConfirmationEmail(User user, String transactionId) {
		new Thread(() -> {
			try {
				emailService.sendEmail(user.getEmail(), "Confirmation Mail for Transaction Id : " + transactionId,
						"Thanks for purchasing the movie from GXMovies.");
			} catch (Exception e) {
				log.error("Failed to send email: " + e.getMessage());
			}
		}).start();
	}

	/**
	 * Retrieves a purchase record for the given purchase ID.
	 * 
	 * @param purchaseId The ID of the purchase.
	 * @return The purchase record.
	 * @throws ResourceNotFoundException If no purchase is found for the given ID.
	 */
	Purchase getPurchase(int purchaseId) {
		return purchaseRepository.findById(purchaseId)
				.orElseThrow(() -> new ResourceNotFoundException("No Purchase Record for this id."));
	}

	/**
	 * Retrieves a list of purchase details for a given purchase ID.
	 * 
	 * @param purchaseId The ID of the purchase.
	 * @return A list of PurchaseDetail objects.
	 */
	List<PurchaseDetail> getPurchaseDetails(int purchaseId) {
		return purchaseDetailRepository.findByPurchase_PurchaseId(purchaseId);
	}

	/**
	 * Sets the footer image for the invoice PDF.
	 * 
	 * @param writer The PdfWriter instance used to write the PDF.
	 */
	private void setFooterImage(PdfWriter writer) {
		writer.setPageEvent(new PdfPageEventHelper() {
			@Override
			public void onEndPage(PdfWriter writer, Document document) {
				try {
					Image footer = Image.getInstance("src/main/resources/static/footer.png");
					footer.scaleToFit(document.getPageSize().getWidth(), document.getPageSize().getHeight());
					footer.setAbsolutePosition(0, 0);
					writer.getDirectContent().addImage(footer);
				} catch (Exception e) {
					throw new RuntimeException("Error adding footer image", e);
				}
			}
		});
	}

	/**
	 * Adds header content, including the company logo and purchase information, to
	 * the document.
	 * 
	 * @param purchase The purchase entity containing the purchase information.
	 * @param document The PDF document to add the content to.
	 */
	private void addHeaderContent(Purchase purchase, Document document) throws Exception {
		// Adding company logo to the top left
		Image logo = Image.getInstance("src/main/resources/static/logo.png");
		logo.scaleToFit(150, 150);
		logo.setAbsolutePosition(36, 750);
		document.add(logo);

		// Adding header with the word "Invoice"
		Font headerFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
		Paragraph header = new Paragraph("Invoice", headerFont);
		header.setAlignment(Element.ALIGN_RIGHT);
		header.setSpacingBefore(20);
		document.add(header);

		// Adding purchase details such as user info and transaction ID
		addPurchaseDetailsParagraphs(purchase, document);
	}

	/**
	 * Adds the details of the purchase (such as user information, purchase ID,
	 * etc.) to the document.
	 * 
	 * @param purchase The purchase entity containing the details.
	 * @param document The PDF document to add the details to.
	 */
	private void addPurchaseDetailsParagraphs(Purchase purchase, Document document) throws Exception {
		document.add(new Paragraph(" ", new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.WHITE)));
		document.add(new Paragraph("Full Name: " + purchase.getUser().getFullName()));
		document.add(new Paragraph(" ", new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.WHITE)));
		document.add(new Paragraph("Purchase ID: " + purchase.getPurchaseId()));
		document.add(new Paragraph(" ", new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.WHITE)));
		document.add(new Paragraph("Transaction ID: " + purchase.getTransactionId()));
		document.add(new Paragraph(" ", new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.WHITE)));
		document.add(new Paragraph("Purchase Date: " + purchase.getPurchaseDate()));
		document.add(new Paragraph(" ", new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.WHITE)));
		document.add(new Paragraph("Payment Method: " + purchase.getPaymentMethod()));
		document.add(new Paragraph(" ", new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.WHITE)));
		document.add(new Paragraph("Total Price: ₹" + purchase.getTotalPrice()));
		document.add(new Paragraph(" "));
	}

	/**
	 * Adds a table containing the details of the purchased movies (e.g., movie ID,
	 * title, price) to the document.
	 * 
	 * @param purchaseDetails The list of PurchaseDetail objects containing movie
	 *                        details.
	 * @param document        The PDF document to add the table to.
	 */
	private void addTableContent(List<PurchaseDetail> purchaseDetails, Document document) throws Exception {
		PdfPTable table = new PdfPTable(3);
		table.setWidthPercentage(100);
		table.setSpacingBefore(10f);
		table.setSpacingAfter(10f);
		table.setHorizontalAlignment(Element.ALIGN_CENTER);

		// Adding table headers
		Font tableHeaderFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
		PdfPCell headerCell1 = new PdfPCell(new Paragraph("Title", tableHeaderFont));
		PdfPCell headerCell2 = new PdfPCell(new Paragraph("Price", tableHeaderFont));
		PdfPCell headerCell3 = new PdfPCell(new Paragraph("Genre", tableHeaderFont));
		table.addCell(headerCell1);
		table.addCell(headerCell2);
		table.addCell(headerCell3);

		// Adding movie details to the table
		Font tableBodyFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
		for (PurchaseDetail detail : purchaseDetails) {
			table.addCell(new PdfPCell(new Paragraph(detail.getMovie().getTitle(), tableBodyFont)));
			table.addCell(new PdfPCell(new Paragraph("₹" + detail.getMovie().getPrice(), tableBodyFont)));
			table.addCell(new PdfPCell(new Paragraph(detail.getMovie().getGenre(), tableBodyFont)));

		}

		document.add(table);
	}

	/**
	 * Adds terms and conditions section to the PDF document.
	 * 
	 * @param document The PDF document to add the terms and conditions to.
	 */
	private void addTermsAndConditions(Document document) throws Exception {
		document.add(new Paragraph("Terms and Conditions:", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
		document.add(new Paragraph(
				"""
						1. Finality of Sales: All sales are final and non-refundable. Please make sure to review your selections carefully before completing the transaction.
						2. Verification of Movie Details: Users are responsible for verifying movie details, such as title, release date, and format, prior to making a purchase.
						3. No Piracy Policy: Unauthorized duplication, distribution, or sharing of purchased content is strictly prohibited. Users must adhere to copyright laws.
						4. User Privacy Protection: We prioritize the privacy and protection of our users’ personal information, handling all data in accordance with our privacy policy.
						5. Customer Support: For any questions, issues, or support needs, users are encouraged to contact our customer support team for prompt assistance.
						6. Content Availability: The availability of movies and content is subject to change without notice. We reserve the right to modify, suspend, or discontinue any content or service at our discretion.
						7. Usage Rights: Purchase of content grants the user a non-transferable, limited right to access and view the content for personal use only."""));
	}

}
