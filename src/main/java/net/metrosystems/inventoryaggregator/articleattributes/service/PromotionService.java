package net.metrosystems.inventoryaggregator.articleattributes.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.PromotionArticleData;
import net.metrosystems.inventoryaggregator.articleattributes.repository.ArticleMasterDataRepository;
import net.metrosystems.inventoryaggregator.common.model.StoreId;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@Slf4j
public class PromotionService {

  private final ArticleMasterDataRepository articleMasterDataRepository;

  public PromotionService(ArticleMasterDataRepository articleMasterDataRepository) {
    this.articleMasterDataRepository = articleMasterDataRepository;
  }

  public List<PromotionArticleData> getFuturePromotionArticleDataForArticleId(
      ArticleId articleId, StoreId storeId) {

    List<PromotionArticleData> promotionArticle =
        articleMasterDataRepository.getPromotionArticle(
            storeId.getCountryCode(), storeId.getStoreNumber(), articleId);
    if (CollectionUtils.isEmpty(promotionArticle)) {
      return Collections.emptyList();
    }
    return promotionArticle.stream().filter(t -> Boolean.TRUE == t.getValid()).toList();
  }
}
