package net.metrosystems.inventoryaggregator.articlemasterdata.service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterId;
import net.metrosystems.inventoryaggregator.articleattributes.repository.ArticleMasterDataRepository;
import net.metrosystems.inventoryaggregator.articlemasterdata.client.DataSourceClient;
import net.metrosystems.inventoryaggregator.featuretoggle.DataSourceFeatureToggle;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleMasterDataService {
  private static final String SALES_LINE = "MCC";
  private final DataSourceClient dataSourceClient;
  private final DataSourceFeatureToggle dataSourceFeatureToggle;
  private final ArticleMasterDataRepository articleMasterDataRepository;

  public List<ArticleMasterId> getArticleMasterDataForArticleIdList(
      List<ArticleId> articleIdList, String countryCode, String languageCode, Integer storeNo)
      throws ExecutionException, InterruptedException {
    boolean dataSourceActive = dataSourceFeatureToggle.isDataSourceActive(countryCode);
    if (!dataSourceActive) {
      return articleMasterDataRepository
          .getArticleMasterDataForArticleIdListSync(
              articleIdList, countryCode, SALES_LINE, languageCode, storeNo)
          .entrySet()
          .stream()
          .map(
              entrySet ->
                  ArticleMasterId.builder()
                      .articleId(entrySet.getKey())
                      .articleMasterData(entrySet.getValue())
                      .build())
          .toList();
    }
    return dataSourceClient.getArticleMasterDataForArticleIdList(
        articleIdList, countryCode, languageCode, storeNo);
  }

  public ArticleMasterData getArticleMasterIdForArticleId(
      ArticleId articleId, String countryCode, String languageCode, Integer storeNumber)
      throws Exception {
    boolean dataSourceActive = dataSourceFeatureToggle.isDataSourceActive(countryCode);

    if (!dataSourceActive) {
      return articleMasterDataRepository.getArticleMasterDataSync(
          articleId, countryCode, SALES_LINE, languageCode, storeNumber);
    }

    ArticleMasterId articleMasterIdForArticleId =
        dataSourceClient.getArticleMasterIdForArticleId(
            articleId, countryCode, languageCode, storeNumber);
    if (articleMasterIdForArticleId != null) {
      return articleMasterIdForArticleId.getArticleMasterData();
    }
    return null;
  }
}
