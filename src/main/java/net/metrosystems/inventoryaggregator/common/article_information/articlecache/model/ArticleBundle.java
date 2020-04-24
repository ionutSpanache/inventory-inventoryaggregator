
package net.metrosystems.inventoryaggregator.common.article_information.articlecache.model;

import net.metrosystems.inventoryaggregator.rawarticledata.model.ArticleStockLocation;
import lombok.Data;

import java.util.List;

@Data
public class ArticleBundle {

	// Attributes used by mobile UI
	private Long    bundleNumber;              //bundleNo 	 
	private Float   stock;                     //n.a.
	private Long    subsysArticleNumber;       //subsysNo 
	private String  barcode;
	private Long    merchandisingCode;                              // ArticleCache.ReaderDigest.MerchGroup
	private Byte    procurementCode;                                // ArticleCache.DataGroup(325).PROCUREMENTCD for requested store
	private Byte    salesBlockingStatus;
	private String  promotionId;                                    // ArticleCache.DataGroup(320).PROMOTIONID for requested store
	private Byte    sellingBundleIndicator;                         // ArticleCache.DataGroup(id=300).SALESBUNDLEINDICATOR
	private Integer sellingBundleContent;                           // ArticleCache.DataGroup(id=310).SELLINGBUNDLECONTENT
	private Byte    priceCheck;                                     // ArticleCache.DataGroup(id=310).PRICECHECKCODEMCCSALESLINE
	private Byte    status;                                         // ArticleCache.DataGroup(id=320).ACTIVATIONSTATUS

	private Double  stockMarkdown;                                  // movie.cache.stockDetails.stockSummary.markDownStock

	private Double  reservedStock;                                  // movie.cache.stockDetails.stockSummary.reservedStock+movie.cache.stockDetails.stockSummary.blockedStock

	private List<ArticlePlannedDeliveryDate> plannedDeliveryDates;
	private List<ArticleStockLocation> stockLocations;
	private String lastSalesDate;
	private String lastGRDate;
	private String lastMutationDate;

	// Attributes used by GapCheck Engine
	private Double  notReceivedDeletedQuantity;

	private String  plannedDeliveryDateNotReceivedDeletedQuantity;
	private Boolean isBlockedForOrdering;

	private Integer availableForOrdering;

	private Boolean orderedBySaf;

	private Integer changedSafQuantity;

	private Integer smallestOrderableSellingBundle;

	private Double unavailableStock;

	private Double dailyMeanSales;                        // aclDailyMeanSalesDetails.dailyMeanSales


	private Integer presentationMainMerchandiseGroup;

	private String priorityAssortmentCode;
	private Boolean isWeightArticle;

	public ArticleBundle() {

	}


	public ArticleBundle(Long   bundleNumber,
						 Long   subsysArticleNumber) {
		this.bundleNumber         = bundleNumber;
		this.subsysArticleNumber  = subsysArticleNumber;
	}


	public void setStockMarkdown(Double stockMarkdown) {
		this.stockMarkdown = stockMarkdown;
	}

	public void setNotReceivedDeletedQuantity(Double notReceivedDeletedQuantity) {
		this.notReceivedDeletedQuantity = notReceivedDeletedQuantity;
	}

	public String getPlannedDeliveryDateNotReceivedDeletedQuantity() {
		return plannedDeliveryDateNotReceivedDeletedQuantity;
	}

	public void setPlannedDeliveryDateNotReceivedDeletedQuantity(String plannedDeliveryDateNotReceivedDeletedQuantity) {
		this.plannedDeliveryDateNotReceivedDeletedQuantity = plannedDeliveryDateNotReceivedDeletedQuantity;
	}

	public Double getNotReceivedDeletedQuantity() {
		return notReceivedDeletedQuantity;
	}

	public Double getReservedStock() {
		return reservedStock;
	}

	public void setReservedStock(Double reservedStock) {
		this.reservedStock = reservedStock;
	}

	public Double getStockMarkdown() {
		return stockMarkdown;
	}

	public Boolean getIsBlockedForOrdering() {
		return isBlockedForOrdering;
	}

	public void setIsBlockedForOrdering(Boolean isBlockedForOrdering) {
		this.isBlockedForOrdering = isBlockedForOrdering;
	}

	public Integer getAvailableForOrdering() {
		return availableForOrdering;
	}

	public void setAvailableForOrdering(Integer availableForOrdering) {
		this.availableForOrdering = availableForOrdering;
	}

	public Double getUnavailableStock() {
		return unavailableStock;
	}

	public void setUnavailableStock(Double unavailableStock) {
		this.unavailableStock = unavailableStock;
	}

	public Boolean getOrderedBySaf() {
		return orderedBySaf;
	}

