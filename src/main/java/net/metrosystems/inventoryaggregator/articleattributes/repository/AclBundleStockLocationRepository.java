package net.metrosystems.inventoryaggregator.articleattributes.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleStockLocation;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.AclArticleStockLocation;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.ArticleIdStockLocation;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.ArticleLocationForArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.articlelocation.ArticleLocation;
import net.metrosystems.inventoryaggregator.articleattributes.model.articlelocation.StocktakingArticleLocation;
import net.metrosystems.inventoryaggregator.common.stock_location.acl.model.AclBundleStockLocation;
import net.metrosystems.inventoryaggregator.common.stock_location.acl.service.AclStockService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

@Repository
public class AclBundleStockLocationRepository {

  private final AclStockService aclStockService;

  @Autowired
  public AclBundleStockLocationRepository(AclStockService aclStockService) {
    this.aclStockService = aclStockService;
  }

  @Async("asyncExecutor")
  public CompletableFuture<ArticleLocation> getArticleLocation(
      String countryCode, String salesLine, Integer storeNo, ArticleId articleId) {
    ArticleLocation articleLocation =
        aclStockService.getAclArticleLocations(countryCode, salesLine, storeNo, articleId);

    return CompletableFuture.completedFuture(
        transformAclArticleLocationsToInventoryArticleLocation(articleLocation));
  }

  @Async("asyncExecutor")
  public CompletableFuture<List<ArticleLocationForArticleId>> getArticleLocationForArticleList(
      String countryCode, String salesLine, Integer storeNo, List<ArticleId> articleIdList) {
    List<ArticleLocationForArticleId> articleLocationForArticleIdList =
        aclStockService.getAclArticleLocationsForArticleList(
            countryCode, salesLine, storeNo, articleIdList);
    articleLocationForArticleIdList.forEach(
        articleLocationForArticleId ->
            articleLocationForArticleId.setArticleLocation(
                transformAclArticleLocationsToInventoryArticleLocation(
                    articleLocationForArticleId.getArticleLocation())));
    return CompletableFuture.completedFuture(articleLocationForArticleIdList);
  }

  private ArticleLocation transformAclArticleLocationsToInventoryArticleLocation(
      ArticleLocation articleLocation) {
    if (Optional.ofNullable(articleLocation).isPresent()
        && isArticleLocationList(articleLocation)) {
      for (StocktakingArticleLocation stocktakingArticleLocation :
          articleLocation.getStocktakingArticleLocationList()) {
        stocktakingArticleLocation.setAisle(
            StringUtils.leftPad(stocktakingArticleLocation.getAisle(), 3, "0"));
        stocktakingArticleLocation.setShelf(
            StringUtils.leftPad(stocktakingArticleLocation.getShelf(), 3, "0"));
      }
      return articleLocation;
    }
    return null;
  }

  private boolean isArticleLocationList(ArticleLocation articleLocation) {
    return (articleLocation.getStocktakingArticleLocationList() != null)
        && (!articleLocation.getStocktakingArticleLocationList().isEmpty());
  }
}
