package inventory.aggregator.rawarticledata.repository;

import inventory.aggregator.common.stock_location.acl.model.AclBundleStockLocation;
import inventory.aggregator.common.stock_location.acl.service.AclStockService;
import inventory.aggregator.rawarticledata.model.ArticleId;
import inventory.aggregator.rawarticledata.model.ArticleStockLocation;
import inventory.aggregator.rawarticledata.model.articlelocation.ArticleLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
                            aclBundleStockLocation.getAisleNo(),
                            aclBundleStockLocation.getBayNo(),
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

        return CompletableFuture.completedFuture(articleLocation);
    }
}
