package net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.document.service;

import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.document.model.Documents;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.client.FilterAAAClient;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.model.ArticleFilterKey;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.model.Filter;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.model.SubFilterKey;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.service.FilterAAAService;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_aaa.ArticleCacheAAAClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class DocumentsServiceTest {
  @Mock private FilterAAAClient filterAAAClient;

  @Mock private FilterAAAService filterAAAService;

  @Mock private ArticleCacheAAAClient articleCacheClient;

  @InjectMocks private DocumentsService documentsService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testTranformDocumentInArticleId() {
    String document = "https://test.test?mmsartno=123&variantno=456&bundleno=789";
    ArticleId articleId = documentsService.tranformDocumentInArticleId(document);

    assertNotNull(articleId);
    assertEquals(123, articleId.getArticleNumber());
    assertEquals(456, articleId.getVariantNumber());
    assertEquals(789, articleId.getBundleNumber());
  }

  @Test
  void testGetDocumentsNextpage_one_page() throws Exception {
    Documents initialDocuments = new Documents();
    initialDocuments.setArticleKeys(
            new ArrayList<>() {{
              add("https://test.test?mmsartno=1&variantno=2&bundleno=3");
              add("https://test.test?mmsartno=4&variantno=5&bundleno=6");
            }});
    initialDocuments.setNextPageToken(null);

    Documents result = documentsService.getDocumentsNextpage(initialDocuments, new Object());

    assertNotNull(result);
    assertEquals(2, result.getArticleKeys().size());
    assertEquals(null, result.getNextPageToken());
    verify(filterAAAClient, times(0)).getFilteredDocumentsNextPage(any(), any());
  }

  @Test
  void testGetDocumentsNextpage_two_pages() throws Exception {
    Documents initialDocuments = new Documents();
    initialDocuments.setArticleKeys(
            new ArrayList<>() {{
              add("https://test.test?mmsartno=1&variantno=2&bundleno=3");
              add("https://test.test?mmsartno=4&variantno=5&bundleno=6");
            }});
    initialDocuments.setNextPageToken("token1");

    Documents nextPageDocuments = new Documents();
    nextPageDocuments.setArticleKeys(
            new ArrayList<>() {{
                add("https://test.test?mmsartno=123&variantno=456&bundleno=789");
                add("https://test.test?mmsartno=111&variantno=222&bundleno=333");
              }});
    nextPageDocuments.setNextPageToken(null);

    when(filterAAAClient.getFilteredDocumentsNextPage(any(), eq("token1")))
        .thenReturn(nextPageDocuments);

    Documents result = documentsService.getDocumentsNextpage(initialDocuments, new Object());

    assertNotNull(result);
    assertEquals(4, result.getArticleKeys().size());
    assertEquals(null, result.getNextPageToken());
    verify(filterAAAClient, times(1)).getFilteredDocumentsNextPage(any(), eq("token1"));
  }

  @Test
  void testGetDocumentsByArticleNumber() throws Exception {
    String countryCode = "RO";
    String salesLine = "MCC";
    long articleNumber = 123456;

    Documents documents = new Documents();
    documents.setArticleKeys(
            new ArrayList<>() {{
              add("https://test.test?mmsartno=1&variantno=2&bundleno=3");
              add("https://test.test?mmsartno=4&variantno=5&bundleno=6");
            }});
    documents.setNextPageToken(null);

    when(filterAAAClient.getFilteredDocuments(any(ArticleFilterKey.class))).thenReturn(documents);

    Documents result = documentsService.getDocumentsByArticleNumber(countryCode, salesLine, articleNumber);

    assertNotNull(result);
    assertEquals(2, result.getArticleKeys().size());
    verify(filterAAAClient, times(1)).getFilteredDocuments(any(ArticleFilterKey.class));
  }

  @Test
  void testGetDocumentsByArticleNumberVarianBundleId() throws Exception {
    String countryCode = "RO";
    String salesLine = "MCC";
    String languageCode = "FR";
    long articleNumber = 123456;
    long varNo = 1;
    long bundleNo = 1;

    Documents documents = new Documents();
    documents.setArticleKeys(
            new ArrayList<>() {{
              add("https://test.test?mmsartno=1&variantno=2&bundleno=3");
              add("https://test.test?mmsartno=4&variantno=5&bundleno=6");
            }});
    documents.setNextPageToken(null);

    when(filterAAAClient.getFilteredDocuments(any(ArticleFilterKey.class))).thenReturn(documents);

    Documents result = documentsService.getDocumentsByArticleNumberVarianBundleId(
            countryCode, salesLine, articleNumber, varNo, bundleNo);

    assertNotNull(result);
    assertEquals(2, result.getArticleKeys().size());
    verify(filterAAAClient, times(1)).getFilteredDocuments(any(ArticleFilterKey.class));
  }

}
