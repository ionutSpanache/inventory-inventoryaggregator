package net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterData;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleMasterDataForArticleId {
  private ArticleId articleId;
  private ArticleMasterData articleMasterData;
}
