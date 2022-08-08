package net.metrosystems.inventoryaggregator.articleattributes.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleMasterData implements Serializable {
  private static final long serialVersionUID = -2971487066548844011L;
  private String articleDescription;
  private String barcode;
  private Integer commercialSupplier;
  private Integer presentationMerchandiseGroup;
  private String promotionId;
  private Integer sellingBundleContent;
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

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  @Override
  public String toString() {
    return "ArticleMasterData{" +
            "articleDescription='" + articleDescription + '\'' +
            ", barcode='" + barcode + '\'' +
            ", commercialSupplier=" + commercialSupplier +
            ", presentationMerchandiseGroup=" + presentationMerchandiseGroup +
            ", promotionId='" + promotionId + '\'' +
            ", sellingBundleContent=" + sellingBundleContent +
            ", packageFactor=" + packageFactor +
            ", weightArticle=" + weightArticle +
            ", subsystemArticleNumber=" + subsystemArticleNumber +
            ", vatPercent=" + vatPercent +
            ", pieceNetSellPrice=" + pieceNetSellPrice +
            ", bundleNetSellPrice=" + bundleNetSellPrice +
            ", promotionBundleNetPrice=" + promotionBundleNetPrice +
            ", variantDescription='" + variantDescription + '\'' +
            ", articleType=" + articleType +
            ", articleClass='" + articleClass + '\'' +
            ", foodNonfoodIndicator='" + foodNonfoodIndicator + '\'' +
            ", presentationMainMerchandiseGroup=" + presentationMainMerchandiseGroup +
            ", presentationSubMerchandiseGroup=" + presentationSubMerchandiseGroup +
            ", supplierNumber=" + supplierNumber +
            ", procurementCode=" + procurementCode +
            ", assortmentCode='" + assortmentCode + '\'' +
            ", blockingCode=" + blockingCode +
            ", activationStatus=" + activationStatus +
            ", expiryDays=" + expiryDays +
            ", pieceNetBuyingPrice=" + pieceNetBuyingPrice +
            ", bundleNetBuyingPrice=" + bundleNetBuyingPrice +
            ", pieceNetNetBuyingPrice=" + pieceNetNetBuyingPrice +
            ", bundleNetNetBuyingPrice=" + bundleNetNetBuyingPrice +
            ", articleNoEmptiesSales=" + articleNoEmptiesSales +
            ", brandName='" + brandName + '\'' +
            ", minimumRetailPrice=" + minimumRetailPrice +
            ", countryOfOrigin='" + countryOfOrigin + '\'' +
            ", isTradeItemExcludedFromDiscounts=" + isTradeItemExcludedFromDiscounts +
            ", gradeCodeReferenceFruitsVegetables='" + gradeCodeReferenceFruitsVegetables + '\'' +
            ", alcoholClass='" + alcoholClass + '\'' +
            ", traceabilityIndicator=" + traceabilityIndicator +
            ", coreAssortmentIndicator=" + coreAssortmentIndicator +
            ", assignedStoreSupplierType=" + assignedStoreSupplierType +
            '}';
  }
}
