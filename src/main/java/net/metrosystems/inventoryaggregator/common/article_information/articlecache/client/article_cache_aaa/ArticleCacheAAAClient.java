package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_aaa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.ArticleCacheClient;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheIhdsTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class ArticleCacheAAAClient implements ArticleCacheClient {


  @Value("${ARTICLE_CACHE_SERVICE_HOST}")
  private String articleCacheServiceHost;

  private final RestTemplate restTemplateArticleCache;

  @Autowired
  public ArticleCacheAAAClient(
      @Qualifier("RestTemplate-ArticleCache") RestTemplate restTemplateArticleCache) {
    this.restTemplateArticleCache = restTemplateArticleCache;
    restTemplateArticleCache.setMessageConverters(prepareArticleCacheDocumentMessageConverters());
  }

  private List<HttpMessageConverter<?>> prepareArticleCacheDocumentMessageConverters() {

    Jaxb2RootElementHttpMessageConverter converter = new Jaxb2RootElementHttpMessageConverter();
    converter.setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_XML));

    List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
    messageConverters.add(converter);
    return messageConverters;
  }
  

  @Override
  public ArticleCacheIhdsTree getArticleCacheDocumentByURL(String articleUrlPath) {
    return getForArticleCacheObject(articleUrlPath, ArticleCacheIhdsTree.class);
  }

  private <T> T getForArticleCacheObject(String articleUrl, Class<T> responseType) {
    try {
      UriComponents uri =
          UriComponentsBuilder.fromUriString(articleCacheServiceHost).path(articleUrl).build();

      return restTemplateArticleCache.getForObject(uri.toUriString(), responseType);
    } catch (Exception e) {
      log.debug(
          "ArticleCacheAAAClient.getForArticleCacheObject - error on GET api call: message {}",
          e.getMessage(),
          e);
      throw e;
    }
  }
}