	public void setOrderedBySaf(Boolean orderedBySaf) {
		this.orderedBySaf = orderedBySaf;
	}

	public Integer getChangedSafQuantity() {
		return changedSafQuantity;
	}

	public void setChangedSafQuantity(Integer changedSafQuantity) {
		this.changedSafQuantity = changedSafQuantity;
	}

	public Integer getSmallestOrderableSellingBundle() {
		return smallestOrderableSellingBundle;
	}

	public void setSmallestOrderableSellingBundle(Integer smallestOrderableSellingBundle) {
		this.smallestOrderableSellingBundle = smallestOrderableSellingBundle;
	}

	public Double getDailyMeanSales() {
		return dailyMeanSales;
	}

	public void setDailyMeanSales(Double dailyMeanSales) {
		this.dailyMeanSales = dailyMeanSales;
	}

	public ArticleBundle(Long    bundleNumber,
						 Long    subsysArticleNumber,
						 String  barcode,
						 Long    merchandisingCode,
						 Byte    procurementCode,
						 Byte    salesBlockingStatus,
						 String  promotionId,
						 Byte    sellingBundleIndicator,
						 Integer sellingBundleContent,
						 Byte    priceCheck,
						 Byte    status,
						 Boolean isWeightArticle,
						 Integer presentationMainMerchandiseGroup) {
		this.bundleNumber           = bundleNumber;
		this.subsysArticleNumber    = subsysArticleNumber;
		this.barcode                = barcode;
		this.merchandisingCode      = merchandisingCode;
		this.procurementCode        = procurementCode;
		this.salesBlockingStatus    = salesBlockingStatus;
		this.promotionId            = promotionId;
		this.sellingBundleIndicator = sellingBundleIndicator;
		this.sellingBundleContent   = sellingBundleContent;
		this.priceCheck             = priceCheck;
		this.status                 = status;
		this.isWeightArticle = isWeightArticle;
		this.presentationMainMerchandiseGroup = presentationMainMerchandiseGroup;
	}


	public ArticleBundle(Long   bundleNumber,
						 Float  stock,
						 Long   subsysArticleNumber,
						 String barcode,
						 Long pMerchandisingCode,
						 Byte pProcurementCode,
						 Byte pSalesBlockingStatus,
						 List<ArticlePlannedDeliveryDate> plannedDeliveryDates,
						 List<ArticleStockLocation> stockLocations,
						 String lastSalesDate,
						 String lastGRDate,
						 String lastMutationDate) {

		this.bundleNumber         = bundleNumber;
		this.stock                = stock;
		this.subsysArticleNumber  = subsysArticleNumber;
		this.barcode              = barcode;
		this.merchandisingCode    = pMerchandisingCode;
		this.procurementCode      = pProcurementCode;
		this.salesBlockingStatus  = pSalesBlockingStatus;
		this.plannedDeliveryDates = plannedDeliveryDates;
		this.stockLocations       = stockLocations;
		this.lastSalesDate        = lastSalesDate;
		this.lastGRDate           = lastGRDate;
		this.lastMutationDate     = lastMutationDate;

	}


	public ArticleBundle(Long  bundleNumber,
						 Float  stock,
						 Long   subsysArticleNumber,
						 String barcode,
						 Byte   status,
						 List<ArticlePlannedDeliveryDate> plannedDeliveryDates,
						 List<ArticleStockLocation> stockLocations,
						 String lastSalesDate,
						 String lastGRDate,
						 String lastMutationDate) {

		this.bundleNumber         = bundleNumber;
		this.stock                = stock;
		this.subsysArticleNumber  = subsysArticleNumber;
		this.barcode              = barcode;
		this.status               = status;
		this.plannedDeliveryDates = plannedDeliveryDates;
		this.stockLocations       = stockLocations;
		this.lastSalesDate        = lastSalesDate;
		this.lastGRDate           = lastGRDate;
		this.lastMutationDate     = lastMutationDate;

	}


	public Long getBundleNumber() {
		return bundleNumber;
	}


	public void setBundleNumber(Long bundleNumber) {
		this.bundleNumber = bundleNumber;
	}

	public Float getStock() {
		return stock;
	}


	public void setStock(Float stock) {
		this.stock = stock;
	}


	public Long getSubsysArticleNumber() {
		return subsysArticleNumber;
	}


	public void setSubsysArticleNumber(Long subsysArticleNumber) {
		this.subsysArticleNumber = subsysArticleNumber;
	}


