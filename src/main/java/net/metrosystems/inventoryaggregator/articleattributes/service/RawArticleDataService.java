package net.metrosystems.inventoryaggregator.articleattributes.service;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleComponent;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleDataFromMovie;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticlePlannedDeliveryDate;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleStockLocation;
import net.metrosystems.inventoryaggregator.articleattributes.model.MaximumRetailPrice;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawArticleData;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawArticleStoreMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.StockInformation;
import net.metrosystems.inventoryaggregator.articleattributes.model.articlelocation.ArticleLocation;
import net.metrosystems.inventoryaggregator.articleattributes.repository.AclBundleStockLocationRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.ArticleDataFromMovieRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.ArticleMasterDataRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.DataSourceRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.GeneralDataRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.RawArticleStoreMasterDataRepository;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.aricle_cache_isu_istem_store_merchandise.StoreInformationGroup;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard.IduItemDerivedStandard;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.service.ArticleCacheService;
import net.metrosystems.inventoryaggregator.common.model.ArticleNotFoundException;
import net.metrosystems.inventoryaggregator.common.stock_information.movie.service.MovieService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RawArticleDataService {

  private final ArticleCacheService articleCacheService;
  private final RawArticleStoreMasterDataRepository rawArticleStoreMasterDataRepository;
  private final ArticleMasterDataRepository articleMasterDataRepository;
  private final ArticleDataFromMovieRepository articleDataFromMovieRepository;
  private final AclBundleStockLocationRepository aclBundleStockLocationRepository;
  private final GeneralDataRepository generalDataRepository;
  private final MovieService movieService;

  private final DataSourceRepository dataSourceRepository;

  public void buildRawArticleDataList(
      ArticleId articleId,
      String countryCode,
      String salesLine,
      String languageCode,
      Integer storeNo,
      List<RawArticleData> rawArticleDataList,
      ArticleComponent articleComponent)
      throws ExecutionException, InterruptedException {

    CompletableFuture<ArticleMasterId> articleMasterIdForArticleId = new CompletableFuture<>();
    try {
      articleMasterIdForArticleId =
          dataSourceRepository.getArticleMasterIdForArticleId(
              articleId, countryCode, salesLine, languageCode, storeNo);
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      log.error(
          "RawArticleDataService.buildRawArticleDataList - articleMasterIdForArticleId - error: {}",
          e.getMessage(),
          e);
    }

    CompletableFuture<RawArticleStoreMasterData> rawArticleStoreMasterData =
        new CompletableFuture<>();
    try {
      rawArticleStoreMasterData =
          rawArticleStoreMasterDataRepository.getRawArticleStoreMasterData(
              articleId, countryCode, salesLine, storeNo);
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      log.error(
          "RawArticleDataService.buildRawArticleDataList - rawArticleStoreMasterData - error: {}",
          e.getMessage(),
          e);
      Thread.currentThread().interrupt();
    }

    CompletableFuture<ArticleDataFromMovie> articleDataFromMovie = new CompletableFuture<>();
    try {
      articleDataFromMovie =
          articleDataFromMovieRepository.getArticleDataFromMovie(
              articleId, countryCode, salesLine, storeNo);
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      log.error(
          "RawArticleDataService.buildRawArticleDataList - articleDataFromMovie - error: {}",
          e.getMessage(),
          e);
    }

    CompletableFuture<List<ArticleStockLocation>> articleStockLocationList =
        new CompletableFuture<>();
    try {
      articleStockLocationList =
          aclBundleStockLocationRepository.getArticleStockLocation(
              articleId, countryCode, salesLine, storeNo);
    } catch (ArticleNotFoundException ex) {
      throw new ArticleNotFoundException();
    } catch (Exception e) {
      log.error(
          "RawArticleDataService.buildRawArticleDataList - articleStockLocationList - error: {}",
          e.getMessage(),
          e);
    }

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

    CompletableFuture<List<IduItemDerivedStandard>> iduDerivedStandardsList =
        articleMasterDataRepository.getIduItemDerivedStandardsAsync(countryCode, articleId);

    CompletableFuture<MaximumRetailPrice> maximumRetailPrice =
        rawArticleStoreMasterDataRepository.getMaximumRetailPrice(
            countryCode, salesLine, storeNo, articleId);

    CompletableFuture<StoreInformationGroup> storeInformationGroup =
        articleMasterDataRepository.getDeliveryFlagAndStoreInformation(
            countryCode, articleId, storeNo);

    CompletableFuture<Optional<IduItemDerivedStandard>> iduItemDerivedStandard =
        articleMasterDataRepository.getMultiLanguageData(countryCode, articleId);

    CompletableFuture<String> dailyMeanSales =
        movieService.getTotalAverageSales(countryCode, salesLine, storeNo, articleId);

    CompletableFuture.allOf(
        articleMasterIdForArticleId,
        rawArticleStoreMasterData,
        articleDataFromMovie,
        articleStockLocationList,
        articleLocationList,
        iduDerivedStandardsList,
        maximumRetailPrice,
        iduItemDerivedStandard,
        dailyMeanSales);

    generalDataRepository.enhanceRawArticleStoreMasterDataWithDepartmentName(
        countryCode, salesLine, storeNo, rawArticleStoreMasterData.get());

    StockInformation stockDetails = null;
    List<ArticlePlannedDeliveryDate> plannedDeliveryDates = null;

    if (articleDataFromMovie.get() != null) {
      stockDetails = articleDataFromMovie.get().getArticleStockData();
      plannedDeliveryDates = articleDataFromMovie.get().getArticlePlannedDeliveryDates();
      if (dailyMeanSales.get() != null && stockDetails != null) {
        Float parsedDailyMeanSales = Float.parseFloat(dailyMeanSales.get());
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        String dailyMeanSales30 = df.format(parsedDailyMeanSales);
        stockDetails.setDailyMeanSales30(dailyMeanSales30);
      }
    }

    RawArticleData rawArticleData =
        RawArticleData.builder()
            .articleId(articleId)
            .articleMasterData(articleMasterIdForArticleId.get().getArticleMasterData())
            .articleStoreMasterData(rawArticleStoreMasterData.get())
            .stockDetails(stockDetails)
            .plannedDeliveryDates(plannedDeliveryDates)
            .stockLocations(articleStockLocationList.get())
            .articleLocation(articleLocationList.get())
            .articleComponent(articleComponent)
            .productLifeCycleData(
                articleCacheService.buildProductLifeCycleData(iduDerivedStandardsList.get()))
            .maximumRetailPrice(maximumRetailPrice.get())
            .channelDeliveryFlag(storeInformationGroup.get().getChannelDeliveryFlag())
            .returnPossibilityStoreSupplierNumber(
                storeInformationGroup.get().getStoreItemPhaseOutInformation() != null
                    ? storeInformationGroup
                        .get()
                        .getStoreItemPhaseOutInformation()
                        .getReturnPossibilityStoreSupplierNumber()
                    : null)
            .minimumRetailPrice(
                articleMasterIdForArticleId.get().getArticleMasterData().getMinimumRetailPrice())
            .multiLanguageData(articleMasterIdForArticleId.get().getMultiLanguageData())
            .build();

    if (rawArticleDataList != null) {
      rawArticleDataList.add(rawArticleData);
    }
  }
}
