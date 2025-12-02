package net.metrosystems.inventoryaggregator.articleattributes.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleMasterData {
  private String articleDescription;
  private String barcode;
  private Integer commercialSupplier;
  private Integer presentationMerchandiseGroup;
  private String promotionId;
  private String promotionDescription;
  private LocalDate promotionStartDate;
  private LocalDate promotionEndDate;
  private Integer sellingBundleContent;
  private Integer buyingBundleContent;
  private Integer packageFactor;
  private Double weightArticle;
  private Double netWeight;
  private Long subsystemArticleNumber;
  private Double vatPercent;
  private double pieceNetSellPrice;
  private double bundleNetSellPrice;
  private double promotionBundleNetPrice;
  private String variantDescription;
  private Long articleType;
  private String articleClass;
  private String foodNonfoodIndicator;
  private Long presentationMainMerchandiseGroup;
  private Long presentationSubMerchandiseGroup;
  private Integer supplierNumber;
  private Byte procurementCode;
  private String assortmentCode;
  private Byte blockingCode;
  private Byte activationStatus;
  private Integer expiryDays;
  private double pieceNetBuyingPrice;
  private double bundleNetBuyingPrice;
  private double pieceNetNetBuyingPrice;
  private double bundleNetNetBuyingPrice;
  private Long articleNoEmptiesSales;
  private String brandName;
  private MinimumRetailPrice minimumRetailPrice;
  private String countryOfOrigin;
  private Boolean isTradeItemExcludedFromDiscounts;
  private String gradeCodeReferenceFruitsVegetables;
  private String alcoholClass;
  private Long traceabilityIndicator;
  private Integer coreAssortmentIndicator;
  private String assignedStoreSupplierType;
  private Integer inOutIndicator;

  private Long presencePeriodEndYear;
  private Long presencePeriodEndWeek;
  private Long presencePeriodStartYear;
  private Long presencePeriodStartWeek;
  private String assortmentCluster;
  private BigDecimal minimumGrossSalesPricePerUnit;
  private Boolean isTradeItemInSellOut;
  private Boolean isTradeItemSellableInPFSShop;
  private Boolean isTradeItemDiscountMachine;
  private Boolean isMerchandiseOwnBrand;
  private Double basPriceContent;
  private String basPriceContentMeasureUnit;
  private Integer basPriceContentUnit;
  private Integer basPriceBaseFactor;
  private String channelDeliveryFlag;
  private Long returnPossibilityStoreSupplierNumber;
  private Boolean isTradeItemInConsignment;
  private String seasonCode;
  private Boolean isTradeItemOnOrder;
  private Long supplierArticleNumber;
  private Boolean exciseIndicator;
  private String packagingTypeId;
  private Double netContent;
  private Boolean isTradeItemOneTouchPallet;
}
