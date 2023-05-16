package net.metrosystems.inventoryaggregator.articleattributes;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.controller.ArticleListDataController;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.PromotionArticleData;
import net.metrosystems.inventoryaggregator.articleattributes.model.PromotionArticleDataForArticleId;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.service.ArticleCacheService;
import net.metrosystems.inventoryaggregator.common.model.StoreId;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@SpringBootTest
class ArticleListDataControllerTest {
  private final StoreId storeId =
      StoreId.builder().countryCode("FR").salesLine("MCC").storeNumber(16).build();
  private final ArticleId existingArticle =
      ArticleId.builder().articleNumber(847843L).variantNumber(1L).bundleNumber(2L).build();
  private final ArticleId notExistingArticle =
      ArticleId.builder().articleNumber(847843111L).variantNumber(1L).bundleNumber(2L).build();

  @MockBean private ArticleCacheService articleCacheService;

  @Autowired private ArticleListDataController articleListDataController;

  private String getFuturePromotionsPath(
      String countryCode, String salesline, Integer storeNumber) {
    URI url =
        UriComponentsBuilder.newInstance()
            .path(
                "/inventoryaggregator/country/{country-code}/salesline/{salesline}/storenumber/{store-number}/articles/future-promotions")
            .build(countryCode, salesline, String.valueOf(storeNumber));
    return url.toString();
  }

  @Test
  void shouldReturnFuturePromotionsForExistingArticle() {
    // Given
    final List<PromotionArticleData> promotions =
        Arrays.asList(
            PromotionArticleData.builder()
                .promotionId("11221")
                .promotionStart(LocalDate.now())
                .promotionEnd(LocalDate.now())
                .promotionNetSalesPricePerPiece(112d)
                .promotionNetSalesPricePerBundle(1121d)
                .valid(true)
                .build(),
            PromotionArticleData.builder()
                .promotionId("112212")
                .promotionStart(LocalDate.now())
                .promotionEnd(LocalDate.now())
                .promotionNetSalesPricePerPiece(112d)
                .promotionNetSalesPricePerBundle(1121d)
                .valid(false)
                .build());
    final List<PromotionArticleData> expectedPromotios =
        Arrays.asList(
            PromotionArticleData.builder()
                .promotionId("11221")
                .promotionStart(LocalDate.now())
                .promotionEnd(LocalDate.now())
                .promotionNetSalesPricePerPiece(112d)
                .promotionNetSalesPricePerBundle(1121d)
                .valid(true)
                .build());

    Mockito.when(
            articleCacheService.getPromotionArticleData(
                storeId.getCountryCode(), existingArticle, storeId.getStoreNumber()))
        .thenReturn(promotions);

    // When
    var response =
        WebTestClient.bindToController(articleListDataController)
            .build()
            .post()
            .uri(
                getFuturePromotionsPath(
                    storeId.getCountryCode(), storeId.getSalesLine(), storeId.getStoreNumber()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(Arrays.asList(existingArticle)))
            // Then
            .exchange()
            .expectStatus()
            .isOk()
            .expectBodyList(PromotionArticleDataForArticleId.class)
            .returnResult()
            .getResponseBody();
    assertThat(response).hasSize(1);
    var data = response.get(0);
    assertThat(data.getArticleId()).isEqualTo(existingArticle);
    assertThat(data.getPromotionArticleData()).isEqualTo(expectedPromotios);
  }

  @Test
  void shouldReturnEmptyPromotionsListForNotExistingArticle() {
    // Given
    final List<PromotionArticleData> promotions = Collections.emptyList();
    Mockito.when(
            articleCacheService.getPromotionArticleData(
                storeId.getCountryCode(), notExistingArticle, storeId.getStoreNumber()))
        .thenReturn(promotions);

    // When
    var response =
        WebTestClient.bindToController(articleListDataController)
            .build()
            .post()
            .uri(
                getFuturePromotionsPath(
                    storeId.getCountryCode(), storeId.getSalesLine(), storeId.getStoreNumber()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(Arrays.asList(existingArticle)))
            // Then
            .exchange()
            .expectStatus()
            .isOk()
            .expectBodyList(PromotionArticleDataForArticleId.class)
            .returnResult()
            .getResponseBody();
    assertThat(response).hasSize(1);
    var data = response.get(0);
    assertThat(data.getArticleId()).isEqualTo(existingArticle);
    assertThat(data.getPromotionArticleData()).isEmpty();
  }

  @Test
  void shouldReturnEmptyListForNoArticle() {
    // When
    var response =
        WebTestClient.bindToController(articleListDataController)
            .build()
            .post()
            .uri(
                getFuturePromotionsPath(
                    storeId.getCountryCode(), storeId.getSalesLine(), storeId.getStoreNumber()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(Collections.emptyList()))
            // Then
            .exchange()
            .expectStatus()
            .isOk()
            .expectBodyList(PromotionArticleDataForArticleId.class)
            .returnResult()
            .getResponseBody();
    assertThat(response).isEmpty();
  }
}
