package net.metrosystems.inventoryaggregator.articleattributes.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.List;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.PromotionArticleData;
import net.metrosystems.inventoryaggregator.articleattributes.service.PromotionService;
import net.metrosystems.inventoryaggregator.common.model.StoreId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest
class PromotionDataControllerTest {

  @Autowired PromotionDataController promotionDataController;

  @MockBean private PromotionService promotionService;


  @Test
  void getSupplierInformation() {
    String uri =
        "/inventoryaggregator/promotion/countryCode/{countryCode}/salesLine/{salesLine}/storeNumber/{storeNumber}/articleNumber/{articleNumber}/variantNumber/{variantNumber}/bundleNumber/{bundleNumber}";

    String countryCode = "RO";
    Long articleNumber = 1L;
    Long variantNumber = 2L;
    Long bundleNumber = 3L;
    String salesLine = "MCC";
    int storeNumber = 11;
    UriComponents url =
        UriComponentsBuilder.fromUriString(uri)
            .buildAndExpand(
                countryCode, salesLine, storeNumber, articleNumber, variantNumber, bundleNumber);

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
    List<PromotionArticleData> promotionArticleDataListExpected =
        List.of(PromotionArticleData.builder().promotionId("1234").build());
    when(promotionService.getFuturePromotionArticleDataForArticleId(articleId, storeId)).thenReturn(promotionArticleDataListExpected);

    List<PromotionArticleData> promotionArticleDataList =
        WebTestClient.bindToController(promotionDataController)
            .build()
            .get()
            .uri(url.toString())
            .exchange()
            .expectStatus()
            .isOk()
            .expectBodyList(PromotionArticleData.class)
            .returnResult()
            .getResponseBody();
    assertFalse(isEmpty(promotionArticleDataList));
    assertEquals(promotionArticleDataListExpected, promotionArticleDataList);
  }
}
