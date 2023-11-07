package net.metrosystems.inventoryaggregator.articleattributes.v2.service;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleComponent;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleDataFromMovie;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticlePlannedDeliveryDate;
import net.metrosystems.inventoryaggregator.articleattributes.model.MaximumRetailPrice;
import net.metrosystems.inventoryaggregator.articleattributes.model.ProductLifeCycleData;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawArticleData;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawArticleStoreMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.StockInformation;
import net.metrosystems.inventoryaggregator.articleattributes.repository.GeneralDataRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.RawArticleStoreMasterDataRepository;
import net.metrosystems.inventoryaggregator.articleattributes.service.ArticleProcessingService;
import net.metrosystems.inventoryaggregator.common.model.StoreId;
import net.metrosystems.inventoryaggregator.common.stock_information.movie.service.MovieService;
import net.metrosystems.inventoryaggregator.stocklocation.model.StockLocation;
import net.metrosystems.inventoryaggregator.stocklocation.repository.StockLocationRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RawArticleDataService {

  private final RawArticleStoreMasterDataRepository rawArticleStoreMasterDataRepository;
  private final GeneralDataRepository generalDataRepository;
  private final MovieService movieService;
  private final ArticleProcessingService articleProcessingService;
  private final ArticleProcessingServiceV2 articleProcessingServiceV2;
  private final StockLocationRepository stockLocationRepository;

  private static final Map<String, String> LANGUAGE_CODE_EXCEPTIONS = new HashMap<>();

  static {
    LANGUAGE_CODE_EXCEPTIONS.put("UA", "UK");
    LANGUAGE_CODE_EXCEPTIONS.put("KZ", "KK");
    LANGUAGE_CODE_EXCEPTIONS.put("MD", "RO");
    LANGUAGE_CODE_EXCEPTIONS.put("PK", "UR");
    LANGUAGE_CODE_EXCEPTIONS.put("RS", "SR");
    LANGUAGE_CODE_EXCEPTIONS.put("CZ", "CS");
  }

  public void buildRawArticleDataList(
      ArticleId articleId,
      StoreId storeId,
      String languageCode,
      List<RawArticleData> rawArticleDataList,
      ArticleComponent articleComponent,
      Optional<List<String>> dataTypeList)
      throws ExecutionException, InterruptedException {

    String correctLanguageCode =
        LANGUAGE_CODE_EXCEPTIONS.getOrDefault(storeId.getCountryCode(), storeId.getCountryCode());

    CompletableFuture<ArticleMasterId> articleMasterIdForArticleId =
        articleProcessingServiceV2.getArticleMasterId(
            articleId, storeId.getCountryCode(), correctLanguageCode, storeId.getStoreNumber());

    CompletableFuture<RawArticleStoreMasterData> rawArticleStoreMasterData =
        articleProcessingService.getRawArticleStoreMasterData(
            articleId, storeId.getCountryCode(), storeId.getSalesLine(), storeId.getStoreNumber());

    CompletableFuture<ArticleDataFromMovie> articleDataFromMovie =
        articleProcessingService.getArticleDataFromMovie(
            articleId, storeId.getCountryCode(), storeId.getSalesLine(), storeId.getStoreNumber());

    CompletableFuture<List<StockLocation>> articleStockLocationList =
        stockLocationRepository.getArticleStockLocation(
            articleId, storeId.getCountryCode(), storeId.getSalesLine(), storeId.getStoreNumber(), dataTypeList);

    CompletableFuture<MaximumRetailPrice> maximumRetailPrice =
        rawArticleStoreMasterDataRepository.getMaximumRetailPrice(
            storeId.getCountryCode(), storeId.getSalesLine(), storeId.getStoreNumber(), articleId);

    CompletableFuture<String> dailyMeanSales =
        movieService.getTotalAverageSales(storeId.getCountryCode(), storeId.getSalesLine(), storeId.getStoreNumber(), articleId);

    CompletableFuture.allOf(
        articleMasterIdForArticleId,
        rawArticleStoreMasterData,
        articleDataFromMovie,
        articleStockLocationList,
        maximumRetailPrice,
        dailyMeanSales);

    generalDataRepository.enhanceRawArticleStoreMasterDataWithDepartmentName(
        storeId.getCountryCode(), storeId.getSalesLine(), storeId.getStoreNumber(), rawArticleStoreMasterData.get());

    updateWithDefaultValues(rawArticleStoreMasterData.get());

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

    ArticleMasterData articleMasterData = articleMasterIdForArticleId.get().getArticleMasterData();
    RawArticleData rawArticleData =
        RawArticleData.builder()
            .articleId(articleId)
            .articleMasterData(articleMasterData)
            .articleStoreMasterData(rawArticleStoreMasterData.get())
            .stockDetails(stockDetails)
            .plannedDeliveryDates(plannedDeliveryDates)
            .stockLocations(articleStockLocationList.get())
            .articleComponent(articleComponent)
            .productLifeCycleData(buildProductLifeCycleData(articleMasterData))
            .maximumRetailPrice(maximumRetailPrice.get())
            .channelDeliveryFlag(articleMasterData.getChannelDeliveryFlag())
            .returnPossibilityStoreSupplierNumber(
                articleMasterData.getReturnPossibilityStoreSupplierNumber())
            .minimumRetailPrice(articleMasterData.getMinimumRetailPrice())
            .multiLanguageData(articleMasterIdForArticleId.get().getMultiLanguageData())
            .build();

    if (rawArticleDataList != null) {
      rawArticleDataList.add(rawArticleData);
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

  private void updateWithDefaultValues(RawArticleStoreMasterData rawArticleStoreMasterData) {
    if (rawArticleStoreMasterData.getSalesBlockingCode() == null) {
      rawArticleStoreMasterData.setSalesBlockingCode(1);
    }
  }
}
