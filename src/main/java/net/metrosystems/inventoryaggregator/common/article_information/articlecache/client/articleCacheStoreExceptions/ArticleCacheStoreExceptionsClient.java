package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.articleCacheStoreExceptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.articleCacheStoreExceptions.model.ArticleCacheStoreExceptionsIhdsTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ArticleCacheStoreExceptionsClient //implements ArticleCacheClient
{

    @Value("${ARTICLE_CACHE_SERVICE_HOST}")
    private String articleCacheServiceHost;

    private final RestTemplate restTemplateArticleCache;

    @Autowired
    public ArticleCacheStoreExceptionsClient(@Qualifier("RestTemplate-ArticleCache") RestTemplate restTemplateArticleCache) {
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

    private List<HttpMessageConverter<?>> prepareArticleCacheFilterMessageConverters() {

        StringHttpMessageConverter converter = new StringHttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_PLAIN));

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(converter);
        return messageConverters;
    }

    public ArticleCacheStoreExceptionsIhdsTree getArticleCacheDocumentByURL(String articleUrlPath) {
        return getForArticleCacheObject(
                articleCacheServiceHost + "/" + articleUrlPath,
                ArticleCacheStoreExceptionsIhdsTree.class
        );
    }

    private <T> T getForArticleCacheObject(String articleUrl, Class<T> responseType) {
        try {
            return restTemplateArticleCache.getForObject(articleUrl, responseType);
        }catch(Exception e) {
            return null;
        }
    }
}
