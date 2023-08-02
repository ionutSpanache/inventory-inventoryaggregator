package net.metrosystems.inventoryaggregator.articleattributes.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimplePromotionArticleData  {
  private String promotionDescription;
  private LocalDate promotionEnd;
  private LocalDate promotionStart;
  private String promotionId;
  private Double promotionNetSalesPricePerBundle;
  private String promotionChannel;
}
