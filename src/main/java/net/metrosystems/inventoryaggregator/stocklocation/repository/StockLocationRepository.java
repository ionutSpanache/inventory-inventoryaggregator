package net.metrosystems.inventoryaggregator.stocklocation.repository;

import static net.metrosystems.inventoryaggregator.articleattributes.model.DataType.ARTICLE_STOCK_LOCATION;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.stocklocation.model.ArticleStockLocations;
import net.metrosystems.inventoryaggregator.stocklocation.model.StockLocation;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class StockLocationRepository {

  private final MstoreAclStockLocationRepository mstoreAclStockLocationRepository;

  public CompletableFuture<List<StockLocation>> getArticleStockLocation(
      ArticleId articleId,
      String countryCode,
      Integer storeNo,
      Optional<List<String>> dataTypeList) {

    CompletableFuture<List<StockLocation>> articleStockLocations =
        CompletableFuture.completedFuture(Collections.emptyList());

    if (dataTypeList.isEmpty()
        || !dataTypeList.get().contains(ARTICLE_STOCK_LOCATION.getDataTypeValue())) {
      return articleStockLocations;
    }

    try {
      articleStockLocations =
          mstoreAclStockLocationRepository.getArticleStockLocation(articleId, countryCode, storeNo);
    } catch (Exception e) {
      log.error("StockLocationRepository.getArticleStockLocation - error: {}", e.getMessage(), e);
    }
    return articleStockLocations;
  }

  public CompletableFuture<List<ArticleStockLocations>> getArticleStockLocation(
      List<ArticleId> articleIdList,
      String countryCode,
      Integer storeNo,
      Optional<List<String>> dataTypeList) {

    CompletableFuture<List<ArticleStockLocations>> articleStockLocations =
        CompletableFuture.completedFuture(Collections.emptyList());

    if (dataTypeList.isEmpty()
        || !dataTypeList.get().contains(ARTICLE_STOCK_LOCATION.getDataTypeValue())) {
      return articleStockLocations;
    }

    try {
      articleStockLocations =
          mstoreAclStockLocationRepository.getStockLocationsForArticleList(
              articleIdList, countryCode, storeNo);
    } catch (Exception e) {
      log.error("StockLocationRepository.getArticleStockLocation - error: {}", e.getMessage(), e);
    }
    return articleStockLocations;
  }
}