	public String getBarcode() {
		return barcode;
	}


	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}


	public Long getMerchandisingCode() {
		return merchandisingCode;
	}


	public void setMerchandisingCode(Long merchandisingCode) {
		this.merchandisingCode = merchandisingCode;
	}


	public Byte getProcurementCode() {
		return procurementCode;
	}


	public void setProcurementCode(Byte procurementCode) {
		this.procurementCode = procurementCode;
	}


	public Byte getSalesBlockingStatus() {
		return salesBlockingStatus;
	}


	public void setSalesBlockingStatus(Byte salesBlockingStatus) {
		this.salesBlockingStatus = salesBlockingStatus;
	}


	public String getPromotionId() {
		return promotionId;
	}


	public void setPromotionId(String promotionId) {
		this.promotionId = promotionId;
	}


	public Byte getSellingBundleIndicator() {
		return sellingBundleIndicator;
	}


	public void setSellingBundleIndicator(Byte sellingBundleIndicator) {
		this.sellingBundleIndicator = sellingBundleIndicator;
	}


	public Integer getSellingBundleContent() {
		return sellingBundleContent;
	}


	public void setSellingBundleContent(Integer sellingBundleContent) {
		this.sellingBundleContent = sellingBundleContent;
	}


	public Byte getPriceCheck() {
		return priceCheck;
	}


	public void setPriceCheck(Byte priceCheck) {
		this.priceCheck = priceCheck;
	}


	public List<ArticlePlannedDeliveryDate> getPlannedDeliveryDates() {
		return plannedDeliveryDates;
	}


	public void setPlannedDeliveryDates(List<ArticlePlannedDeliveryDate> plannedDeliveryDates) {
		this.plannedDeliveryDates = plannedDeliveryDates;
	}


	public List<ArticleStockLocation> getStockLocations() {
		return stockLocations;
	}


	public void setStockLocations(List<ArticleStockLocation> stockLocations) {
		this.stockLocations = stockLocations;
	}


	public String getLastSalesDate() {
		return lastSalesDate;
	}


	public void setLastSalesDate(String lastSalesDate) {
		this.lastSalesDate = lastSalesDate;
	}


	public String getLastGRDate() {
		return lastGRDate;
	}


	public void setLastGRDate(String lastGRDate) {
		this.lastGRDate = lastGRDate;
	}


	public String getLastMutationDate() {
		return lastMutationDate;
	}


	public void setLastMutationDate(String lastMutationDate) {
		this.lastMutationDate = lastMutationDate;
	}


	public Byte getStatus() {
		return status;
	}


	public void setStatus(Byte status) {
		this.status = status;
	}


	public Integer getPresentationMainMerchandiseGroup() {
		return presentationMainMerchandiseGroup;
	}


	public void setPresentationMainMerchandiseGroup(Integer presentationMainMerchandiseGroup) {
		this.presentationMainMerchandiseGroup = presentationMainMerchandiseGroup;
	}


	public String getPriorityAssortmentCode() {
		return priorityAssortmentCode;
	}


	public void setPriorityAssortmentCode(String priorityAssortmentCode) {
		this.priorityAssortmentCode = priorityAssortmentCode;
	}


	@Override
	public String toString() {
		return "ArticleBundle [bundleNumber=" + bundleNumber + ", stock=" + stock + ", subsysArticleNumber="
				+ subsysArticleNumber + ", barcode=" + barcode + ", merchandisingCode=" + merchandisingCode
				+ ", procurementCode=" + procurementCode + ", salesBlockingStatus=" + salesBlockingStatus
				+ ", promotionId=" + promotionId + ", sellingBundleIndicator=" + sellingBundleIndicator
				+ ", sellingBundleContent=" + sellingBundleContent + ", priceCheck=" + priceCheck + ", status=" + status
				+ ", stockMarkdown=" + stockMarkdown + ", reservedStock=" + reservedStock + ", plannedDeliveryDates="
				+ plannedDeliveryDates + ", stockLocations=" + stockLocations + ", lastSalesDate=" + lastSalesDate
				+ ", lastGRDate=" + lastGRDate + ", lastMutationDate=" + lastMutationDate
				+ ", notReceivedDeletedQuantity=" + notReceivedDeletedQuantity
				+ ", plannedDeliveryDateNotReceivedDeletedQuantity=" + plannedDeliveryDateNotReceivedDeletedQuantity
				+ ", isBlockedForOrdering=" + isBlockedForOrdering + ", availableForOrdering=" + availableForOrdering
				+ ", orderedBySaf=" + orderedBySaf + ", changedSafQuantity=" + changedSafQuantity
				+ ", smallestOrderableSellingBundle=" + smallestOrderableSellingBundle + ", unavailableStock="
				+ unavailableStock + ", dailyMeanSales=" + dailyMeanSales + ", presentationMainMerchandiseGroup="
				+ presentationMainMerchandiseGroup + ", priorityAssortmentCode="+priorityAssortmentCode+"]";
	}


}
