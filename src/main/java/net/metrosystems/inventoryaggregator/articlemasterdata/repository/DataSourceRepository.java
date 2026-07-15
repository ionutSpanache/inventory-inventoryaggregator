package net.metrosystems.inventoryaggregator.articlemasterdata.repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterId;
import net.metrosystems.inventoryaggregator.articleattributes.model.articlecomponent.ArticleComponent;
import net.metrosystems.inventoryaggregator.articlemasterdata.client.DataSourceClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataSourceRepository {
  private final DataSourceClient dataSourceClient;

  @Async("asyncExecutor")
  public CompletableFuture<Map<ArticleId, ArticleMasterId>>
      getArticleMasterDataForArticleIdListAsync(
          List<ArticleId> articleIdList,
          String countryCode,
          String languageCode,
          Integer storeNo,
          List<String> dataTypeList) {
    return CompletableFuture.completedFuture(
        dataSourceClient.getArticleMasterIdByArticleId(
            articleIdList, countryCode, languageCode, storeNo, dataTypeList));
  }

  @Async("asyncExecutor")
  public CompletableFuture<ArticleMasterId> getArticleMasterIdForArticleIdAsync(
      ArticleId articleId,
      String countryCode,
      String languageCode,
      Integer storeNo,
      List<String> dataTypeList) {
    return CompletableFuture.completedFuture(
        dataSourceClient.getArticleMasterIdForArticleId(
            articleId, countryCode, languageCode, storeNo, dataTypeList));
  }

  @Async("asyncExecutor")
  public CompletableFuture<ArticleComponent> getArticleComponentAsync(
      String countryCode, Integer storeNumber, Long articleNumber) {
    return CompletableFuture.completedFuture(
        dataSourceClient.getArticleComponent(countryCode, storeNumber, articleNumber));
  }

  @Async("asyncExecutor")
  public CompletableFuture<Map<ArticleId, ArticleComponent>> getArticleComponentsAsync(
      String countryCode, Integer storeNumber, List<ArticleId> articleIds) {
    return CompletableFuture.completedFuture(
        dataSourceClient.getArticleComponents(countryCode, storeNumber, articleIds));
  }
}
