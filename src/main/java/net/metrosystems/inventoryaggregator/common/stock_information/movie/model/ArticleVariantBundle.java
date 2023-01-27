package net.metrosystems.inventoryaggregator.common.stock_information.movie.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleVariantBundle {
  private Long articleNo;
  private Long variant;
  private Long bundleNo;
}
