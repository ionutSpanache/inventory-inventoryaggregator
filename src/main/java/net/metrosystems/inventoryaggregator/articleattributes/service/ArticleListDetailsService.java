package net.metrosystems.inventoryaggregator.articleattributes.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
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

  private final Executor executor = Executors.newFixedThreadPool(8);

  private final RawArticleStoreMasterDataRepository rawArticleStoreMasterDataRepository;
  private final ArticleMasterDataRepository articleMasterDataRepository;
  private final ArticleDataFromMovieRepository articleDataFromMovieRepository;
  private final AclBundleStockLocationRepository aclBundleStockLocationRepository;
  private final ArticleComponentRepository articleComponentRepository;

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
            e.printStackTrace();
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

    AtomicReference<Long> articleComponentDurationAC = new AtomicReference<>(0L);
    AtomicReference<Long> articleMasterDataDurationAC = new AtomicReference<>(0L);
    AtomicReference<Long> rawArticleStoreMasterDataDurationAC = new AtomicReference<>(0L);
    AtomicReference<Long> articleDataFromMovieDuration = new AtomicReference<>(0L);
    AtomicReference<Long> articleStockLocationListDurationACL = new AtomicReference<>(0L);
    AtomicReference<Long> articleLocationListDurationACL = new AtomicReference<>(0L);
    CompletableFuture<List<ArticleComponentForArticleId>> articleComponentForArticleId =
        CompletableFuture.supplyAsync(
            () -> {
              try {
                long startTimeArticleComponent = System.currentTimeMillis();
                var articleMasterDataResult =
                    articleComponentRepository
                        .getArticleComponentForArticleIdList(
                            articleIdList, countryCode, salesLine, languageCode)
                        .get(20, TimeUnit.SECONDS);
                articleComponentDurationAC.set(
                    System.currentTimeMillis() - startTimeArticleComponent);
                return articleMasterDataResult;
              } catch (ArticleNotFoundException ex) {
                throw new ArticleNotFoundException();
              } catch (Exception e) {
                e.printStackTrace();
              }
              return null;
            },
            executor);

    CompletableFuture<List<ArticleMasterDataForArticleId>> articleMasterDataForArticleId =
        CompletableFuture.supplyAsync(
            () -> {
              try {
                long startTimeArticleMasterData = System.currentTimeMillis();
                var articleMasterDataResult =
                    articleMasterDataRepository
                        .getArticleMasterDataForArticleIdList(
                            articleIdList, countryCode, salesLine, languageCode, storeNo)
                        .get(20, TimeUnit.SECONDS);
                articleMasterDataDurationAC.set(
                    System.currentTimeMillis() - startTimeArticleMasterData);
                return articleMasterDataResult;
              } catch (ArticleNotFoundException ex) {
                throw new ArticleNotFoundException();
              } catch (Exception e) {
                e.printStackTrace();
              }
              return null;
            },
            executor);

    CompletableFuture<List<RawArticleStoreMasterDataForArticleId>> rawArticleStoreMasterData =
        CompletableFuture.supplyAsync(
            () -> {
              try {
                long startTimeRawArticleStoreMasterData = System.currentTimeMillis();
                var rawArticleStoreMasterDataResult =
                    rawArticleStoreMasterDataRepository
                        .getRawArticleStoreMasterDataForArticleIdList(
                            articleIdList, countryCode, salesLine, languageCode, storeNo)
                        .get(20, TimeUnit.SECONDS);
                rawArticleStoreMasterDataDurationAC.set(
                    System.currentTimeMillis() - startTimeRawArticleStoreMasterData);
                return rawArticleStoreMasterDataResult;
              } catch (ArticleNotFoundException ex) {
                throw new ArticleNotFoundException();
              } catch (Exception e) {
                e.printStackTrace();
              }
              return null;
            },
            executor);

    CompletableFuture<List<StockData>> stockDataFromMovie =
        CompletableFuture.supplyAsync(
            () -> {
              try {
                long startTimeArticleDataFromMovie = System.currentTimeMillis();
                var articleDataFromMovieResult =
                    articleDataFromMovieRepository
                        .getArticleListDataFromMovie(articleIdList, countryCode, salesLine, storeNo)
                        .get(20, TimeUnit.SECONDS);
                articleDataFromMovieDuration.set(
                    System.currentTimeMillis() - startTimeArticleDataFromMovie);
                return articleDataFromMovieResult;
              } catch (ArticleNotFoundException ex) {
                throw new ArticleNotFoundException();
              } catch (Exception e) {
                e.printStackTrace();
              }
              return null;
            },
            executor);

    CompletableFuture<List<ArticleIdStockLocation>> articleStockLocationList =
        CompletableFuture.supplyAsync(
            () -> {
              try {
                long startTimeArticleStockLocationList = System.currentTimeMillis();
                var articleStockLocationListResult =
                    aclBundleStockLocationRepository
                        .getStockLocationsForArticleList(
                            articleIdList, countryCode, salesLine, storeNo)
                        .get(20, TimeUnit.SECONDS);
                articleStockLocationListDurationACL.set(
                    System.currentTimeMillis() - startTimeArticleStockLocationList);
                return articleStockLocationListResult;
              } catch (ArticleNotFoundException ex) {
                throw new ArticleNotFoundException();
              } catch (Exception e) {
                e.printStackTrace();
              }
              return null;
            },
            executor);

    CompletableFuture<List<ArticleLocationForArticleId>> articleLocationList =
        CompletableFuture.supplyAsync(
            () -> {
              try {
                long startTimeArticleLocationList = System.currentTimeMillis();
                var articleLocationListResult =
                    aclBundleStockLocationRepository
                        .getArticleLocationForArticleList(
                            countryCode, salesLine, storeNo, articleIdList)
                        .get(20, TimeUnit.SECONDS);
                articleLocationListDurationACL.set(
                    System.currentTimeMillis() - startTimeArticleLocationList);
                return articleLocationListResult;
              } catch (ArticleNotFoundException ex) {
                throw new ArticleNotFoundException();
              } catch (Exception e) {
                e.printStackTrace();
              }
              return null;
            },
            executor);

    CompletableFuture.allOf(
        articleComponentForArticleId,
        articleMasterDataForArticleId,
        rawArticleStoreMasterData,
        stockDataFromMovie,
        articleStockLocationList,
        articleLocationList);

    Map<ArticleId, ArticleComponentForArticleId> articleComponentForArticleIdMap =
        articleComponentForArticleId.get().stream()
            .collect(
                Collectors.toMap(
                    ArticleComponentForArticleId::getArticleId,
                    articleComponent -> articleComponent));
    Map<ArticleId, ArticleMasterDataForArticleId> articleMasterDataForArticleIdMap =
        articleMasterDataForArticleId.get().stream()
            .collect(
                Collectors.toMap(
                    ArticleMasterDataForArticleId::getArticleId,
                    articleMasterData -> articleMasterData));
    Map<ArticleId, RawArticleStoreMasterDataForArticleId> rawArticleStoreMasterDataMap =
        rawArticleStoreMasterData.get().stream()
            .collect(
                Collectors.toMap(
                    RawArticleStoreMasterDataForArticleId::getArticleId,
                    rawArticleStoreMasterDataForArticleId ->
                        rawArticleStoreMasterDataForArticleId));
    Map<ArticleId, StockData> stockDataFromMovieMap =
        stockDataFromMovie.get().stream()
            .collect(Collectors.toMap(StockData::getArticleId, stockData -> stockData));
    Map<ArticleId, ArticleIdStockLocation> articleStockLocationListMap =
        articleStockLocationList.get().stream()
            .collect(
                Collectors.toMap(
                    ArticleIdStockLocation::getArticleId,
                    articleIdStockLocation -> articleIdStockLocation));
    Map<ArticleId, ArticleLocationForArticleId> articleLocationListMap =
        articleLocationList.get().stream()
            .collect(
                Collectors.toMap(
                    ArticleLocationForArticleId::getArticleId,
                    articleLocationForArticleId -> articleLocationForArticleId));

    rawArticleDataList.forEach(
        rawArticleData -> {
          rawArticleData.setArticleMasterData(
              Optional.ofNullable(
                      articleMasterDataForArticleIdMap.get(rawArticleData.getArticleId()))
                  .map(ArticleMasterDataForArticleId::getArticleMasterData)
                  .orElse(null));
          rawArticleData.setArticleStoreMasterData(
              Optional.ofNullable(rawArticleStoreMasterDataMap.get(rawArticleData.getArticleId()))
                  .map(RawArticleStoreMasterDataForArticleId::getRawArticleStoreMasterData)
                  .orElse(null));
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
              Optional.ofNullable(
                      articleComponentForArticleIdMap.get(rawArticleData.getArticleId()))
                  .map(ArticleComponentForArticleId::getArticleComponent)
                  .orElse(null));
        });

    log.info(
        "IPO Article Details List (" + articleIdList.size() + ") - parallel execution duration: "
            + "\n"
            + "articleComponentDuration: "
            + articleComponentDurationAC
            + "\n"
            + "articleMasterDataDuration: "
            + articleMasterDataDurationAC
            + "\n"
            + "rawArticleStoreMasterDataDuration: "
            + rawArticleStoreMasterDataDurationAC
            + "\n"
            + "articleDataFromMovieDuration: "
            + articleDataFromMovieDuration
            + "\n"
            + "articleStockLocationListDuration: "
            + articleStockLocationListDurationACL
            + "\n"
            + "articleLocationListDuration: "
            + articleLocationListDurationACL);
  }
}
