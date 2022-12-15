package net.metrosystems.inventoryaggregator.articleattributes.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.PromotionArticleData;
import net.metrosystems.inventoryaggregator.articleattributes.repository.ArticleMasterDataRepository;
import net.metrosystems.inventoryaggregator.common.model.StoreId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.util.CollectionUtils;

class PromotionServiceTest {

  @Mock ArticleMasterDataRepository articleMasterDataRepository;

  @InjectMocks PromotionService promotionService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getFuturePromotionArticleDataForArticleId() {
    String countryCode = "RO";
    Long articleNumber = 1L;
    Long variantNumber = 2L;
    Long bundleNumber = 3L;
    String salesLine = "MCC";
    int storeNumber = 11;
    ArticleId articleId =
        ArticleId.builder()
            .articleNumber(articleNumber)
            .variantNumber(variantNumber)
            .bundleNumber(bundleNumber)
            .build();
    StoreId storeId =
        StoreId.builder()
            .countryCode(countryCode)
            .salesLine(salesLine)
            .storeNumber(storeNumber)
            .build();
    when(articleMasterDataRepository.getPromotionArticle(
            storeId.getCountryCode(), storeId.getStoreNumber(), articleId))
        .thenReturn(null);

    List<PromotionArticleData> promotionArticleDataForArticleId =
        promotionService.getFuturePromotionArticleDataForArticleId(articleId, storeId);
    assertTrue(CollectionUtils.isEmpty(promotionArticleDataForArticleId));

    LocalDate now = LocalDate.now();

    PromotionArticleData promotionArticleData =
        PromotionArticleData.builder().promotionId("123").valid(false).build();
    PromotionArticleData promotionArticleData2 =
        PromotionArticleData.builder()
            .promotionId("321")
            .valid(true)
            .build();
    List<PromotionArticleData> promotionArticleDataList =
        List.of(promotionArticleData, promotionArticleData2);
    when(articleMasterDataRepository.getPromotionArticle(
            storeId.getCountryCode(), storeId.getStoreNumber(), articleId))
        .thenReturn(promotionArticleDataList);

    List<PromotionArticleData> promotionArticleDataForArticleId2 =
        promotionService.getFuturePromotionArticleDataForArticleId(articleId, storeId);
    assertFalse(CollectionUtils.isEmpty(promotionArticleDataForArticleId2));
    assertEquals(1, promotionArticleDataForArticleId2.size());
    assertTrue( promotionArticleDataForArticleId2.contains(promotionArticleData2));
  }
}
