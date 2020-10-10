package net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.articlelocation.ArticleLocation;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleLocationForArticleId {
  private ArticleId articleId;
  private ArticleLocation articleLocation;
}
