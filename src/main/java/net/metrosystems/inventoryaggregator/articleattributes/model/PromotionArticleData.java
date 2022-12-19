package net.metrosystems.inventoryaggregator.articleattributes.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_promotion_article.PromotionDescription;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionArticleData implements Serializable {

  private Boolean valid;
  private Double promotionNetSalesPricePerBundle;
  private Double promotionNetSalesPricePerPiece;
  private List<PromotionDescription> descriptions;
  private LocalDate promotionEnd;
  private LocalDate promotionStart;
  private String promotionId;
}
