package net.metrosystems.inventoryaggregator.common.article_information.articlecache.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.metrosystems.inventoryaggregator.articleattributes.model.MinimumRetailPrice;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleCacheFullData {

    private Long articleNumber;
    private Long variantNumber;
    private Long bundleNumber;
    private String articleDescription;
    private String barcode;
    private Integer commercialSupplier;
    private Integer presentationMerchandiseGroup;
    private Integer sellingBundleContent;
    private Double weightArticle;
    private Double netWeight;
    private Long subsysArticleNumber;
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
    private Integer packageFactor;
    private String alcoholClass;
    private Long traceabilityIndicator;
    private Integer coreAssortmentIndicator;
    private String assignedStoreSupplierType;
    private Integer inOutIndicator;
    private Double minimumGrossSalesPricePerUnit;
}
