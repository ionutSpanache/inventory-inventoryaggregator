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
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.StockData;
import net.metrosystems.inventoryaggregator.articleattributes.model.articlecomponent.ArticleComponent;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticlePlannedDeliveryDate;
import net.metrosystems.inventoryaggregator.articleattributes.model.ProductLifeCycleData;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawArticleData;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawArticleStoreMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.StockInformation;
import net.metrosystems.inventoryaggregator.articleattributes.repository.GeneralDataRepository;
import net.metrosystems.inventoryaggregator.articleattributes.service.ArticleProcessingService;
import net.metrosystems.inventoryaggregator.common.model.StoreId;
import net.metrosystems.inventoryaggregator.common.movieacl.MovieAclService;
import net.metrosystems.inventoryaggregator.stocklocation.model.StockLocation;
import net.metrosystems.inventoryaggregator.stocklocation.repository.StockLocationRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RawArticleDataService {

  private final GeneralDataRepository generalDataRepository;
  private final ArticleProcessingService articleProcessingService;
  private final ArticleProcessingServiceV2 articleProcessingServiceV2;
  private final StockLocationRepository stockLocationRepository;

  private static final Map<String, String> LANGUAGE_CODE_EXCEPTIONS = new HashMap<>();

  static {
    LANGUAGE_CODE_EXCEPTIONS.put("UA", "UK");
    LANGUAGE_CODE_EXCEPTIONS.put("KZ", "RU");
    LANGUAGE_CODE_EXCEPTIONS.put("MD", "RO");
    LANGUAGE_CODE_EXCEPTIONS.put("PK", "UR");
    LANGUAGE_CODE_EXCEPTIONS.put("RS", "SR");
    LANGUAGE_CODE_EXCEPTIONS.put("CZ", "CS");
  }

  private final MovieAclService movieAclService;

  public void buildRawArticleDataList(
      ArticleId articleId,
      StoreId storeId,
      String languageCode,
      List<RawArticleData> rawArticleDataList,
      Optional<List<String>> dataTypeList)
      throws ExecutionException, InterruptedException {

    String correctLanguageCode =
        LANGUAGE_CODE_EXCEPTIONS.getOrDefault(storeId.getCountryCode(), storeId.getCountryCode());

    CompletableFuture<ArticleComponent> articleComponentCompletableFuture =
        articleProcessingServiceV2.getArticleComponent(storeId.getCountryCode(),storeId.getStoreNumber(), articleId.getArticleNumber());

    CompletableFuture<ArticleMasterId> articleMasterIdForArticleId =
        articleProcessingServiceV2.getArticleMasterId(
            articleId, storeId.getCountryCode(), correctLanguageCode, storeId.getStoreNumber());

    CompletableFuture<RawArticleStoreMasterData> rawArticleStoreMasterData =
        articleProcessingService.getRawArticleStoreMasterData(
            articleId, storeId.getCountryCode(), storeId.getSalesLine(), storeId.getStoreNumber());

    CompletableFuture<StockData> stockData =
        articleProcessingService.getArticleDataFromMovie(
            articleId, storeId.getCountryCode(), storeId.getSalesLine(), storeId.getStoreNumber());

    CompletableFuture<List<StockLocation>> articleStockLocationList =
        stockLocationRepository.getArticleStockLocation(
            articleId, storeId.getCountryCode(), storeId.getSalesLine(), storeId.getStoreNumber(), dataTypeList);

    CompletableFuture<String> dailyMeanSales =
        movieAclService.getAverageSales(
            articleId, storeId.getCountryCode(), storeId.getSalesLine(), storeId.getStoreNumber());

    CompletableFuture.allOf(
        articleComponentCompletableFuture,
        articleMasterIdForArticleId,
        rawArticleStoreMasterData,
        stockData,
        articleStockLocationList,
        dailyMeanSales);

    generalDataRepository.enhanceRawArticleStoreMasterDataWithDepartmentName(
        storeId.getCountryCode(), storeId.getSalesLine(), storeId.getStoreNumber(), rawArticleStoreMasterData.get());

    updateWithDefaultValues(rawArticleStoreMasterData.get());

    StockInformation stockDetails = null;
    List<ArticlePlannedDeliveryDate> plannedDeliveryDates = null;

    if (stockData.get() != null) {
      stockDetails = stockData.get().getArticleStockData();
      plannedDeliveryDates = stockData.get().getArticlePlannedDeliveryDates();
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
            .articleComponent(articleComponentCompletableFuture.get())
            .productLifeCycleData(buildProductLifeCycleData(articleMasterData))
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
