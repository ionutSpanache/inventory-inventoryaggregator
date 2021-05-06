package net.metrosystems.inventoryaggregator.articleattributes.service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawArticleData;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.ArticleComponentForArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.ArticleIdStockLocation;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.ArticleLocationForArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.ArticleMasterDataForArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.RawArticleStoreMasterDataForArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.StockData;
import net.metrosystems.inventoryaggregator.articleattributes.repository.AclBundleStockLocationRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.ArticleComponentRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.ArticleDataFromMovieRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.ArticleMasterDataRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.RawArticleStoreMasterDataRepository;
import net.metrosystems.inventoryaggregator.common.model.ArticleNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleListDetailsService {

  private final RawArticleStoreMasterDataRepository rawArticleStoreMasterDataRepository;
  private final ArticleMasterDataRepository articleMasterDataRepository;
  private final ArticleDataFromMovieRepository articleDataFromMovieRepository;
  private final AclBundleStockLocationRepository aclBundleStockLocationRepository;
  private final ArticleComponentRepository articleComponentRepository;

  public List<RawArticleData> getArticleDataListByArticleVariantBundle(
      List<ArticleId> articleIdList, String countryCode, String salesLine, String languageCode,
      Integer storeNo) throws Exception {
    return getRawArticleDataListForArticleVariantBundleIdentifierList(articleIdList, countryCode,
        salesLine, languageCode, storeNo);
  }

  private List<RawArticleData> getRawArticleDataListForArticleVariantBundleIdentifierList(
      List<ArticleId> articleIdList, String countryCode, String salesLine, String languageCode,
      Integer storeNo) throws Exception {
    List<RawArticleData> rawArticleDataList = new ArrayList<>();

    articleIdList.forEach(articleId -> {
      try {
        rawArticleDataList.add(RawArticleData.builder().articleId(articleId).build());
      } catch (Exception e) {
        log.error(
            "ArticleListDetailsService.getRawArticleDataListForArticleVariantBundleIdentifierList - articleIdList - error: {}",
            e.getMessage(), e);
      }
    });

    buildRawArticleDataList(articleIdList, countryCode, salesLine, languageCode, storeNo,
        rawArticleDataList);
    return rawArticleDataList;
  }

  private void buildRawArticleDataList(List<ArticleId> articleIdList, String countryCode,
      String salesLine, String languageCode, Integer storeNo,
      List<RawArticleData> rawArticleDataList) throws ExecutionException, InterruptedException {

    CompletableFuture<List<ArticleComponentForArticleId>> articleComponentForArticleId = new CompletableFuture<>();
    try {
      articleComponentForArticleId = articleComponentRepository
          .getArticleComponentForArticleIdList(articleIdList, countryCode, salesLine, languageCode);
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      log.error(
          "ArticleListDetailsService.buildRawArticleDataList - articleComponentForArticleId - error: {}",
          e.getMessage(), e);
    }

    CompletableFuture<List<ArticleMasterDataForArticleId>> articleMasterDataForArticleId = new CompletableFuture<>();
    try {
      articleMasterDataForArticleId = articleMasterDataRepository
          .getArticleMasterDataForArticleIdList(articleIdList, countryCode, salesLine, languageCode,
              storeNo);
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      log.error(
          "ArticleListDetailsService.buildRawArticleDataList - articleMasterDataForArticleId - error: {}",
          e.getMessage(), e);
    }

    CompletableFuture<List<RawArticleStoreMasterDataForArticleId>> rawArticleStoreMasterData = new CompletableFuture<>();
    try {
      rawArticleStoreMasterData = rawArticleStoreMasterDataRepository
          .getRawArticleStoreMasterDataForArticleIdList(articleIdList, countryCode, salesLine,
              languageCode, storeNo);

    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      log.error(
          "ArticleListDetailsService.buildRawArticleDataList - rawArticleStoreMasterDataRepository - error: {}",
          e.getMessage(), e);
    }

    CompletableFuture<List<StockData>> stockDataFromMovie = new CompletableFuture<>();
    try {
      stockDataFromMovie = articleDataFromMovieRepository
          .getArticleListDataFromMovie(articleIdList, countryCode, salesLine, storeNo);

    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      log.error(
          "ArticleListDetailsService.buildRawArticleDataList - articleDataFromMovieRepository - error: {}",
          e.getMessage(), e);
    }

    CompletableFuture<List<ArticleIdStockLocation>> articleStockLocationList = new CompletableFuture<>();
    try {
      articleStockLocationList = aclBundleStockLocationRepository
          .getStockLocationsForArticleList(articleIdList, countryCode, salesLine, storeNo);
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      log.error(
          "ArticleListDetailsService.articleStockLocationList - articleDataFromMovieRepository - error: {}",
          e.getMessage(), e);
    }

    CompletableFuture<List<ArticleLocationForArticleId>> articleLocationList = new CompletableFuture<>();
    try {
      articleLocationList = aclBundleStockLocationRepository
          .getArticleLocationForArticleList(countryCode, salesLine, storeNo, articleIdList);
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      log.error(
          "ArticleListDetailsService.articleStockLocationList - articleLocationList - error: {}",
          e.getMessage(), e);
    }

    CompletableFuture.allOf(articleComponentForArticleId, articleMasterDataForArticleId,
        rawArticleStoreMasterData, stockDataFromMovie, articleStockLocationList,
        articleLocationList);

    Map<ArticleId, ArticleComponentForArticleId> articleComponentForArticleIdMap = articleComponentForArticleId
        .get().stream().collect(Collectors.toMap(ArticleComponentForArticleId::getArticleId,
            articleComponent -> articleComponent));
    List<ArticleMasterDataForArticleId> result = articleMasterDataForArticleId.get();
    Map<ArticleId, ArticleMasterDataForArticleId> articleMasterDataForArticleIdMap = result != null ?
    result.stream().collect(Collectors.toMap(ArticleMasterDataForArticleId::getArticleId,
            articleMasterData -> articleMasterData)) : Collections.emptyMap();
    Map<ArticleId, RawArticleStoreMasterDataForArticleId> rawArticleStoreMasterDataMap = rawArticleStoreMasterData
        .get().stream().collect(Collectors
            .toMap(RawArticleStoreMasterDataForArticleId::getArticleId,
                rawArticleStoreMasterDataForArticleId -> rawArticleStoreMasterDataForArticleId));
    Map<ArticleId, StockData> stockDataFromMovieMap = stockDataFromMovie.get().stream().collect(
        Collectors.toMap(StockData::getArticleId, stockData -> stockData));
    Map<ArticleId, ArticleIdStockLocation> articleStockLocationListMap = articleStockLocationList
        .get().stream().collect(Collectors.toMap(ArticleIdStockLocation::getArticleId,
            articleIdStockLocation -> articleIdStockLocation));
    Map<ArticleId, ArticleLocationForArticleId> articleLocationListMap = articleLocationList.get()
                                                                                            .stream()
                                                                                            .collect(
                                                                                                Collectors
                                                                                                    .toMap(
                                                                                                        ArticleLocationForArticleId::getArticleId,
                                                                                                        articleLocationForArticleId -> articleLocationForArticleId));

    rawArticleDataList.forEach(rawArticleData -> {
      rawArticleData.setArticleMasterData(
          Optional.ofNullable(articleMasterDataForArticleIdMap.get(rawArticleData.getArticleId()))
                  .map(ArticleMasterDataForArticleId::getArticleMasterData).orElse(null));
      rawArticleData.setArticleStoreMasterData(
          Optional.ofNullable(rawArticleStoreMasterDataMap.get(rawArticleData.getArticleId()))
                  .map(RawArticleStoreMasterDataForArticleId::getRawArticleStoreMasterData)
                  .orElse(null));
      rawArticleData.setStockDetails(
          Optional.ofNullable(stockDataFromMovieMap.get(rawArticleData.getArticleId()))
                  .map(StockData::getArticleStockData).orElse(null));
      rawArticleData.setPlannedDeliveryDates(
          Optional.ofNullable(stockDataFromMovieMap.get(rawArticleData.getArticleId()))
                  .map(StockData::getArticlePlannedDeliveryDates).orElse(Collections.emptyList()));
      rawArticleData.setStockLocations(
          Optional.ofNullable(articleStockLocationListMap.get(rawArticleData.getArticleId()))
                  .map(ArticleIdStockLocation::getArticleStockLocationList)
                  .orElse(Collections.emptyList()));
      rawArticleData.setArticleLocation(
          Optional.ofNullable(articleLocationListMap.get(rawArticleData.getArticleId()))
                  .map(ArticleLocationForArticleId::getArticleLocation).orElse(null));
      rawArticleData.setArticleComponent(
          Optional.ofNullable(articleComponentForArticleIdMap.get(rawArticleData.getArticleId()))
                  .map(ArticleComponentForArticleId::getArticleComponent).orElse(null));
    });

  }
}
