package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.articleCacheStoreExceptions.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name="DataGroup")
@XmlAccessorType(XmlAccessType.FIELD)
public class ArticleCacheStoreExceptionsDataGroup {

	/*Common*/
	@XmlAttribute(name="id")
	private int id;
	@XmlAttribute(name="name")
	private String name;
	@XmlElement(name="VALID_FROM")
	private Date validFrom;
	@XmlElement(name="VALID_TO")
	private Date validTo;
	/*Data Group 100*/
	@XmlElement(name="NET_SALES_PRICE_PER_BUNDLE")
	private Double netSalesPricePerBundle;
	@XmlElement(name="NET_SALES_PRICE_PER_PIECE")
	private Double netSalesPricePerPiece;
	@XmlElement(name="GROSS_SALES_PRICE_PER_BUNDLE")
	private Double grossSalesPricePerBundle;
	@XmlElement(name="GROSS_SALES_PRICE_PER_PIECE")
	private Double grossSalesPricePerPiece;

	/*Data Group 200*/
	@XmlElement(name="PROMOTION_NET_SALES_PRICE_PER_BUNDLE")
	private Double promotionNetSalesPricePerBundle;
	@XmlElement(name="PROMOTION_NET_SALES_PRICE_PER_PIECE")
	private Double promotionNetSalesPricePerPiece;
	@XmlElement(name="PROMOTION_GROSS_SALES_PRICE_PER_BUNDLE")
	private Double promotionGrossSalesPricePerBundle;
	@XmlElement(name="PROMOTION_GROSS_SALES_PRICE_PER_PIECE")
	private Double promotionGrossSalesPricePerPiece;
	@XmlElement(name="PACKAGEFACTOR")
	private Integer packageFactor;

	/*Data Group 300*/
	@XmlElement(name="SALES_BLOCKING_CODE")
	private Integer salesBlockingCode;

}
