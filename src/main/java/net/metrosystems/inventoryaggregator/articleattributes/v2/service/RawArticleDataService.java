package net.metrosystems.inventoryaggregator.articleattributes.v2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.*;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.StockData;
import net.metrosystems.inventoryaggregator.articleattributes.model.articlecomponent.ArticleComponent;
import net.metrosystems.inventoryaggregator.articleattributes.repository.GeneralDataRepository;
import net.metrosystems.inventoryaggregator.articleattributes.repository.RawArticleStoreMasterDataRepository;
import net.metrosystems.inventoryaggregator.articlestoremasterdata.model.AclStoreMasterData;
import net.metrosystems.inventoryaggregator.common.model.StoreId;
import net.metrosystems.inventoryaggregator.common.movieacl.MovieAclService;
import net.metrosystems.inventoryaggregator.stocklocation.model.StockLocation;
import net.metrosystems.inventoryaggregator.stocklocation.repository.StockLocationRepository;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
@RequiredArgsConstructor
public class RawArticleDataService {

  private final GeneralDataRepository generalDataRepository;
  private final ArticleProcessingService articleProcessingService;
  private final StockLocationRepository stockLocationRepository;
  private final RawArticleStoreMasterDataRepository rawArticleStoreMasterDataRepository;

  private final MovieAclService movieAclService;

  public void buildRawArticleDataList(
      ArticleId articleId,
      StoreId storeId,
      List<RawArticleData> rawArticleDataList,
      Optional<List<String>> dataTypeList)
      throws ExecutionException, InterruptedException {

    CompletableFuture<ArticleComponent> articleComponentCompletableFuture =
        articleProcessingService.getArticleComponent(
            storeId.getCountryCode(), storeId.getStoreNumber(), articleId.getArticleNumber());

    CompletableFuture<ArticleMasterId> articleMasterIdForArticleId =
        articleProcessingService.getArticleMasterId(
            articleId, storeId.getCountryCode(), "EN", storeId.getStoreNumber());

    CompletableFuture<AclStoreMasterData> aclStoreMasterData =
        articleProcessingService.getAclStoreMasterData(
            articleId, storeId.getCountryCode(), storeId.getSalesLine(), storeId.getStoreNumber());

    CompletableFuture<StockData> stockData =
        articleProcessingService.getArticleDataFromMovie(
            articleId, storeId.getCountryCode(), storeId.getSalesLine(), storeId.getStoreNumber());

    CompletableFuture<List<StockLocation>> articleStockLocationList =
        stockLocationRepository.getArticleStockLocation(
            articleId, storeId.getCountryCode(), storeId.getStoreNumber(), dataTypeList);

    CompletableFuture<String> dailyMeanSales =
        movieAclService.getAverageSales(
            articleId, storeId.getCountryCode(), storeId.getSalesLine(), storeId.getStoreNumber());

    CompletableFuture.allOf(
        articleComponentCompletableFuture,
        articleMasterIdForArticleId,
        aclStoreMasterData,
        stockData,
        articleStockLocationList,
        dailyMeanSales);

    generalDataRepository.enhanceRawArticleStoreMasterDataWithDepartmentName(
        storeId.getCountryCode(),
        storeId.getSalesLine(),
        storeId.getStoreNumber(),
        aclStoreMasterData.get());

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
            .articleStoreMasterData(
                rawArticleStoreMasterDataRepository.getRawArticleStoreMasterData(
                    articleMasterIdForArticleId.get(), aclStoreMasterData.get()))
            .stockDetails(stockDetails)
            .plannedDeliveryDates(plannedDeliveryDates)
            .stockLocations(articleStockLocationList.get())
            .articleComponent(articleComponentCompletableFuture.get())
            .productLifeCycleData(buildProductLifeCycleData(articleMasterData))
            .channelDeliveryFlag(articleMasterData.getChannelDeliveryFlag())
            .salesAreaFlag(articleMasterData.getSalesAreaFlag())
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
}
