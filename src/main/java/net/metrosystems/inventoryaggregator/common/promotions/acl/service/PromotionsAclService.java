package net.metrosystems.inventoryaggregator.common.promotions.acl.service;

import net.metrosystems.inventoryaggregator.articleid.model.ArticleId;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheIhdsTree;
import net.metrosystems.inventoryaggregator.common.model.StoreId;
import net.metrosystems.inventoryaggregator.common.promotions.acl.client.PromotionsAclClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromotionsAclService {

  private final PromotionsAclClient promotionsAclClient;

  @Autowired
  public PromotionsAclService(PromotionsAclClient promotionsAclClient) {
    this.promotionsAclClient = promotionsAclClient;
  }

  public String getActivePromotion(
      ArticleCacheIhdsTree articleCacheDocument, List<String> promotionIdList, Integer storeNo) {
    StoreId storeId =
        StoreId.builder()
            .countryCode(
                articleCacheDocument
                    .getTransaction()
                    .getArticleCacheIncident()
                    .getArticleCacheDomain()
                    .getPlkz())
            .salesLine("MCC")
            .storeNumber(storeNo)
            .build();
    ArticleId articleId =
        ArticleId.builder()
            .articleNumber(
                articleCacheDocument
                    .getTransaction()
                    .getArticleCacheIncident()
                    .getArticleCacheReaderDigest()
                    .getMmsArtNo())
            .variantNumber(
                articleCacheDocument
                    .getTransaction()
                    .getArticleCacheIncident()
                    .getArticleCacheReaderDigest()
                    .getVariantNo())
            .bundleNumber(
                articleCacheDocument
                    .getTransaction()
                    .getArticleCacheIncident()
                    .getArticleCacheReaderDigest()
                    .getBundleNo())
            .build();

    return promotionIdList.stream()
        .filter(
            promotionId ->
                Boolean.TRUE.equals(
                    promotionsAclClient.getActivePromotion(storeId, articleId, promotionId)))
        .findFirst()
        .orElse(null);
  }
}
