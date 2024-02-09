package net.metrosystems.inventoryaggregator.articleattributes.v2.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterId;
import net.metrosystems.inventoryaggregator.articleattributes.model.articlecomponent.ArticleComponent;
import net.metrosystems.inventoryaggregator.articlemasterdata.repository.DataSourceRepository;
import net.metrosystems.inventoryaggregator.common.model.ArticleNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleProcessingServiceV2 {
  private final DataSourceRepository dataSourceRepository;

  public CompletableFuture<ArticleMasterId> getArticleMasterId(
      ArticleId articleId, String countryCode, String languageCode, Integer storeNo) {
    CompletableFuture<ArticleMasterId> articleMasterIdForArticleId = new CompletableFuture<>();
    try {
      articleMasterIdForArticleId =
          dataSourceRepository.getArticleMasterIdForArticleIdAsync(
              articleId, countryCode, languageCode, storeNo);
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      log.error("ArticleProcessingServiceV2.getArticleMasterId - error: {}", e.getMessage(), e);
    }
    return articleMasterIdForArticleId;
  }

  public CompletableFuture<Map<ArticleId, ArticleMasterData>> getMasterDataForArticleId(
      List<ArticleId> articleIdList, String countryCode, String languageCode, Integer storeNo) {
    CompletableFuture<Map<ArticleId, ArticleMasterData>> articleMasterDataForArticleId =
        CompletableFuture.completedFuture(Collections.emptyMap());
    try {
      articleMasterDataForArticleId =
          dataSourceRepository.getArticleMasterDataForArticleIdListAsync(
              articleIdList, countryCode, languageCode, storeNo);
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      log.error(
          "ArticleProcessingServiceV2.getMasterDataForArticleId - error: {}", e.getMessage(), e);
    }
    return articleMasterDataForArticleId;
  }

  public CompletableFuture<ArticleComponent> getArticleComponent(
      String countryCode,Integer storeNumber, Long articleNumber) {
    CompletableFuture<ArticleComponent> articleComponentCompletableFuture =
        new CompletableFuture<>();
    try {
      articleComponentCompletableFuture =
          dataSourceRepository.getArticleComponentAsync(countryCode,storeNumber, articleNumber);
    } catch (Exception e) {
      log.error("ArticleProcessingServiceV2.getArticleComponent - error: {}", e.getMessage(), e);
    }
    return articleComponentCompletableFuture;
  }

  public CompletableFuture<Map<ArticleId, ArticleComponent>> getArticleComponents(
      String countryCode, Integer storeNumber, List<ArticleId> articleIdList) {
    CompletableFuture<Map<ArticleId, ArticleComponent>> articleComponentsCompletableFuture =
        CompletableFuture.completedFuture(Collections.emptyMap());

    try {
      List<ArticleId> articleIdsWithoutDuplicates = articleIdList.stream().distinct().toList();
      articleComponentsCompletableFuture =
          dataSourceRepository.getArticleComponentsAsync(countryCode, storeNumber, articleIdsWithoutDuplicates);
    } catch (Exception e) {
      log.error("ArticleProcessingServiceV2.getArticleComponents - error: {}", e.getMessage(), e);
    }
    return articleComponentsCompletableFuture;
  }
}
