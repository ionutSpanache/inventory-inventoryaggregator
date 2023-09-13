package net.metrosystems.inventoryaggregator.articleattributes.v2.service;

import java.util.Collections;
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
import net.metrosystems.inventoryaggregator.articleattributes.model.ProductLifeCycleData;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawArticleData;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawArticleStoreMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.ArticleIdStockLocation;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.ArticleLocationForArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.StockData;
import net.metrosystems.inventoryaggregator.articleattributes.repository.RawArticleStoreMasterDataRepository;
import net.metrosystems.inventoryaggregator.articleattributes.service.ArticleProcessingService;
import net.metrosystems.inventoryaggregator.articlestoremasterdata.model.ArticleStoreMasterData;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.articleCacheStoreExceptions.model.ArticleCacheStoreExceptionsIhdsTree;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RawArticleDataListService {
  private final ArticleProcessingService articleProcessingService;
  private final ArticleProcessingServiceV2 articleProcessingServiceV2;
  private final RawArticleStoreMasterDataRepository rawArticleStoreMasterDataRepository;

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
        articleProcessingService.getComponentForArticleId(
            articleIdList, countryCode, salesLine, languageCode, dataTypeList);

    CompletableFuture<Map<ArticleId, ArticleMasterData>> articleMasterDataForArticleId =
        articleProcessingServiceV2.getMasterDataForArticleId(
            articleIdList, countryCode, languageCode, storeNo, dataTypeList);

    CompletableFuture<Map<ArticleId, ArticleStoreMasterData>> articleStoreMasterFromAcl =
        articleProcessingService.getArticleStoreMasterFromAcl(
            articleIdList, countryCode, salesLine, storeNo, dataTypeList);

    CompletableFuture<Map<ArticleId, ArticleCacheStoreExceptionsIhdsTree>>
        articleStoreExceptionsFromAC =
            articleProcessingService.getArticleStoreExceptionsFromAC(
                articleIdList, countryCode, salesLine, storeNo, dataTypeList);

    CompletableFuture<List<StockData>> stockDataFromMovie =
        articleProcessingService.getStockDataFromMovie(
            articleIdList, countryCode, salesLine, storeNo, dataTypeList);

    CompletableFuture<List<ArticleIdStockLocation>> articleStockLocationList =
        articleProcessingService.getArticleStockLocation(
            articleIdList, countryCode, salesLine, storeNo, dataTypeList);

    CompletableFuture<List<ArticleLocationForArticleId>> articleLocationList =
        articleProcessingService.getArticleLocationList(
            articleIdList, countryCode, salesLine, storeNo, dataTypeList);

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
