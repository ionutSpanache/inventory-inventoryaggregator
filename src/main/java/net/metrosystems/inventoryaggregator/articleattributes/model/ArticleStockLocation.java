package net.metrosystems.inventoryaggregator.articleattributes.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleStockLocation {

	private Long bundleNo;
	private String aisleNo;
	private String bayNo;
	private Integer quantity;
	private String bestBeforeDate;
	private String creationDate;
	private String changeDate ;
	private String serialShippingContainerCode;
	private String storeLocation;
	private String masterSscc;

}
