package net.metrosystems.inventoryaggregator.articlemasterdata.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterId;
import net.metrosystems.inventoryaggregator.articlemasterdata.client.DataSourceClient;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleMasterDataService {
  private final DataSourceClient dataSourceClient;

  public ArticleMasterData getArticleMasterIdForArticleId(
      ArticleId articleId, String countryCode, String languageCode, Integer storeNumber) {

    ArticleMasterId articleMasterIdForArticleId =
        dataSourceClient.getArticleMasterIdForArticleId(
            articleId, countryCode, languageCode, storeNumber);
    if (articleMasterIdForArticleId != null) {
      return articleMasterIdForArticleId.getArticleMasterData();
    }
    return null;
  }
}
