package net.metrosystems.inventoryaggregator.common.stock_location.acl.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AclBundleStockLocation {

	private Long bundleNo;
	private Integer aisleNo;
	private Integer bayNo;
	private Integer quantity;
	private String bestBeforeDate;
	private String dateOfCreation;
	private String lastChangeDate ;
	private String serialShippingContainerCode;

}
