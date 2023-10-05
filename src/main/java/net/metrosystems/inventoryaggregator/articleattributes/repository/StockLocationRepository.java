package net.metrosystems.inventoryaggregator.articleattributes.repository;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleStockLocation;
import net.metrosystems.inventoryaggregator.articleattributes.model.DataType;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.ArticleIdStockLocation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class StockLocationRepository {

  private final MstoreAclStockLocationRepository mstoreAclStockLocationRepository;

  private final AclBundleStockLocationRepository aclBundleStockLocationRepository;

  @Value("#{'${countries.slm.active:null}'}")
  private List<String> countriesSlmActive;

  public CompletableFuture<List<ArticleStockLocation>> getArticleStockLocation(
      ArticleId articleId, String countryCode, String salesLine, Integer storeNo) {
    boolean slmActive =
        !isEmpty(countriesSlmActive)
            && (countriesSlmActive.contains(countryCode)
                || "ALL".equals(countriesSlmActive.get(0)));

    CompletableFuture<List<ArticleStockLocation>> articleStockLocationList =
        new CompletableFuture<>();
    try {
      if (slmActive) {
        articleStockLocationList =
            mstoreAclStockLocationRepository.getArticleStockLocation(
                articleId, countryCode, storeNo);
      } else {
        articleStockLocationList =
            aclBundleStockLocationRepository.getArticleStockLocation(
                articleId, countryCode, salesLine, storeNo);
      }
    } catch (Exception e) {
      log.error("StockLocationRepository.getArticleStockLocation - - error: {}", e.getMessage(), e);
    }
    return articleStockLocationList;
  }

  public CompletableFuture<List<ArticleIdStockLocation>> getArticleStockLocation(
      List<ArticleId> articleIdList,
      String countryCode,
      String salesLine,
      Integer storeNo,
      Optional<List<String>> dataTypeList) {
    boolean slmActive =
        !isEmpty(countriesSlmActive)
            && (countriesSlmActive.contains(countryCode)
                || "ALL".equals(countriesSlmActive.get(0)));

    CompletableFuture<List<ArticleIdStockLocation>> articleStockLocationList =
        CompletableFuture.completedFuture(Collections.emptyList());
    if (dataTypeList.isEmpty()
        || dataTypeList.get().contains(DataType.ARTICLE_STOCK_LOCATION.getDataType())) {
      try {
        if (slmActive) {
          articleStockLocationList =
              mstoreAclStockLocationRepository.getStockLocationsForArticleList(
                  articleIdList, countryCode, storeNo);
        } else {
          articleStockLocationList =
              aclBundleStockLocationRepository.getStockLocationsForArticleList(
                  articleIdList, countryCode, salesLine, storeNo);
        }

      } catch (Exception e) {
        log.error(
            "ArticleListDetailsService.articleStockLocationList - articleDataFromMovieRepository - error: {}",
            e.getMessage(),
            e);
      }
    }
    return articleStockLocationList;
  }
}
