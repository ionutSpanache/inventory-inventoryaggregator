package net.metrosystems.inventoryaggregator.articleattributes.repository;

import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleStockLocation;
import net.metrosystems.inventoryaggregator.articleattributes.model.articlelocation.ArticleLocation;
import net.metrosystems.inventoryaggregator.articleattributes.model.articlelocation.StocktakingArticleLocation;
import net.metrosystems.inventoryaggregator.common.stock_location.acl.model.AclBundleStockLocation;
import net.metrosystems.inventoryaggregator.common.stock_location.acl.service.AclStockService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Repository
public class AclBundleStockLocationRepository {
    private final AclStockService aclStockService;

    @Autowired
    public AclBundleStockLocationRepository(AclStockService aclStockService){
        this.aclStockService = aclStockService;
    }

    @Async
    public CompletableFuture<List<ArticleStockLocation>> getArticleStockLocation(ArticleId articleId, String countryCode, String salesLine, Integer storeNo) {
        List<AclBundleStockLocation> aclBundleStockLocations = aclStockService.getAclBundleStockLocations(
                countryCode, salesLine, storeNo,
                articleId.getArticleNumber(),
                articleId.getVariantNumber());

        List<ArticleStockLocation> articleStockLocationList = transformAclBundleStockLocationsToArticleStockLocationListByBundle(
                aclBundleStockLocations,
                articleId.getBundleNumber());

        return CompletableFuture.completedFuture(articleStockLocationList);
    }

    private List<ArticleStockLocation> transformAclBundleStockLocationsToArticleStockLocationListByBundle(
            List<AclBundleStockLocation> aclBundleStockLocations,
            Long bundleNo) {
        if ((aclBundleStockLocations != null)
                && (!aclBundleStockLocations.isEmpty())) {
            List<AclBundleStockLocation> aclStockLocationsForBundle;
            try {
                aclStockLocationsForBundle = aclBundleStockLocations.stream()
                        .filter(aclBundleStockLocation -> (aclBundleStockLocation.getBundleNo().equals(bundleNo)))
                        .collect(Collectors.toList());
            } catch (Exception e) {
                return null;
            }

            if ((aclStockLocationsForBundle != null)
                    && (!aclStockLocationsForBundle.isEmpty())) {
                List<ArticleStockLocation> articleStockLocationList = null;
                for (AclBundleStockLocation aclBundleStockLocation : aclStockLocationsForBundle) {
                    ArticleStockLocation articleStockLocation = new ArticleStockLocation(
                            aclBundleStockLocation.getBundleNo(),
                            StringUtils.leftPad(String.valueOf(aclBundleStockLocation.getAisleNo()),3, "0"),
                            StringUtils.leftPad(String.valueOf(aclBundleStockLocation.getBayNo()),3, "0"),
                            aclBundleStockLocation.getQuantity(),
                            aclBundleStockLocation.getBestBeforeDate(),
                            aclBundleStockLocation.getDateOfCreation(),
                            aclBundleStockLocation.getLastChangeDate(),
                            aclBundleStockLocation.getSerialShippingContainerCode());
                    if (articleStockLocationList == null) {
                        articleStockLocationList = new ArrayList<>(Collections.singletonList(articleStockLocation));
                    } else {
                        articleStockLocationList.add(articleStockLocation);
                    }
                }
                return articleStockLocationList;
            }
        }
        return null;
    }

    @Async
    public CompletableFuture<ArticleLocation> getArticleLocation(String countryCode, String salesLine, Integer storeNo, ArticleId articleId) {
        ArticleLocation articleLocation = aclStockService.getAclArticleLocations(
                countryCode, salesLine, storeNo,articleId);

        return CompletableFuture.completedFuture(transformAclArticleLocationsToInventoryArticleLocation(articleLocation));
    }

    private ArticleLocation transformAclArticleLocationsToInventoryArticleLocation(ArticleLocation articleLocation) {
        if (Optional.ofNullable(articleLocation).isPresent()) {
            if ((articleLocation.getStocktakingArticleLocationList() != null)
                    && (!articleLocation.getStocktakingArticleLocationList().isEmpty())) {
                for (StocktakingArticleLocation stocktakingArticleLocation : articleLocation.getStocktakingArticleLocationList()) {
                    stocktakingArticleLocation.setAisle(StringUtils.leftPad(stocktakingArticleLocation.getAisle(), 3, "0"));
                    stocktakingArticleLocation.setShelf(StringUtils.leftPad(stocktakingArticleLocation.getShelf(), 3, "0"));
                }
                return articleLocation;
            }
        }
        return null;
    }
}
