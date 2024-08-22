package net.metrosystems.inventoryaggregator.common.stock_information.movieacl;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.StockData;
import net.metrosystems.inventoryaggregator.common.stock_information.movieacl.client.MovieAclClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieAclService {
  private final MovieAclClient movieAclClient;

  @Async("asyncExecutor")
  public CompletableFuture<List<StockData>> getStockDataListAsync(
      List<ArticleId> articleIdList, String countryCode, String salesLine, Integer storeNumber) {
    List<StockData> stockDataList =
        movieAclClient.getStockDataList(articleIdList, countryCode, salesLine, storeNumber);

    return CompletableFuture.completedFuture(stockDataList);
  }

  @Async("asyncExecutor")
  public CompletableFuture<StockData> getStockDataAsync(
      ArticleId articleId, String countryCode, String salesLine, Integer storeNumber) {
    List<StockData> stockDataList =
        movieAclClient.getStockDataList(List.of(articleId), countryCode, salesLine, storeNumber);
    if (CollectionUtils.isEmpty(stockDataList)) {
      return CompletableFuture.completedFuture(null);
    }
    return CompletableFuture.completedFuture(stockDataList.get(0));
  }
}
