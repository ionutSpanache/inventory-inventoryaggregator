package net.metrosystems.inventoryaggregator.articleattributes.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionArticleData implements Serializable {

  private String promotionId;
  private Double promotionNetSalesPricePerBundle;
  private Double promotionNetSalesPricePerPiece;
  private LocalDate promotionStart;
  private LocalDate promotionEnd;
  @JsonIgnore private Boolean valid;
  private List<String> descriptions;
}
