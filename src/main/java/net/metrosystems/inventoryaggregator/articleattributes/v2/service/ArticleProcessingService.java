package net.metrosystems.inventoryaggregator.articleattributes.v2.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterId;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.StockData;
import net.metrosystems.inventoryaggregator.articleattributes.model.articlecomponent.ArticleComponent;
import net.metrosystems.inventoryaggregator.articleattributes.repository.RawArticleStoreMasterDataRepository;
import net.metrosystems.inventoryaggregator.articlemasterdata.repository.DataSourceRepository;
import net.metrosystems.inventoryaggregator.articlestoremasterdata.model.AclStoreMasterData;
import net.metrosystems.inventoryaggregator.common.model.ArticleNotFoundException;
import net.metrosystems.inventoryaggregator.common.movieacl.MovieAclService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleProcessingService {
  private final DataSourceRepository dataSourceRepository;
  private final RawArticleStoreMasterDataRepository rawArticleStoreMasterDataRepository;
  private final MovieAclService movieAclService;

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

  public CompletableFuture<Map<ArticleId, ArticleMasterId>> getMasterDataForArticleId(
      List<ArticleId> articleIdList, String countryCode, String languageCode, Integer storeNo) {
    CompletableFuture<Map<ArticleId, ArticleMasterId>> articleMasterIdForArticleId =
        CompletableFuture.completedFuture(Collections.emptyMap());
    try {
      articleMasterIdForArticleId =
          dataSourceRepository.getArticleMasterDataForArticleIdListAsync(
              articleIdList, countryCode, languageCode, storeNo);
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      log.error(
          "ArticleProcessingServiceV2.getMasterDataForArticleId - error: {}", e.getMessage(), e);
    }
    return articleMasterIdForArticleId;
  }

  public CompletableFuture<ArticleComponent> getArticleComponent(
      String countryCode, Integer storeNumber, Long articleNumber) {
    CompletableFuture<ArticleComponent> articleComponentCompletableFuture =
        new CompletableFuture<>();
    try {
      articleComponentCompletableFuture =
          dataSourceRepository.getArticleComponentAsync(countryCode, storeNumber, articleNumber);
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
          dataSourceRepository.getArticleComponentsAsync(
              countryCode, storeNumber, articleIdsWithoutDuplicates);
    } catch (Exception e) {
      log.error("ArticleProcessingServiceV2.getArticleComponents - error: {}", e.getMessage(), e);
    }
    return articleComponentsCompletableFuture;
  }

  public CompletableFuture<List<StockData>> getStockDataFromMovie(
      List<ArticleId> articleIdList, String countryCode, String salesLine, Integer storeNo) {
    CompletableFuture<List<StockData>> stockDataFromMovie =
        CompletableFuture.completedFuture(Collections.emptyList());
    try {
      stockDataFromMovie =
          movieAclService.getStockDataListAsync(articleIdList, countryCode, salesLine, storeNo);

    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      log.error(
          "ArticleListDetailsService.buildRawArticleDataList - articleDataFromMovieRepository - error: {}",
          e.getMessage(),
          e);
    }
    return stockDataFromMovie;
  }

  public CompletableFuture<Map<ArticleId, AclStoreMasterData>> getAclStoreMasterDataMap(
      List<ArticleId> articleIdList, String countryCode, String salesLine, Integer storeNo) {
    CompletableFuture<Map<ArticleId, AclStoreMasterData>> articleStoreMasterFromAcl =
        CompletableFuture.completedFuture(Collections.emptyMap());
    try {
      articleStoreMasterFromAcl =
          rawArticleStoreMasterDataRepository.getAclStoreMasterDataMap(
              articleIdList, countryCode, salesLine, storeNo);

    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      log.error(
          "ArticleListDetailsService.buildRawArticleDataList - articleStoreMasterFromAcl - error: {}",
          e.getMessage(),
          e);
    }
    return articleStoreMasterFromAcl;
  }

  public CompletableFuture<AclStoreMasterData> getAclStoreMasterData(
      ArticleId articleId, String countryCode, String salesLine, Integer storeNo) {
    CompletableFuture<AclStoreMasterData> aclStoreMasterData = new CompletableFuture<>();
    try {
      aclStoreMasterData =
          rawArticleStoreMasterDataRepository.getAclStoreMasterData(
              articleId, countryCode, salesLine, storeNo);
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      Thread.currentThread().interrupt();
      log.error(
          "ArticleDetailsService.buildRawArticleDataList - aclStoreMasterData - error: {}",
          e.getMessage(),
          e);
    }
    return aclStoreMasterData;
  }

  public CompletableFuture<StockData> getArticleDataFromMovie(
      ArticleId articleId, String countryCode, String salesLine, Integer storeNo) {
    CompletableFuture<StockData> stockDataCompletableFuture = new CompletableFuture<>();
    try {
      stockDataCompletableFuture =
          movieAclService.getStockDataAsync(articleId, countryCode, salesLine, storeNo);
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      log.error(
          "ArticleDetailsService.buildRawArticleDataList - articleDataFromMovie - error: {}",
          e.getMessage(),
          e);
    }
    return stockDataCompletableFuture;
  }
}
