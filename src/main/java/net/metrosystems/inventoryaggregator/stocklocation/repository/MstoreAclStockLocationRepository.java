package net.metrosystems.inventoryaggregator.stocklocation.repository;

import static java.util.Collections.emptyList;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.stocklocation.model.ArticleStockLocations;
import net.metrosystems.inventoryaggregator.stocklocation.model.StockLocation;
import net.metrosystems.inventoryaggregator.stocklocation.service.StockLocationClientService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MstoreAclStockLocationRepository {
  private final StockLocationClientService stockLocationClientService;

  @Async("asyncExecutor")
  public CompletableFuture<List<StockLocation>> getArticleStockLocation(
      ArticleId articleId, String countryCode, Integer storeNumber) {
    List<StockLocation> articleStockLocations =
        stockLocationClientService.getArticleStockLocation(articleId, countryCode, storeNumber);
    return CompletableFuture.completedFuture(articleStockLocations);
  }

  @Async("asyncExecutor")
  public CompletableFuture<List<ArticleStockLocations>> getStockLocationsForArticleList(
      List<ArticleId> articleIds, String countryCode, Integer storeNo) {
    List<ArticleStockLocations> articleStockLocations =
        stockLocationClientService.getArticleStockLocationForArticleList(
            articleIds, countryCode, storeNo);
    return articleStockLocations != null
        ? CompletableFuture.completedFuture(articleStockLocations)
        : CompletableFuture.completedFuture(emptyList());
  }
}
