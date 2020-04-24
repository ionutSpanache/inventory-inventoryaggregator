package net.metrosystems.inventoryaggregator.rawarticledata.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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

}
