package net.metrosystems.inventoryaggregator.common.promotions.acl.service;

import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.PromotionArticleData;
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

  public PromotionArticleData getActivePromotion(
      String countryCode, Integer storeNo,ArticleId articleId, List<PromotionArticleData> promotionArticleDataList) {
    StoreId storeId =
        StoreId.builder()
            .countryCode(
                countryCode)
            .salesLine("MCC")
            .storeNumber(storeNo)
            .build();

    return promotionArticleDataList.stream()
        .filter(
            promotionArticleData ->
                Boolean.TRUE.equals(
                    promotionsAclClient.getActivePromotion(storeId, articleId, promotionArticleData.getPromotionId())))
        .findFirst()
        .orElse(PromotionArticleData.builder().build());
  }
}
