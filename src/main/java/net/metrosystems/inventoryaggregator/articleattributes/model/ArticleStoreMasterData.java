package net.metrosystems.inventoryaggregator.articleattributes.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleStoreMasterData {
  private Integer salesBlockingCode;
  private Double storePieceNetSellPrice;
  private Double storeBundleNetSellPrice;
  private Double storePieceGrossSellPrice;
  private Double storeBundleGrossSellPrice;
  private Date storePriceValidFrom;
  private Date storePriceValidTo;
  private Double storePromotionPieceNetPrice;
  private Double storePromotionBundleNetPrice;
  private Double storePromotionPieceGrossPrice;
  private Double storePromotionBundleGrossPrice;
  private Date storePromotionValidFrom;
  private Date storePromotionValidTo;
}
