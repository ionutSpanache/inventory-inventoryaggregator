package net.metrosystems.inventoryaggregator.articleattributes.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleComponent;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.DataType;
import net.metrosystems.inventoryaggregator.articleattributes.model.ProductLifeCycleData;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawArticleData;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawArticleStoreMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.ArticleIdStockLocation;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.ArticleLocationForArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.StockData;
import net.metrosystems.inventoryaggregator.articleattributes.repository.AclBundleStockLocationRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.ArticleComponentRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.ArticleDataFromMovieRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.ArticleMasterDataRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.DataSourceRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.RawArticleStoreMasterDataRepository;
import net.metrosystems.inventoryaggregator.articlestoremasterdata.model.ArticleStoreMasterData;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.articleCacheStoreExceptions.model.ArticleCacheStoreExceptionsIhdsTree;
import net.metrosystems.inventoryaggregator.common.model.ArticleNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RawArticleDataListService {

  private final RawArticleStoreMasterDataRepository rawArticleStoreMasterDataRepository;
  private final ArticleDataFromMovieRepository articleDataFromMovieRepository;
  private final AclBundleStockLocationRepository aclBundleStockLocationRepository;
  private final ArticleComponentRepository articleComponentRepository;
  private final DataSourceRepository dataSourceRepository;

  public void buildRawArticleDataList(
      List<ArticleId> articleIdList,
      String countryCode,
      String salesLine,
      String languageCode,
      Integer storeNo,
      List<RawArticleData> rawArticleDataList,
      Optional<List<String>> dataTypeList)
      throws ExecutionException, InterruptedException {

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

    CompletableFuture<Map<ArticleId, ArticleMasterData>> articleMasterDataForArticleId =
        CompletableFuture.completedFuture(Collections.emptyMap());
    if (dataTypeList.isEmpty()
        || dataTypeList.get().contains(DataType.ARTICLE_MASTER_DATA.getDataType())) {
      try {
        articleMasterDataForArticleId =
            dataSourceRepository.getArticleMasterDataForArticleIdList(
                articleIdList, countryCode, salesLine, languageCode, storeNo);
      } catch (ArticleNotFoundException ex) {
        throw new ArticleNotFoundException();
      } catch (Exception e) {
        log.error(
            "ArticleListDetailsService.buildRawArticleDataList - articleMasterDataForArticleId - error: {}",
            e.getMessage(),
            e);
      }
    }

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

    CompletableFuture<List<ArticleIdStockLocation>> articleStockLocationList =
        CompletableFuture.completedFuture(Collections.emptyList());
    if (dataTypeList.isEmpty()
        || dataTypeList.get().contains(DataType.ARTICLE_STOCK_LOCATION.getDataType())) {
      try {
        articleStockLocationList =
            aclBundleStockLocationRepository.getStockLocationsForArticleList(
                articleIdList, countryCode, salesLine, storeNo);
      } catch (ArticleNotFoundException ex) {
        throw new ArticleNotFoundException();
      } catch (Exception e) {
        log.error(
            "ArticleListDetailsService.articleStockLocationList - articleDataFromMovieRepository - error: {}",
            e.getMessage(),
            e);
      }
    }

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

    CompletableFuture.allOf(
        articleComponentForArticleId,
        articleMasterDataForArticleId,
        articleStoreMasterFromAcl,
        articleStoreExceptionsFromAC,
        stockDataFromMovie,
        articleStockLocationList,
        articleLocationList);

    Map<ArticleId, RawArticleStoreMasterData> rawArticleStoreMasterDataMap =
        rawArticleStoreMasterDataRepository.getArticleIdRawArticleStoreMasterDataMap(
            articleStoreMasterFromAcl.get(), articleStoreExceptionsFromAC.get());

    Map<ArticleId, StockData> stockDataFromMovieMap =
        stockDataFromMovie.get().stream()
            .collect(
                Collectors.toMap(
                    StockData::getArticleId,
                    stockData -> stockData,
                    (oldValue, newValue) -> oldValue));
    Map<ArticleId, ArticleIdStockLocation> articleStockLocationListMap =
        articleStockLocationList.get().stream()
            .collect(
                Collectors.toMap(
                    ArticleIdStockLocation::getArticleId,
                    articleIdStockLocation -> articleIdStockLocation,
                    (oldValue, newValue) -> oldValue));
    Map<ArticleId, ArticleLocationForArticleId> articleLocationListMap =
        articleLocationList.get().stream()
            .collect(
                Collectors.toMap(
                    ArticleLocationForArticleId::getArticleId,
                    articleLocationForArticleId -> articleLocationForArticleId,
                    (oldValue, newValue) -> oldValue));

    Map<ArticleId, ProductLifeCycleData> productLifeCycleMap =
        articleMasterDataForArticleId.get().entrySet().stream()
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    entry ->
                        Optional.ofNullable(buildProductLifeCycleData(entry.getValue()))
                            .orElse(new ProductLifeCycleData())));

    for (RawArticleData rawArticleData : rawArticleDataList) {
      rawArticleData.setArticleMasterData(
          articleMasterDataForArticleId.get().get(rawArticleData.getArticleId()));
      rawArticleData.setArticleStoreMasterData(
          rawArticleStoreMasterDataMap.getOrDefault(
              rawArticleData.getArticleId(), RawArticleStoreMasterData.builder().build()));
      rawArticleData.setStockDetails(
          Optional.ofNullable(stockDataFromMovieMap.get(rawArticleData.getArticleId()))
              .map(StockData::getArticleStockData)
              .orElse(null));
      rawArticleData.setPlannedDeliveryDates(
          Optional.ofNullable(stockDataFromMovieMap.get(rawArticleData.getArticleId()))
              .map(StockData::getArticlePlannedDeliveryDates)
              .orElse(Collections.emptyList()));
      rawArticleData.setStockLocations(
          Optional.ofNullable(articleStockLocationListMap.get(rawArticleData.getArticleId()))
              .map(ArticleIdStockLocation::getArticleStockLocationList)
              .orElse(Collections.emptyList()));
      rawArticleData.setArticleLocation(
          Optional.ofNullable(articleLocationListMap.get(rawArticleData.getArticleId()))
              .map(ArticleLocationForArticleId::getArticleLocation)
              .orElse(null));
      rawArticleData.setArticleComponent(
          articleComponentForArticleId.get().get(rawArticleData.getArticleId()));
      rawArticleData.setProductLifeCycleData(
          productLifeCycleMap.getOrDefault(
              rawArticleData.getArticleId(), new ProductLifeCycleData()));
    }
  }

  private ProductLifeCycleData buildProductLifeCycleData(ArticleMasterData articleMasterData) {
    return ProductLifeCycleData.builder()
        .assortmentCluster(articleMasterData.getAssortmentCluster())
        .presencePeriodEndWeek(articleMasterData.getPresencePeriodEndWeek())
        .presencePeriodEndYear(articleMasterData.getPresencePeriodEndYear())
        .presencePeriodStartWeek(articleMasterData.getPresencePeriodStartWeek())
        .presencePeriodStartYear(articleMasterData.getPresencePeriodStartYear())
        .build();
  }
}
