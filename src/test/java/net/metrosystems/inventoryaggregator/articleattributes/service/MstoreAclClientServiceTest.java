package net.metrosystems.inventoryaggregator.articleattributes.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleStockLocation;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.ArticleIdStockLocation;
import net.metrosystems.inventoryaggregator.common.utils.JsonHelper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

class MstoreAclClientServiceTest {

  private WebClient webClient;
  private MstoreAclClientService mstoreAclClientService;
  private MockWebServer mockWebServer;
  private JsonHelper jsonHelper;

  @BeforeEach
  void setUp() throws IOException {
    this.mockWebServer = new MockWebServer();
    this.mockWebServer.start();
    this.jsonHelper = new JsonHelper();
    String baseUrl = this.mockWebServer.url("/").toString();
    mstoreAclClientService =
        new MstoreAclClientService(WebClient.builder().baseUrl(baseUrl).build(), baseUrl + "aggregator",
            "/mstoreacl/api/v0/articlelocation/countrycode/{countryCode}/storenumber/{storeNumber}",
            "/mstoreacl/api/v0/articlelocation/countrycode/{countryCode}/storenumber/{storeNumber}"
                + "/articlenumber/{articleNumber}/variantnumber/{variantNumber}/bundlenumber/{bundleNumber}");
  }

  @AfterEach
  void reset() throws IOException {
    this.mockWebServer.shutdown();
  }

  @Test
  void getArticleStockLocations_returnValue() throws JsonProcessingException, ExecutionException, InterruptedException {
    String countryCode = "DE";
    Integer storeNumber = 1;

    mockWebServer.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody(jsonHelper.toJson(buildAticleIDStockLocationResponse()))
            .addHeader("Content-Type", "application/json"));
    List<ArticleStockLocation> locations =
        mstoreAclClientService.getArticleStockLocation(
            buildArticleId(), countryCode, storeNumber);
    assertEquals(locations.get(0).getQuantity(), 20);
    assertEquals(locations.get(0).getMasterSscc(), "asdcdsgasdscasc");
    assertEquals(locations.get(0).getBayNo(), "123");
    assertEquals(locations.get(0).getAisleNo(), "456");
  }

  @Test
  void getArticleStockLocationForArticleList_returnValue()
      throws JsonProcessingException, ExecutionException, InterruptedException {
    String countryCode = "DE";
    Integer storeNumber = 1;
    mockWebServer.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody(jsonHelper.toJson(List.of(buildAticleIDStockLocationResponse())))
            .addHeader("Content-Type", "application/json"));
    List<ArticleIdStockLocation> articleStockLocationForArticleList =
        mstoreAclClientService.getArticleStockLocationForArticleList(
            List.of(buildArticleId()), countryCode, storeNumber);
    assertEquals(articleStockLocationForArticleList.get(0).getArticleStockLocationList().get(0).getQuantity(), 20);
    assertEquals(articleStockLocationForArticleList.get(0).getArticleStockLocationList().get(0).getMasterSscc(),
        "asdcdsgasdscasc");
    assertEquals(articleStockLocationForArticleList.get(0).getArticleStockLocationList().get(0).getBayNo(), "123");
    assertEquals(articleStockLocationForArticleList.get(0).getArticleStockLocationList().get(0).getAisleNo(), "456");
  }

  private ArticleStockLocation buildAticleStockLocationResponse() {
    return ArticleStockLocation.builder()
        .storeLocation("storage")
        .quantity(20)
        .bayNo("123")
        .aisleNo("456")
        .masterSscc("asdcdsgasdscasc")
        .creationDate("22-01-2015 10:15:55")
        .changeDate("22-01-2015 10:15:55")
        .build();
  }

  private ArticleIdStockLocation buildAticleIDStockLocationResponse() {
    return ArticleIdStockLocation.builder()
        .articleId(buildArticleId())
        .articleStockLocationList(List.of(buildAticleStockLocationResponse()))
        .build();
  }

  private ArticleId buildArticleId() {
    return ArticleId.builder().articleNumber(123456L).bundleNumber(1L).variantNumber(2L).build();
  }
}
