package net.metrosystems.inventoryaggregator.articleattributes.model.articlecomponent;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleVariant {
  private Integer variantNumber;
  private List<ArticleBundle> bundleList;

}
