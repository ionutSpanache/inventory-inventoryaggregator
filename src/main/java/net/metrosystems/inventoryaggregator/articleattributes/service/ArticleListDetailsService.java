package net.metrosystems.inventoryaggregator.articleattributes.service;

import java.util.ArrayList;
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
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.ProductLifeCycleData;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawArticleData;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawArticleStoreMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawIsTradeItemExcludedFromDiscounts;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.StockData;
import net.metrosystems.inventoryaggregator.articleattributes.model.articlecomponent.ArticleComponent;
import net.metrosystems.inventoryaggregator.articleattributes.repository.ArticleMasterDataRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.RawArticleStoreMasterDataRepository;
import net.metrosystems.inventoryaggregator.articleattributes.v2.service.ArticleProcessingServiceV2;
import net.metrosystems.inventoryaggregator.articlestoremasterdata.model.ArticleStoreMasterData;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.articleCacheStoreExceptions.model.ArticleCacheStoreExceptionsIhdsTree;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard.IduItemDerivedStandard;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard.IsTradeItemExcludedFromDiscounts;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.service.ArticleCacheService;
import net.metrosystems.inventoryaggregator.common.model.ArticleNotFoundException;
import net.metrosystems.inventoryaggregator.common.model.StoreId;
import net.metrosystems.inventoryaggregator.common.movieacl.MovieAclService;
import net.metrosystems.inventoryaggregator.stocklocation.model.ArticleStockLocations;
import net.metrosystems.inventoryaggregator.stocklocation.repository.StockLocationRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleListDetailsService {

  private final RawArticleStoreMasterDataRepository rawArticleStoreMasterDataRepository;
  private final ArticleMasterDataRepository articleMasterDataRepository;
  private final ArticleCacheService articleCacheService;
  private final StockLocationRepository stockLocationRepository;
  private final ArticleProcessingServiceV2 articleProcessingServiceV2;
  private final MovieAclService movieAclService;

  public List<RawArticleData> getArticleDataListByArticleVariantBundle(
      List<ArticleId> articleIdList,
      StoreId storeId,
      String languageCode,
      Optional<List<String>> dataTypeList)
      throws Exception {
    return getRawArticleDataListForArticleVariantBundleIdentifierList(
        articleIdList,
        storeId.getCountryCode(),
        storeId.getSalesLine(),
        languageCode,
        storeId.getStoreNumber(),
        dataTypeList);
  }

  public List<RawIsTradeItemExcludedFromDiscounts> isTradeItemExcludedFromDiscounts(
      List<ArticleId> articleIdList, String countryCode, String salesLine) {
    return articleIdList.stream()
        .map(
            articleId -> {
              List<String> articleKeys =
                  articleCacheService.getArticleKeysForDerivedStandardDocument(
                      countryCode,
                      articleId.getArticleNumber(),
                      articleId.getBundleNumber(),
                      articleId.getVariantNumber(),
                      salesLine);

              IsTradeItemExcludedFromDiscounts isExcluded =
                  articleCacheService.isTradeItemExcludedFromDiscounts(articleKeys);
              return RawIsTradeItemExcludedFromDiscounts.builder()
                  .value(isExcluded.getValue())
                  .validFrom(isExcluded.getValidFrom())
                  .articleNumber(articleId.getArticleNumber())
                  .bundleNumber(articleId.getBundleNumber())
                  .variantNumber(articleId.getVariantNumber())
                  .build();
            })
        .toList();
  }

  private List<RawArticleData> getRawArticleDataListForArticleVariantBundleIdentifierList(
      List<ArticleId> articleIdList,
      String countryCode,
      String salesLine,
      String languageCode,
      Integer storeNo,
      Optional<List<String>> dataTypeList)
      throws Exception {
    List<RawArticleData> rawArticleDataList = new ArrayList<>();

    articleIdList.forEach(
        articleId -> {
          try {
            rawArticleDataList.add(RawArticleData.builder().articleId(articleId).build());
          } catch (Exception e) {
            log.error(
                "ArticleListDetailsService.getRawArticleDataListForArticleVariantBundleIdentifierList - articleIdList - error: {}",
                e.getMessage(),
                e);
          }
        });
    buildRawArticleDataList(
        articleIdList,
        countryCode,
        salesLine,
        languageCode,
        storeNo,
        rawArticleDataList,
        dataTypeList);
    return rawArticleDataList;
  }

  private void buildRawArticleDataList(
      List<ArticleId> articleIdList,
      String countryCode,
      String salesLine,
      String languageCode,
      Integer storeNo,
      List<RawArticleData> rawArticleDataList,
      Optional<List<String>> dataTypeList)
      throws ExecutionException, InterruptedException {

    CompletableFuture<Map<ArticleId, ArticleComponent>> articleComponentForArticleId =
        articleProcessingServiceV2.getArticleComponents(countryCode, storeNo, articleIdList);

    CompletableFuture<Map<ArticleId, ArticleMasterData>> articleMasterDataForArticleId =
        getArticleMasterDataForArticleId(
            articleIdList, countryCode, salesLine, languageCode, storeNo);

    CompletableFuture<Map<ArticleId, List<IduItemDerivedStandard>>> iduItemDerivedStandards =
        getIduItemDerivedStandards(articleIdList, countryCode);

    CompletableFuture<Map<ArticleId, ArticleStoreMasterData>> articleStoreMasterFromAcl =
        getArticleStoreMasterFromAcl(articleIdList, countryCode, salesLine, storeNo);

    CompletableFuture<Map<ArticleId, ArticleCacheStoreExceptionsIhdsTree>>
        articleStoreExceptionsFromAC =
            getArticleStoreExceptionFromAC(articleIdList, countryCode, salesLine, storeNo);

    CompletableFuture<List<StockData>> stockDataFromMovie =
        getStockDataFromMovie(articleIdList, countryCode, salesLine, storeNo);

    CompletableFuture<List<ArticleStockLocations>> articleStockLocationList =
        stockLocationRepository.getArticleStockLocation(
            articleIdList, countryCode, salesLine, storeNo, dataTypeList);

    CompletableFuture.allOf(
        articleComponentForArticleId,
        articleMasterDataForArticleId,
        articleStoreMasterFromAcl,
        articleStoreExceptionsFromAC,
        stockDataFromMovie,
        articleStockLocationList,
        iduItemDerivedStandards);

    for (Map.Entry<ArticleId, ArticleMasterData> entry :
        articleMasterDataForArticleId.get().entrySet()) {
      articleCacheService.enhanceArticleMasterData(
          entry.getValue(), iduItemDerivedStandards.get().get(entry.getKey()));
    }

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
        iduItemDerivedStandards.get().entrySet().stream()
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    entry ->
                        Optional.ofNullable(
                                articleCacheService.buildProductLifeCycleData(entry.getValue()))
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

  private CompletableFuture<List<StockData>> getStockDataFromMovie(
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

  private CompletableFuture<Map<ArticleId, ArticleCacheStoreExceptionsIhdsTree>>
      getArticleStoreExceptionFromAC(
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
      Thread.currentThread().interrupt();
      log.error(
          "ArticleListDetailsService.buildRawArticleDataList - articleStoreExceptionsFromAC - error: {}",
          e.getMessage(),
          e);
    }
    return articleStoreExceptionsFromAC;
  }

  private CompletableFuture<Map<ArticleId, ArticleStoreMasterData>> getArticleStoreMasterFromAcl(
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

  private CompletableFuture<Map<ArticleId, List<IduItemDerivedStandard>>>
      getIduItemDerivedStandards(List<ArticleId> articleIdList, String countryCode) {
    CompletableFuture<Map<ArticleId, List<IduItemDerivedStandard>>> iduItemDerivedStandards =
        CompletableFuture.completedFuture(Collections.emptyMap());
    try {
      iduItemDerivedStandards =
          articleMasterDataRepository.getIduItemDerivedStandards(articleIdList, countryCode);
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      log.error(
          "ArticleListDetailsService.buildRawArticleDataList - getIduItemDerivedStandards - error: {}",
          e.getMessage(),
          e);
    }
    return iduItemDerivedStandards;
  }

  private CompletableFuture<Map<ArticleId, ArticleMasterData>> getArticleMasterDataForArticleId(
      List<ArticleId> articleIdList,
      String countryCode,
      String salesLine,
      String languageCode,
      Integer storeNo) {
    CompletableFuture<Map<ArticleId, ArticleMasterData>> articleMasterDataForArticleId =
        CompletableFuture.completedFuture(Collections.emptyMap());
    try {
      articleMasterDataForArticleId =
          articleMasterDataRepository.getArticleMasterDataForArticleIdList(
              articleIdList, countryCode, salesLine, languageCode, storeNo);
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
