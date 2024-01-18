package net.metrosystems.inventoryaggregator.articleattributes.v2.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.ProductLifeCycleData;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawArticleData;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawArticleStoreMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.StockData;
import net.metrosystems.inventoryaggregator.articleattributes.model.articlecomponent.ArticleComponent;
import net.metrosystems.inventoryaggregator.articleattributes.repository.RawArticleStoreMasterDataRepository;
import net.metrosystems.inventoryaggregator.articleattributes.service.ArticleProcessingService;
import net.metrosystems.inventoryaggregator.articlestoremasterdata.model.ArticleStoreMasterData;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.articleCacheStoreExceptions.model.ArticleCacheStoreExceptionsIhdsTree;
import net.metrosystems.inventoryaggregator.stocklocation.model.ArticleStockLocations;
import net.metrosystems.inventoryaggregator.stocklocation.repository.StockLocationRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RawArticleDataListService {

  private static final Map<String, String> LANGUAGE_CODE_EXCEPTIONS = new HashMap<>();

  static {
    LANGUAGE_CODE_EXCEPTIONS.put("UA", "UK");
    LANGUAGE_CODE_EXCEPTIONS.put("KZ", "RU");
    LANGUAGE_CODE_EXCEPTIONS.put("MD", "RO");
    LANGUAGE_CODE_EXCEPTIONS.put("PK", "UR");
    LANGUAGE_CODE_EXCEPTIONS.put("RS", "SR");
    LANGUAGE_CODE_EXCEPTIONS.put("CZ", "CS");
  }

  private final ArticleProcessingService articleProcessingService;
  private final ArticleProcessingServiceV2 articleProcessingServiceV2;
  private final RawArticleStoreMasterDataRepository rawArticleStoreMasterDataRepository;
  private final StockLocationRepository stockLocationRepository;

  public void buildRawArticleDataList(
      List<ArticleId> articleIdList,
      String countryCode,
      String salesLine,
      Integer storeNo,
      List<RawArticleData> rawArticleDataList,
      Optional<List<String>> dataTypeList)
      throws ExecutionException, InterruptedException {

    String correctLanguageCode = LANGUAGE_CODE_EXCEPTIONS.getOrDefault(countryCode, countryCode);

    CompletableFuture<Map<ArticleId, ArticleComponent>> articleComponentForArticleId =
        articleProcessingServiceV2.getArticleComponents(countryCode, storeNo, articleIdList);

    CompletableFuture<Map<ArticleId, ArticleMasterData>> articleMasterDataForArticleId =
        articleProcessingServiceV2.getMasterDataForArticleId(
            articleIdList, countryCode, correctLanguageCode, storeNo);

    CompletableFuture<Map<ArticleId, ArticleStoreMasterData>> articleStoreMasterFromAcl =
        articleProcessingService.getArticleStoreMasterFromAcl(
            articleIdList, countryCode, salesLine, storeNo);

    CompletableFuture<Map<ArticleId, ArticleCacheStoreExceptionsIhdsTree>>
        articleStoreExceptionsFromAC =
            articleProcessingService.getArticleStoreExceptionsFromAC(
                articleIdList, countryCode, salesLine, storeNo);

    CompletableFuture<List<StockData>> stockDataFromMovie =
        articleProcessingService.getStockDataFromMovie(
            articleIdList, countryCode, salesLine, storeNo);

    CompletableFuture<List<ArticleStockLocations>> articleStockLocationList =
        stockLocationRepository.getArticleStockLocation(
            articleIdList, countryCode, salesLine, storeNo, dataTypeList);

    CompletableFuture.allOf(
        articleComponentForArticleId,
        articleMasterDataForArticleId,
        articleStoreMasterFromAcl,
        articleStoreExceptionsFromAC,
        stockDataFromMovie,
        articleStockLocationList);

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
    Map<ArticleId, ArticleStockLocations> articleStockLocationListMap =
        articleStockLocationList.get().stream()
            .collect(
                Collectors.toMap(
                    ArticleStockLocations::getArticleId,
                    articleIdStockLocation -> articleIdStockLocation,
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
              .map(ArticleStockLocations::getStockLocations)
              .orElse(Collections.emptyList()));
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
