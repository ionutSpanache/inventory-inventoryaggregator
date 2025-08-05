package net.metrosystems.inventoryaggregator.articleattributes.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleMasterId {
  private ArticleId articleId;
  private ArticleMasterData articleMasterData;
  private ArticleStoreMasterData articleStoreMasterData;
  private MultiLanguageData multiLanguageData;
}
