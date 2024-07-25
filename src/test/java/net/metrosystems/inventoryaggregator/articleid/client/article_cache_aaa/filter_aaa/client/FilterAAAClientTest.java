package net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.client;

import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.config.ConfigArticleCacheAAA;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.document.model.Documents;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.model.ArticleFilterKey;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.model.FilterIsuMaster;
import net.metrosystems.inventoryaggregator.config.RestTemplate.RestTemplateCustom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.URI;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class FilterAAAClientTest {

  @Mock private ConfigArticleCacheAAA configArticleCacheAAA;

  @Mock private RestTemplateCustom restTemplateCustom;

  private FilterAAAClient filterAAAClient;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    filterAAAClient = new FilterAAAClient(configArticleCacheAAA, restTemplateCustom);
  }

  @Test
  void getFilteredDocuments_shouldReturnFilteredDocuments() throws Exception {
    // Arrange
    ArticleFilterKey filter = new ArticleFilterKey();
    Documents expectedDocuments = new Documents();
    expectedDocuments.setArticleKeys(new ArrayList<>());
    expectedDocuments.getArticleKeys().add("key1");
    expectedDocuments.getArticleKeys().add("key2");

    when(configArticleCacheAAA.getHost()).thenReturn(new URI("http://test.test"));
    when(configArticleCacheAAA.getGeneral()).thenReturn("/articlekeys");
    when(configArticleCacheAAA.getGeneralQueryParam()).thenReturn("action=search&amount=200");
    when(restTemplateCustom.postForObject(anyString(), anyString(), eq(Documents.class), eq(true)))
        .thenReturn(expectedDocuments);

    // Act
    Documents documents = filterAAAClient.getFilteredDocuments(filter);

    // Assert
    assertNotNull(documents);
    assertEquals(2, documents.getArticleKeys().size());
    assertTrue(documents.getArticleKeys().contains("key1"));
    assertTrue(documents.getArticleKeys().contains("key2"));

    verify(configArticleCacheAAA).getHost();
    verify(configArticleCacheAAA).getGeneral();
    verify(configArticleCacheAAA).getGeneralQueryParam();
    verify(restTemplateCustom)
        .postForObject(anyString(), anyString(), eq(Documents.class), eq(true));
    verifyNoMoreInteractions(configArticleCacheAAA, restTemplateCustom);
  }

  @Test
  void getFilteredDocumentsForIsuMaster_shouldReturnFilteredDocuments() throws Exception {
    // Arrange
    FilterIsuMaster filterIsuMaster = new FilterIsuMaster();
    Documents expectedDocuments = new Documents();
    expectedDocuments.setArticleKeys(new ArrayList<>());
    expectedDocuments.getArticleKeys().add("key1");
    expectedDocuments.getArticleKeys().add("key2");

    when(configArticleCacheAAA.getHost()).thenReturn(new URI("http://test.test"));
    when(configArticleCacheAAA.getGeneral()).thenReturn("/articlekeys");
    when(configArticleCacheAAA.getGeneralQueryParam()).thenReturn("action=search&amount=200");
    when(restTemplateCustom.postForObject(anyString(), anyString(), eq(Documents.class), eq(true)))
        .thenReturn(expectedDocuments);

    // Act
    Documents documents = filterAAAClient.getFilteredDocumentsForIsuMaster(filterIsuMaster);

    // Assert
    assertNotNull(documents);
    assertEquals(2, documents.getArticleKeys().size());
    assertTrue(documents.getArticleKeys().contains("key1"));
    assertTrue(documents.getArticleKeys().contains("key2"));

    verify(configArticleCacheAAA).getHost();
    verify(configArticleCacheAAA).getGeneral();
    verify(configArticleCacheAAA).getGeneralQueryParam();
    verify(restTemplateCustom)
        .postForObject(anyString(), anyString(), eq(Documents.class), eq(true));
    verifyNoMoreInteractions(configArticleCacheAAA, restTemplateCustom);
  }

  @Test
  void getFilteredDocumentsNextPage_shouldReturnFilteredDocuments() throws Exception {
    // Arrange
    ArticleFilterKey filter = new ArticleFilterKey();
    String nextPage = "2";
    Documents expectedDocuments = new Documents();
    expectedDocuments.setArticleKeys(new ArrayList<>());
    expectedDocuments.getArticleKeys().add("key1");
    expectedDocuments.getArticleKeys().add("key2");

    when(configArticleCacheAAA.getHost()).thenReturn(new URI("http://test.test"));
    when(configArticleCacheAAA.getGeneral()).thenReturn("/articlekeys");
    when(configArticleCacheAAA.getGeneralQueryParam()).thenReturn("action=search&amount=200");
    when(configArticleCacheAAA.getFrom()).thenReturn("fromPage=");
    when(restTemplateCustom.postForObject(anyString(), anyString(), eq(Documents.class), eq(true)))
        .thenReturn(expectedDocuments);

    // Act
    Documents documents = filterAAAClient.getFilteredDocumentsNextPage(filter, nextPage);

    // Assert
    assertNotNull(documents);
    assertEquals(2, documents.getArticleKeys().size());
    assertTrue(documents.getArticleKeys().contains("key1"));
    assertTrue(documents.getArticleKeys().contains("key2"));

    verify(configArticleCacheAAA).getHost();
    verify(configArticleCacheAAA).getGeneral();
    verify(configArticleCacheAAA).getGeneralQueryParam();
    verify(configArticleCacheAAA).getFrom();
    verify(restTemplateCustom)
        .postForObject(anyString(), anyString(), eq(Documents.class), eq(true));
    verifyNoMoreInteractions(configArticleCacheAAA, restTemplateCustom);
  }
}
