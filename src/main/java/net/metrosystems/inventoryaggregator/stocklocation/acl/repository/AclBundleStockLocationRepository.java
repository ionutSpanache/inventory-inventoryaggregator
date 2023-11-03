package net.metrosystems.inventoryaggregator.stocklocation.acl.repository;

import static java.lang.String.valueOf;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.stocklocation.acl.model.AclArticleStockLocation;
import net.metrosystems.inventoryaggregator.stocklocation.acl.model.AclBundleStockLocation;
import net.metrosystems.inventoryaggregator.stocklocation.acl.service.AclStockService;
import net.metrosystems.inventoryaggregator.stocklocation.model.ArticleStockLocations;
import net.metrosystems.inventoryaggregator.stocklocation.model.StockLocation;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AclBundleStockLocationRepository {

  private final AclStockService aclStockService;

  @Async("asyncExecutor")
  public CompletableFuture<List<StockLocation>> getArticleStockLocation(
      ArticleId articleId, String countryCode, String salesLine, Integer storeNo) {
    List<AclBundleStockLocation> aclBundleStockLocations =
        aclStockService.getAclBundleStockLocations(
            countryCode,
            salesLine,
            storeNo,
            articleId.getArticleNumber(),
            articleId.getVariantNumber());

    List<StockLocation> stockLocationList =
        transformAclBundleStockLocationsToArticleStockLocationListByBundle(
            aclBundleStockLocations, articleId.getBundleNumber());

    return CompletableFuture.completedFuture(stockLocationList);
  }

  @Async("asyncExecutor")
  public CompletableFuture<List<ArticleStockLocations>> getStockLocationsForArticleList(
      List<ArticleId> articleIdList, String countryCode, String salesLine, Integer storeNo) {
    List<AclArticleStockLocation> aclBundleStockLocations =
        aclStockService.getStockLocationsForArticleList(
            countryCode, salesLine, storeNo, articleIdList);

    List<ArticleStockLocations> articleStockLocationsList =
        Stream.ofNullable(aclBundleStockLocations)
            .flatMap(Collection::stream)
            .filter(this::isArticleKeyNotNull)
            .map(this::getArticleStockLocations)
            .collect(Collectors.toList());

    return CompletableFuture.completedFuture(articleStockLocationsList);
  }

  private ArticleStockLocations getArticleStockLocations(
      AclArticleStockLocation aclArticleStockLocation) {
    return ArticleStockLocations.builder()
        .articleId(
            ArticleId.builder()
                .articleNumber(aclArticleStockLocation.getArticleNumber())
                .variantNumber(Long.valueOf(aclArticleStockLocation.getVariantNumber()))
                .bundleNumber(Long.valueOf(aclArticleStockLocation.getBundleNumber()))
                .build())
        .stockLocations(
            transformAclBundleStockLocationsToArticleStockLocationListByBundle(
                aclArticleStockLocation.getAisleBay(),
                Long.valueOf(aclArticleStockLocation.getBundleNumber())))
        .build();
  }

  private boolean isArticleKeyNotNull(AclArticleStockLocation aclArticleStockLocation) {
    return aclArticleStockLocation.getArticleNumber() != null
        && aclArticleStockLocation.getVariantNumber() != null
        && aclArticleStockLocation.getBundleNumber() != null;
  }

  private List<StockLocation> transformAclBundleStockLocationsToArticleStockLocationListByBundle(
      List<AclBundleStockLocation> aclBundleStockLocations, Long bundleNo) {
    if (!isEmpty(aclBundleStockLocations)) {
      List<AclBundleStockLocation> aclStockLocationsForBundle =
          aclBundleStockLocations.stream()
              .filter(
                  aclBundleStockLocation -> (bundleNo.equals(aclBundleStockLocation.getBundleNo())))
              .toList();

      if (!isEmpty(aclStockLocationsForBundle)) {
        List<StockLocation> stockLocationList = new ArrayList<>();
        for (AclBundleStockLocation aclBundleStockLocation : aclStockLocationsForBundle) {
          StockLocation stockLocation =
              new StockLocation(
                  aclBundleStockLocation.getBundleNo(),
                  leftPad(valueOf(aclBundleStockLocation.getAisleNo()), 3, "0"),
                  leftPad(valueOf(aclBundleStockLocation.getBayNo()), 3, "0"),
                  aclBundleStockLocation.getQuantity(),
                  aclBundleStockLocation.getBestBeforeDate(),
                  aclBundleStockLocation.getDateOfCreation(),
                  aclBundleStockLocation.getLastChangeDate(),
                  aclBundleStockLocation.getSerialShippingContainerCode(),
                  null,
                  null);
          stockLocationList.add(stockLocation);
        }
        return stockLocationList;
      }
    }
    return null;
  }
}
