package net.metrosystems.inventoryaggregator.movieacl.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.StockData;
import net.metrosystems.inventoryaggregator.common.movieacl.client.MovieAclClient;
import net.metrosystems.inventoryaggregator.common.utils.JsonHelper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MovieAclClientTest {
    private MovieAclClient movieAclClient;
    private MockWebServer mockWebServer;
    private JsonHelper jsonHelper;

    @BeforeEach
    void setUp() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start();
        this.jsonHelper = new JsonHelper();
        String baseUrl = this.mockWebServer.url("/").toString();
        movieAclClient = new MovieAclClient(baseUrl,
                "/movieacl/api/v1/stock-data/countrycode/{countryCode}/salesline/{salesLine}/storenumber/{storeNumber}",
                "/movieacl/api/v1/average-sales/countrycode/{countryCode}/salesline/{salesLine}/storenumber/{storeNumber}/articlenumber/{articleNumber}/variantnumber/{variantNumber}/bundlenumber/{bundleNumber}",
                WebClient.builder().baseUrl(baseUrl).build());

    }

    @AfterEach
    void reset() throws IOException {
        this.mockWebServer.shutdown();
    }

    @Test
    void getStockDataList_returnValue() throws JsonProcessingException {
        String countryCode = "DE";
        String salesLine = "01";
        Integer storeNumber = 1;

        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(jsonHelper.toJson(List.of(buildStockDataResponse()))).addHeader("Content-Type", "application/json"));

        List<StockData> stockDataList = movieAclClient.getStockDataList(List.of(buildArticleId()), countryCode, salesLine, storeNumber);

        assertNotNull(stockDataList);
        assertEquals(1, stockDataList.size());
        assertEquals(buildArticleId().getArticleNumber(), stockDataList.get(0).getArticleId().getArticleNumber());
        assertEquals(buildArticleId().getVariantNumber(), stockDataList.get(0).getArticleId().getVariantNumber());
        assertEquals(buildArticleId().getBundleNumber(), stockDataList.get(0).getArticleId().getBundleNumber());
    }

    @Test
    void getAverageSales_returnValue() {
        String countryCode = "DE";
        String salesLine = "01";
        Integer storeNumber = 1;
        String expectedAverageSales = "15.5";

        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(expectedAverageSales).addHeader("Content-Type", "application/json"));

        String averageSales = movieAclClient.getAverageSales(buildArticleId(), countryCode, salesLine, storeNumber);

        assertNotNull(averageSales);
        assertEquals(expectedAverageSales, averageSales);
    }

    private StockData buildStockDataResponse() {
        StockData stockData = new StockData();
        stockData.setArticleId(buildArticleId());
        return stockData;
    }

    private ArticleId buildArticleId() {
        return ArticleId.builder().articleNumber(123456L).bundleNumber(1L).variantNumber(2L).build();
    }
}

