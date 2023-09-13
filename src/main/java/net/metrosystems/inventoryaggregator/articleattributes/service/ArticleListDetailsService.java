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
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleComponent;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.DataType;
import net.metrosystems.inventoryaggregator.articleattributes.model.ProductLifeCycleData;
import net.metrosystems.inventoryaggregator.articleattributes.model.PromotionArticleDataForArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawArticleData;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawArticleStoreMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawIsTradeItemExcludedFromDiscounts;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.ArticleIdStockLocation;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.ArticleLocationForArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.StockData;
import net.metrosystems.inventoryaggregator.articleattributes.repository.AclBundleStockLocationRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.ArticleComponentRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.ArticleDataFromMovieRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.ArticleMasterDataRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.RawArticleStoreMasterDataRepository;
import net.metrosystems.inventoryaggregator.articleattributes.v2.service.RawArticleDataListService;
import net.metrosystems.inventoryaggregator.articlestoremasterdata.model.ArticleStoreMasterData;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.articleCacheStoreExceptions.model.ArticleCacheStoreExceptionsIhdsTree;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard.IduItemDerivedStandard;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard.IsTradeItemExcludedFromDiscounts;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.service.ArticleCacheService;
import net.metrosystems.inventoryaggregator.common.model.ArticleNotFoundException;
import net.metrosystems.inventoryaggregator.common.model.InterruptedRuntimeException;
import net.metrosystems.inventoryaggregator.common.model.StoreId;
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
  private final ArticleCacheService articleCacheService;

  public List<RawArticleData> getArticleDataListByArticleVariantBundle(
      List<ArticleId> articleIdList,
      String countryCode,
      String salesLine,
      String languageCode,
      Integer storeNo,
      Optional<List<String>> dataTypeList)
      throws Exception {
    return getRawArticleDataListForArticleVariantBundleIdentifierList(
        articleIdList, countryCode, salesLine, languageCode, storeNo, dataTypeList);
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
        getArticleComponentForArticleId(
            articleIdList, countryCode, salesLine, languageCode, dataTypeList);

    CompletableFuture<Map<ArticleId, ArticleMasterData>> articleMasterDataForArticleId =
        getArticleMasterDataForArticleId(
            articleIdList, countryCode, salesLine, languageCode, storeNo, dataTypeList);

    CompletableFuture<Map<ArticleId, List<IduItemDerivedStandard>>> iduItemDerivedStandards =
        getIduItemDerivedStandards(articleIdList, countryCode, dataTypeList);

    CompletableFuture<Map<ArticleId, ArticleStoreMasterData>> articleStoreMasterFromAcl =
        getArticleStoreMasterFromAcl(articleIdList, countryCode, salesLine, storeNo, dataTypeList);

    CompletableFuture<Map<ArticleId, ArticleCacheStoreExceptionsIhdsTree>>
        articleStoreExceptionsFromAC =
            getArticleStoreExceptionFromAC(
                articleIdList, countryCode, salesLine, storeNo, dataTypeList);

    CompletableFuture<List<StockData>> stockDataFromMovie =
        getStockDataFromMovie(articleIdList, countryCode, salesLine, storeNo, dataTypeList);

    CompletableFuture<List<ArticleIdStockLocation>> articleStockLocationList =
        getArticleStockLocationList(articleIdList, countryCode, salesLine, storeNo, dataTypeList);

    CompletableFuture<List<ArticleLocationForArticleId>> articleLocationList =
        getArticleLocationForArticleId(
            articleIdList, countryCode, salesLine, storeNo, dataTypeList);

    CompletableFuture.allOf(
        articleComponentForArticleId,
        articleMasterDataForArticleId,
        articleStoreMasterFromAcl,
        articleStoreExceptionsFromAC,
        stockDataFromMovie,
        articleStockLocationList,
        articleLocationList,
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

  private CompletableFuture<List<ArticleLocationForArticleId>> getArticleLocationForArticleId(
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

  private CompletableFuture<List<ArticleIdStockLocation>> getArticleStockLocationList(
      List<ArticleId> articleIdList,
      String countryCode,
      String salesLine,
      Integer storeNo,
      Optional<List<String>> dataTypeList) {
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
    return articleStockLocationList;
  }

  private CompletableFuture<List<StockData>> getStockDataFromMovie(
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

  private CompletableFuture<Map<ArticleId, ArticleCacheStoreExceptionsIhdsTree>>
      getArticleStoreExceptionFromAC(
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
        Thread.currentThread().interrupt();
        log.error(
            "ArticleListDetailsService.buildRawArticleDataList - articleStoreExceptionsFromAC - error: {}",
            e.getMessage(),
            e);
      }
    }
    return articleStoreExceptionsFromAC;
  }

  private CompletableFuture<Map<ArticleId, ArticleStoreMasterData>> getArticleStoreMasterFromAcl(
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

  private CompletableFuture<Map<ArticleId, List<IduItemDerivedStandard>>>
      getIduItemDerivedStandards(
          List<ArticleId> articleIdList, String countryCode, Optional<List<String>> dataTypeList) {
    CompletableFuture<Map<ArticleId, List<IduItemDerivedStandard>>> iduItemDerivedStandards =
        CompletableFuture.completedFuture(Collections.emptyMap());
    if (dataTypeList.isEmpty()
        || dataTypeList.get().contains(DataType.IDU_ITEM_DERIVED_STANDARDS.getDataType())) {
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
    }
    return iduItemDerivedStandards;
  }

  private CompletableFuture<Map<ArticleId, ArticleMasterData>> getArticleMasterDataForArticleId(
      List<ArticleId> articleIdList,
      String countryCode,
      String salesLine,
      String languageCode,
      Integer storeNo,
      Optional<List<String>> dataTypeList) {
    CompletableFuture<Map<ArticleId, ArticleMasterData>> articleMasterDataForArticleId =
        CompletableFuture.completedFuture(Collections.emptyMap());
    if (dataTypeList.isEmpty()
        || dataTypeList.get().contains(DataType.ARTICLE_MASTER_DATA.getDataType())) {
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
    }
    return articleMasterDataForArticleId;
  }

  private CompletableFuture<Map<ArticleId, ArticleComponent>> getArticleComponentForArticleId(
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

  public List<PromotionArticleDataForArticleId> getFuturePromotionsForArticleIds(
      List<ArticleId> articleIds, StoreId storeId) {
    if (articleIds == null || articleIds.isEmpty()) {
      return Collections.emptyList();
    }
    try {
      return articleMasterDataRepository
          .getPromotionDataArticlesForArticleIds(
              articleIds, storeId.getCountryCode(), storeId.getStoreNumber())
          .get();
    } catch (InterruptedException | ExecutionException e) {
      log.error("Could not getFuturePromotionsForArticleIds={}", e.getMessage(), e);
      Thread.currentThread().interrupt();
      throw new InterruptedRuntimeException("Could not getFuturePromotionsForArticleIds");
    }
  }
}
