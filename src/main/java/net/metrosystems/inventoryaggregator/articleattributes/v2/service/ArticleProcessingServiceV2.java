package net.metrosystems.inventoryaggregator.articleattributes.v2.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterId;
import net.metrosystems.inventoryaggregator.articleattributes.model.DataType;
import net.metrosystems.inventoryaggregator.articleattributes.v2.repository.DataSourceRepository;
import net.metrosystems.inventoryaggregator.common.model.ArticleNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleProcessingServiceV2 {
  private final DataSourceRepository dataSourceRepository;

  public CompletableFuture<ArticleMasterId> getArticleMasterId(
      ArticleId articleId,
      String countryCode,
      String languageCode,
      Integer storeNo) {
    CompletableFuture<ArticleMasterId> articleMasterIdForArticleId = new CompletableFuture<>();
    try {
      articleMasterIdForArticleId =
          dataSourceRepository.getArticleMasterIdForArticleId(
              articleId, countryCode, languageCode, storeNo);
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      log.error(
          "RawArticleDataService.buildRawArticleDataList - articleMasterIdForArticleId - error: {}",
          e.getMessage(),
          e);
    }
    return articleMasterIdForArticleId;
  }

  public CompletableFuture<Map<ArticleId, ArticleMasterData>> getMasterDataForArticleId(
      List<ArticleId> articleIdList,
      String countryCode,
      String languageCode,
      Integer storeNo) {
    CompletableFuture<Map<ArticleId, ArticleMasterData>> articleMasterDataForArticleId =
        CompletableFuture.completedFuture(Collections.emptyMap());
      try {
        articleMasterDataForArticleId =
            dataSourceRepository.getArticleMasterDataForArticleIdList(
                articleIdList, countryCode, languageCode, storeNo);
      } catch (ArticleNotFoundException ex) {
        throw new ArticleNotFoundException();
      } catch (Exception e) {
        log.error(
            "ArticleListDetailsService.buildRawArticleDataList - articleMasterDataForArticleId - error: {}",
            e.getMessage(),
            e);
    }
    return articleMasterDataForArticleId;
  }
}
