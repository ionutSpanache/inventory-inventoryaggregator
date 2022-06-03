package net.metrosystems.inventoryaggregator.articleattributes.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.*;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.ArticleIdStockLocation;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.ArticleLocationForArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.StockData;
import net.metrosystems.inventoryaggregator.articleattributes.repository.*;
import net.metrosystems.inventoryaggregator.articlestoremasterdata.model.ArticleStoreMasterData;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.articleCacheStoreExceptions.model.ArticleCacheStoreExceptionsIhdsTree;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard.IduItemDerivedStandard;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard.IsTradeItemExcludedFromDiscounts;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.service.ArticleCacheService;
import net.metrosystems.inventoryaggregator.common.model.ArticleNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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
      Integer storeNo)
      throws Exception {
    return getRawArticleDataListForArticleVariantBundleIdentifierList(
        articleIdList, countryCode, salesLine, languageCode, storeNo);
  }

  public List<RawIsTradeItemExcludedFromDiscounts> isTradeItemExcludedFromDiscounts(
      List<ArticleId> articleIdList, String countryCode, String salesLine) {
    return articleIdList.stream()
        .map(
            articleId -> {
              List<String> articleKeys = articleCacheService.getArticleKeysForDerivedStandardDocument(countryCode,
                      articleId.getArticleNumber(), articleId.getBundleNumber(), articleId.getVariantNumber(), salesLine);

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
        .collect(Collectors.toList());
  }

  private List<RawArticleData> getRawArticleDataListForArticleVariantBundleIdentifierList(
      List<ArticleId> articleIdList,
      String countryCode,
      String salesLine,
      String languageCode,
      Integer storeNo)
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
        articleIdList, countryCode, salesLine, languageCode, storeNo, rawArticleDataList);
    return rawArticleDataList;
  }

  private void buildRawArticleDataList(
      List<ArticleId> articleIdList,
      String countryCode,
      String salesLine,
      String languageCode,
      Integer storeNo,
      List<RawArticleData> rawArticleDataList)
      throws ExecutionException, InterruptedException {

    CompletableFuture<Map<ArticleId, ArticleComponent>> articleComponentForArticleId =
        new CompletableFuture<>();
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

    CompletableFuture<Map<ArticleId, ArticleMasterData>> articleMasterDataForArticleId =
        new CompletableFuture<>();
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

    CompletableFuture<Map<ArticleId, List<IduItemDerivedStandard>>>  iduItemDerivedStandards =
            new CompletableFuture<>();
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

    CompletableFuture<Map<ArticleId, ArticleStoreMasterData>> articleStoreMasterFromAcl =
            new CompletableFuture<>();
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

    CompletableFuture<Map<ArticleId, ArticleCacheStoreExceptionsIhdsTree>> articleStoreExceptionsFromAC =
            new CompletableFuture<>();
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
    }

    CompletableFuture<List<StockData>> stockDataFromMovie = new CompletableFuture<>();
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

    CompletableFuture<List<ArticleIdStockLocation>> articleStockLocationList =
        new CompletableFuture<>();
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

    CompletableFuture<List<ArticleLocationForArticleId>> articleLocationList =
        new CompletableFuture<>();
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

    //    CompletableFuture<List<PromotionArticleDataForArticleId>>  promotionArticles =
    //            new CompletableFuture<>();
    //    try {
    //      promotionArticles =
    //              articleMasterDataRepository.getPromotionDataArticlesForArticleIds(
    //                      articleIdList, countryCode, storeNo);
    //    } catch (ArticleNotFoundException ex) {
    //      throw new ArticleNotFoundException();
    //    } catch (Exception e) {
    //      log.error(
    //              "ArticleListDetailsService.getProductLifeCycleData - getProductLyfeCycleData -
    // error: {}",
    //              e.getMessage(),
    //              e);
    //    }

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

//    Map<ArticleId, PromotionArticleData> promotionArticleDataMap =
//            promotionArticles.get().stream().filter(promotionArticleDataForArticleId ->
//                            (promotionArticleDataForArticleId.getPromotionArticleData().getIsActive() != null &&
//                                    promotionArticleDataForArticleId.getPromotionArticleData().getIsActive()))
//                    .collect(
//                            Collectors.toMap(
//                                    PromotionArticleDataForArticleId::getArticleId,
//                                    articleComponent -> articleComponent.getPromotionArticleData().getPromotionNetSalesPricePerBundle() == null ?
//                                            PromotionArticleData.builder().build() : articleComponent.getPromotionArticleData()));


//    rawArticleStoreMasterDataMap.entrySet().stream().forEach(entry -> {
//      PromotionArticleData promotionArticleData = promotionArticleDataMap.get(entry.getKey());
//
//      if (promotionArticleData != null && promotionArticleData.getPromotionNetSalesPricePerBundle() != null) {
//        entry.getValue().getRawArticleStoreMasterData().setStorePromotionBundleNetPrice(promotionArticleData.getPromotionNetSalesPricePerBundle());
//      }
//    });

    for (RawArticleData rawArticleData : rawArticleDataList) {
          rawArticleData.setArticleMasterData(
                  articleMasterDataForArticleId.get().get(rawArticleData.getArticleId()));
          rawArticleData.setArticleStoreMasterData(
                  rawArticleStoreMasterDataMap
                          .getOrDefault(rawArticleData.getArticleId(), RawArticleStoreMasterData.builder().build()));
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
                  productLifeCycleMap.getOrDefault(rawArticleData.getArticleId(), new ProductLifeCycleData()));

        }
  }
}
