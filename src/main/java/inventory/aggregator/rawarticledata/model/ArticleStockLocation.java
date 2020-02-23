package inventory.aggregator.rawarticledata.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleStockLocation {

	private Long bundleNo;
	private Integer aisleNo;
	private Integer bayNo;
	private Integer quantity;
	private String bestBeforeDate;
	private String creationDate;
	private String changeDate ;
	private String serialShippingContainerCode;

}
