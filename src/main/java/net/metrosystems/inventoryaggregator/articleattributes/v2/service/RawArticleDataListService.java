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
import net.metrosystems.inventoryaggregator.articleattributes.model.*;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.StockData;
import net.metrosystems.inventoryaggregator.articleattributes.model.articlecomponent.ArticleComponent;
import net.metrosystems.inventoryaggregator.articleattributes.repository.RawArticleStoreMasterDataRepository;
import net.metrosystems.inventoryaggregator.articlestoremasterdata.model.AclStoreMasterData;
import net.metrosystems.inventoryaggregator.stocklocation.model.ArticleStockLocations;
import net.metrosystems.inventoryaggregator.stocklocation.repository.StockLocationRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RawArticleDataListService {

  private final ArticleProcessingService articleProcessingService;
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

    CompletableFuture<Map<ArticleId, ArticleComponent>> articleComponentForArticleId =
        articleProcessingService.getArticleComponents(countryCode, storeNo, articleIdList);

    CompletableFuture<Map<ArticleId, ArticleMasterId>> articleMasterId =
        articleProcessingService.getMasterDataForArticleId(
            articleIdList, countryCode, "EN", storeNo);

    CompletableFuture<Map<ArticleId, AclStoreMasterData>> articleStoreMasterFromAcl =
        articleProcessingService.getAclStoreMasterDataMap(
            articleIdList, countryCode, salesLine, storeNo);

    CompletableFuture<List<StockData>> stockDataFromMovie =
        articleProcessingService.getStockDataFromMovie(
            articleIdList, countryCode, salesLine, storeNo);

    CompletableFuture<List<ArticleStockLocations>> articleStockLocationList =
        stockLocationRepository.getArticleStockLocation(
            articleIdList, countryCode, storeNo, dataTypeList);

    CompletableFuture.allOf(
        articleComponentForArticleId,
        articleMasterId,
        articleStoreMasterFromAcl,
        stockDataFromMovie,
        articleStockLocationList);

    Map<ArticleId, RawArticleStoreMasterData> rawArticleStoreMasterDataMap =
        rawArticleStoreMasterDataRepository.getArticleIdRawArticleStoreMasterDataMap(
            articleStoreMasterFromAcl.get(), articleMasterId.get());

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
        articleMasterId.get().entrySet().stream()
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    entry ->
                        Optional.ofNullable(
                                buildProductLifeCycleData(entry.getValue().getArticleMasterData()))
                            .orElse(new ProductLifeCycleData())));

    for (RawArticleData rawArticleData : rawArticleDataList) {
      Optional<ArticleMasterId> articleMasterIdOpt =
          Optional.ofNullable(articleMasterId.get().get(rawArticleData.getArticleId()));

      rawArticleData
          .setArticleMasterData(
              articleMasterIdOpt.map(ArticleMasterId::getArticleMasterData).orElse(null))
          .setArticleStoreMasterData(
              rawArticleStoreMasterDataMap.getOrDefault(
                  rawArticleData.getArticleId(), RawArticleStoreMasterData.builder().build()))
          .setStockDetails(
              Optional.ofNullable(stockDataFromMovieMap.get(rawArticleData.getArticleId()))
                  .map(StockData::getArticleStockData)
                  .orElse(null))
          .setPlannedDeliveryDates(
              Optional.ofNullable(stockDataFromMovieMap.get(rawArticleData.getArticleId()))
                  .map(StockData::getArticlePlannedDeliveryDates)
                  .orElse(Collections.emptyList()))
          .setStockLocations(
              Optional.ofNullable(articleStockLocationListMap.get(rawArticleData.getArticleId()))
                  .map(ArticleStockLocations::getStockLocations)
                  .orElse(Collections.emptyList()))
          .setArticleComponent(
              articleComponentForArticleId.get().get(rawArticleData.getArticleId()))
          .setProductLifeCycleData(
              productLifeCycleMap.getOrDefault(
                  rawArticleData.getArticleId(), new ProductLifeCycleData()))
          .setMultiLanguageData(
              articleMasterIdOpt.map(ArticleMasterId::getMultiLanguageData).orElse(null));
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
