package com.endava.example.dto;

import lombok.Data;

/**
 * PurchaseDetailDTO containing details of the purchase made by user.. includes
 * unique identifier Id , the purchaseId whose details are being stored , the
 * movieId being purchased , and the details of that movie as movieDTO..
 */
@Data
public class PurchaseDetailDTO {

	private int purchaseDetailId;
	private int purchaseId;
	private int movieId;
	private MovieDTO movieDTO;

}
