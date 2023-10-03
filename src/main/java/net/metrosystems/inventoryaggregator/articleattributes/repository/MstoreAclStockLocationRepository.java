package net.metrosystems.inventoryaggregator.articleattributes.repository;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleStockLocation;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.ArticleIdStockLocation;
import net.metrosystems.inventoryaggregator.articleattributes.service.MstoreAclClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

@Repository
public class MstoreAclStockLocationRepository {

  private final MstoreAclClientService mstoreAclClientService;

  @Autowired
  public MstoreAclStockLocationRepository(MstoreAclClientService mstoreAclClientService) {
    this.mstoreAclClientService = mstoreAclClientService;
  }

  @Async("asyncExecutor")
  public CompletableFuture<List<ArticleStockLocation>> getArticleStockLocation(
      ArticleId articleId, String countryCode, Integer storeNumber) {
    List<ArticleStockLocation> mstoreAclStockLocations =
        mstoreAclClientService.getArticleStockLocation(articleId, countryCode, storeNumber);
    return CompletableFuture.completedFuture(mstoreAclStockLocations);
  }

  @Async("asyncExecutor")
  public CompletableFuture<List<ArticleIdStockLocation>> getStockLocationsForArticleList(
      List<ArticleId> articleIdList, String countryCode, Integer storeNo) {
    List<ArticleIdStockLocation> mstoreAclStockLocations =
        mstoreAclClientService.getArticleStockLocationForArticleList(
            articleIdList, countryCode, storeNo);
    return mstoreAclStockLocations != null
        ? CompletableFuture.completedFuture(mstoreAclStockLocations)
        : CompletableFuture.completedFuture(Collections.emptyList());
  }
}
