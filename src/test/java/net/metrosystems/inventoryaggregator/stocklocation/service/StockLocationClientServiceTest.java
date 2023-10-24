package net.metrosystems.inventoryaggregator.stocklocation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.List;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.common.utils.JsonHelper;
import net.metrosystems.inventoryaggregator.stocklocation.model.ArticleStockLocations;
import net.metrosystems.inventoryaggregator.stocklocation.model.StockLocation;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

class StockLocationClientServiceTest {
  private StockLocationClientService stockLocationClientService;
  private MockWebServer mockWebServer;
  private JsonHelper jsonHelper;

  @BeforeEach
  void setUp() throws IOException {
    this.mockWebServer = new MockWebServer();
    this.mockWebServer.start();
    this.jsonHelper = new JsonHelper();
    String baseUrl = this.mockWebServer.url("/").toString();
    stockLocationClientService =
        new StockLocationClientService(
            WebClient.builder().baseUrl(baseUrl).build(),
            baseUrl + "aggregator",
            "/mstoreacl/api/v0/articlelocation/countrycode/{countryCode}/storenumber/{storeNumber}",
            "/mstoreacl/api/v0/articlelocation/countrycode/{countryCode}/storenumber/{storeNumber}"
                + "/articlenumber/{articleNumber}/variantnumber/{variantNumber}/bundlenumber/{bundleNumber}");
  }

  @AfterEach
  void reset() throws IOException {
    this.mockWebServer.shutdown();
  }

  @Test
  void getArticleStockLocations_returnValue() throws JsonProcessingException {
    String countryCode = "DE";
    Integer storeNumber = 1;

    mockWebServer.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody(jsonHelper.toJson(buildAticleIDStockLocationResponse()))
            .addHeader("Content-Type", "application/json"));
    List<StockLocation> locations =
        stockLocationClientService.getArticleStockLocation(
            buildArticleId(), countryCode, storeNumber);
    assertEquals(20, locations.get(0).getQuantity());
    assertEquals("asdcdsgasdscasc", locations.get(0).getMasterSscc());
    assertEquals("123", locations.get(0).getBayNo());
    assertEquals("456", locations.get(0).getAisleNo());
  }

  @Test
  void getArticleStockLocationForArticleList_returnValue() throws JsonProcessingException {
    String countryCode = "DE";
    Integer storeNumber = 1;
    mockWebServer.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody(jsonHelper.toJson(List.of(buildAticleIDStockLocationResponse())))
            .addHeader("Content-Type", "application/json"));
    List<ArticleStockLocations> articleStockLocationForArticleList =
        stockLocationClientService.getArticleStockLocationForArticleList(
            List.of(buildArticleId()), countryCode, storeNumber);
    StockLocation stockLocation =
        articleStockLocationForArticleList.get(0).getStockLocations().get(0);
    assertEquals(20, stockLocation.getQuantity());
    assertEquals("asdcdsgasdscasc", stockLocation.getMasterSscc());
    assertEquals("123", stockLocation.getBayNo());
    assertEquals("456", stockLocation.getAisleNo());
  }

  private StockLocation buildAticleStockLocationResponse() {
    return StockLocation.builder()
        .storeLocation("storage")
        .quantity(20)
        .bayNo("123")
        .aisleNo("456")
        .masterSscc("asdcdsgasdscasc")
        .creationDate("22-01-2015 10:15:55")
        .changeDate("22-01-2015 10:15:55")
        .build();
  }

  private ArticleStockLocations buildAticleIDStockLocationResponse() {
    return ArticleStockLocations.builder()
        .articleId(buildArticleId())
        .stockLocations(List.of(buildAticleStockLocationResponse()))
        .build();
  }

  private ArticleId buildArticleId() {
    return ArticleId.builder().articleNumber(123456L).bundleNumber(1L).variantNumber(2L).build();
  }
}
