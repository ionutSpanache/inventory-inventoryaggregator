package net.metrosystems.inventoryaggregator.articlestoremasterdata.client;

import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articlestoremasterdata.model.AclStoreMasterData;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

class AclArticleStoreMasterDataClientTest {
  private AclArticleStoreMasterDataClient aclArticleStoreMasterDataClient;
  private MockWebServer mockWebServer;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();

    String baseUrl = mockWebServer.url("/").toString();
    WebClient webClient = WebClient.builder().baseUrl(baseUrl).build();
    objectMapper = new ObjectMapper();

    aclArticleStoreMasterDataClient =
        new AclArticleStoreMasterDataClient(
            webClient, baseUrl, "/api/v1/acl-article-store-master-data");
  }

  @AfterEach
  void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  @Test
  void getArticleStoreMasterDataByArtVarBundle() {

    AclStoreMasterData aclStoreMasterData =
        new AclStoreMasterData().setArticleNumber(1).setDepartmentNumber(23);
    List<AclStoreMasterData> aclStoreMasterDataList = List.of(aclStoreMasterData);

    mockWebServer.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setBody(objectMapper.writeValueAsString(aclStoreMasterDataList))
            .addHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE));

    List<AclStoreMasterData> articleStoreMasterDataByArtVarBundle =
        aclArticleStoreMasterDataClient.getArticleStoreMasterDataByArtVarBundle(
            "DE", "MCC", 15, List.of(new ArticleId(1L, 1L, 1L)));

    assertEquals(aclStoreMasterDataList, articleStoreMasterDataByArtVarBundle);
  }

  @Test
  void getArticleStoreMasterDataByArtVarBundle_error() {

    mockWebServer.enqueue(new MockResponse().setResponseCode(500));

    List<AclStoreMasterData> articleStoreMasterDataByArtVarBundle =
        aclArticleStoreMasterDataClient.getArticleStoreMasterDataByArtVarBundle(
            "DE", "MCC", 15, List.of(new ArticleId(1L, 1L, 1L)));

    assertTrue(CollectionUtils.isEmpty(articleStoreMasterDataByArtVarBundle));
  }
}
