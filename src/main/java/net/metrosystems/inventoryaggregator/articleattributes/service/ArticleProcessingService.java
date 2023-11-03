package net.metrosystems.inventoryaggregator.articleattributes.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleComponent;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleDataFromMovie;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawArticleStoreMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.StockData;
import net.metrosystems.inventoryaggregator.articleattributes.repository.ArticleComponentRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.ArticleDataFromMovieRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.ArticleMasterDataRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.RawArticleStoreMasterDataRepository;
import net.metrosystems.inventoryaggregator.articlestoremasterdata.model.ArticleStoreMasterData;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.articleCacheStoreExceptions.model.ArticleCacheStoreExceptionsIhdsTree;
import net.metrosystems.inventoryaggregator.common.model.ArticleNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleProcessingService {

  private final ArticleDataFromMovieRepository articleDataFromMovieRepository;
  private final ArticleComponentRepository articleComponentRepository;
  private final RawArticleStoreMasterDataRepository rawArticleStoreMasterDataRepository;
  private final ArticleMasterDataRepository articleMasterDataRepository;

  public CompletableFuture<List<StockData>> getStockDataFromMovie(
      List<ArticleId> articleIdList, String countryCode, String salesLine, Integer storeNo) {
    CompletableFuture<List<StockData>> stockDataFromMovie =
        CompletableFuture.completedFuture(Collections.emptyList());
    try {
      stockDataFromMovie =
          articleDataFromMovieRepository.getArticleListDataFromMovie(
              articleIdList, countryCode, salesLine, storeNo);

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

  public CompletableFuture<Map<ArticleId, ArticleCacheStoreExceptionsIhdsTree>>
      getArticleStoreExceptionsFromAC(
          List<ArticleId> articleIdList, String countryCode, String salesLine, Integer storeNo) {
    CompletableFuture<Map<ArticleId, ArticleCacheStoreExceptionsIhdsTree>>
        articleStoreExceptionsFromAC = CompletableFuture.completedFuture(Collections.emptyMap());
    try {
      articleStoreExceptionsFromAC =
          rawArticleStoreMasterDataRepository.getArticleIdArticleCacheStoreExceptionIhdsTree(
              countryCode, salesLine, storeNo, new HashSet<>(articleIdList));

    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      log.error(
          "ArticleListDetailsService.buildRawArticleDataList - articleStoreExceptionsFromAC - error: {}",
          e.getMessage(),
          e);
      Thread.currentThread().interrupt();
    }
    return articleStoreExceptionsFromAC;
  }

  public CompletableFuture<Map<ArticleId, ArticleStoreMasterData>> getArticleStoreMasterFromAcl(
      List<ArticleId> articleIdList, String countryCode, String salesLine, Integer storeNo) {
    CompletableFuture<Map<ArticleId, ArticleStoreMasterData>> articleStoreMasterFromAcl =
        CompletableFuture.completedFuture(Collections.emptyMap());
    try {
      articleStoreMasterFromAcl =
          rawArticleStoreMasterDataRepository.getArticleStoreMasterFromAcl(
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

  public CompletableFuture<Map<ArticleId, ArticleComponent>> getComponentForArticleId(
      List<ArticleId> articleIdList, String countryCode, String salesLine, String languageCode) {
    CompletableFuture<Map<ArticleId, ArticleComponent>> articleComponentForArticleId =
        CompletableFuture.completedFuture(Collections.emptyMap());
    try {
      articleComponentForArticleId =
          articleComponentRepository.getArticleComponentForArticleIdList(
              articleIdList, countryCode, salesLine, languageCode);
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      log.error(
          "ArticleListDetailsService.buildRawArticleDataList - articleComponentForArticleId - error: {}",
          e.getMessage(),
          e);
    }
    return articleComponentForArticleId;
  }

  public CompletableFuture<ArticleMasterData> getArticleMasterData(
      ArticleId articleId,
      String countryCode,
      String salesLine,
      String languageCode,
      Integer storeNo) {
    CompletableFuture<ArticleMasterData> articleMasterData = new CompletableFuture<>();
    try {
      articleMasterData =
          articleMasterDataRepository.getArticleMasterData(
              articleId, countryCode, salesLine, languageCode, storeNo);
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      log.error(
          "ArticleDetailsService.buildRawArticleDataList - articleMasterData - error: {}",
          e.getMessage(),
          e);
    }
    return articleMasterData;
  }

  public CompletableFuture<RawArticleStoreMasterData> getRawArticleStoreMasterData(
      ArticleId articleId, String countryCode, String salesLine, Integer storeNo) {
    CompletableFuture<RawArticleStoreMasterData> rawArticleStoreMasterData =
        new CompletableFuture<>();
    try {
      rawArticleStoreMasterData =
          rawArticleStoreMasterDataRepository.getRawArticleStoreMasterData(
              articleId, countryCode, salesLine, storeNo);
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      Thread.currentThread().interrupt();
      log.error(
          "ArticleDetailsService.buildRawArticleDataList - rawArticleStoreMasterData - error: {}",
          e.getMessage(),
          e);
    }
    return rawArticleStoreMasterData;
  }

  public CompletableFuture<ArticleDataFromMovie> getArticleDataFromMovie(
      ArticleId articleId, String countryCode, String salesLine, Integer storeNo) {
    CompletableFuture<ArticleDataFromMovie> articleDataFromMovie = new CompletableFuture<>();
    try {
      articleDataFromMovie =
          articleDataFromMovieRepository.getArticleDataFromMovie(
              articleId, countryCode, salesLine, storeNo);
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      log.error(
          "ArticleDetailsService.buildRawArticleDataList - articleDataFromMovie - error: {}",
          e.getMessage(),
          e);
    }
    return articleDataFromMovie;
  }
}
