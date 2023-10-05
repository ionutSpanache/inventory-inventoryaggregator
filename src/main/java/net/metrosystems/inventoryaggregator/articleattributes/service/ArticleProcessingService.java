package net.metrosystems.inventoryaggregator.articleattributes.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.*;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.ArticleIdStockLocation;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.ArticleLocationForArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.StockData;
import net.metrosystems.inventoryaggregator.articleattributes.model.articlelocation.ArticleLocation;
import net.metrosystems.inventoryaggregator.articleattributes.repository.*;
import net.metrosystems.inventoryaggregator.articleattributes.v2.repository.DataSourceRepository;
import net.metrosystems.inventoryaggregator.articlestoremasterdata.model.ArticleStoreMasterData;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.articleCacheStoreExceptions.model.ArticleCacheStoreExceptionsIhdsTree;
import net.metrosystems.inventoryaggregator.common.model.ArticleNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleProcessingService {

  private final ArticleDataFromMovieRepository articleDataFromMovieRepository;
  private final AclBundleStockLocationRepository aclBundleStockLocationRepository;
  private final ArticleComponentRepository articleComponentRepository;
  private final RawArticleStoreMasterDataRepository rawArticleStoreMasterDataRepository;
  private final ArticleMasterDataRepository articleMasterDataRepository;

  public CompletableFuture<List<ArticleLocationForArticleId>> getArticleLocationList(
      List<ArticleId> articleIdList,
      String countryCode,
      String salesLine,
      Integer storeNo,
      Optional<List<String>> dataTypeList) {
    CompletableFuture<List<ArticleLocationForArticleId>> articleLocationList =
        CompletableFuture.completedFuture(Collections.emptyList());
    if (dataTypeList.isEmpty()
        || dataTypeList.get().contains(DataType.ARTICLE_LOCATION.getDataType())) {
      try {
        articleLocationList =
            aclBundleStockLocationRepository.getArticleLocationForArticleList(
                countryCode, salesLine, storeNo, articleIdList);
      } catch (ArticleNotFoundException ex) {
        throw new ArticleNotFoundException();
      } catch (Exception e) {
        log.error(
            "ArticleListDetailsService.articleStockLocationList - articleLocationList - error: {}",
            e.getMessage(),
            e);
      }
    }
    return articleLocationList;
  }

  public CompletableFuture<List<StockData>> getStockDataFromMovie(
      List<ArticleId> articleIdList,
      String countryCode,
      String salesLine,
      Integer storeNo,
      Optional<List<String>> dataTypeList) {
    CompletableFuture<List<StockData>> stockDataFromMovie =
        CompletableFuture.completedFuture(Collections.emptyList());
    if (dataTypeList.isEmpty() || dataTypeList.get().contains(DataType.STOCK_DATA.getDataType())) {
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
    }
    return stockDataFromMovie;
  }

  public CompletableFuture<Map<ArticleId, ArticleCacheStoreExceptionsIhdsTree>>
  getArticleStoreExceptionsFromAC(
      List<ArticleId> articleIdList,
      String countryCode,
      String salesLine,
      Integer storeNo,
      Optional<List<String>> dataTypeList) {
    CompletableFuture<Map<ArticleId, ArticleCacheStoreExceptionsIhdsTree>>
        articleStoreExceptionsFromAC = CompletableFuture.completedFuture(Collections.emptyMap());
    if (dataTypeList.isEmpty()
        || dataTypeList.get().contains(DataType.ARTICLE_STORE_EXCEPTIONS.getDataType())) {
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
    }
    return articleStoreExceptionsFromAC;
  }

  public CompletableFuture<Map<ArticleId, ArticleStoreMasterData>> getArticleStoreMasterFromAcl(
      List<ArticleId> articleIdList,
      String countryCode,
      String salesLine,
      Integer storeNo,
      Optional<List<String>> dataTypeList) {
    CompletableFuture<Map<ArticleId, ArticleStoreMasterData>> articleStoreMasterFromAcl =
        CompletableFuture.completedFuture(Collections.emptyMap());
    if (dataTypeList.isEmpty()
        || dataTypeList.get().contains(DataType.ARTICLE_STORE_MASTER.getDataType())) {
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
    }
    return articleStoreMasterFromAcl;
  }

  public CompletableFuture<Map<ArticleId, ArticleComponent>> getComponentForArticleId(
      List<ArticleId> articleIdList,
      String countryCode,
      String salesLine,
      String languageCode,
      Optional<List<String>> dataTypeList) {
    CompletableFuture<Map<ArticleId, ArticleComponent>> articleComponentForArticleId =
        CompletableFuture.completedFuture(Collections.emptyMap());
    if (dataTypeList.isEmpty()
        || dataTypeList.get().contains(DataType.ARTICLE_COMPONENT.getDataType())) {
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

  public CompletableFuture<ArticleLocation> getArticleLocationList(
      ArticleId articleId, String countryCode, String salesLine, Integer storeNo) {
    CompletableFuture<ArticleLocation> articleLocationList = new CompletableFuture<>();
    try {
      articleLocationList =
          aclBundleStockLocationRepository.getArticleLocation(
              countryCode, salesLine, storeNo, articleId);
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      log.error(
          "ArticleDetailsService.buildRawArticleDataList - articleLocationList - error: {}",
          e.getMessage(),
          e);
    }
    return articleLocationList;
  }

  public CompletableFuture<ArticleLocation> getArticleLocation(
      ArticleId articleId, String countryCode, String salesLine, Integer storeNo) {
    CompletableFuture<ArticleLocation> articleLocationList = new CompletableFuture<>();
    try {
      articleLocationList =
          aclBundleStockLocationRepository.getArticleLocation(
              countryCode, salesLine, storeNo, articleId);
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      log.error(
          "RawArticleDataService.buildRawArticleDataList - articleLocationList - error: {}",
          e.getMessage(),
          e);
    }
    return articleLocationList;
  }
}
