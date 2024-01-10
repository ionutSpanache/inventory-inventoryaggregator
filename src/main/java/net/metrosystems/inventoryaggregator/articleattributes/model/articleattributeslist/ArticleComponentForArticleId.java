package net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.metrosystems.inventoryaggregator.articleattributes.model.articlecomponent.ArticleComponent;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleComponentForArticleId {
  private ArticleId articleId;
  private ArticleComponent articleComponent;
}
