package net.metrosystems.inventoryaggregator.articleattributes.model.articlecomponent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleIdComponent {
  private ArticleId articleId;
  private ArticleComponent articleComponent;
}
