package inventory.aggregator.common.article_information.articlecache.client.article_cache_iappl;

import inventory.aggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheIhdsTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * Old service reading from OLD Article cache api IAPPL
 */
@Component
public class ArticleCacheIapplClient //implements ArticleCacheClient
{

    @Value("${ARTICLE_CACHE_SERVICE_HOST}")
    private String articleCacheServiceHost;

    @Value("${ARTICLE_CACHE_FILTER_PATH}")
    private String articleCacheFilterPath;

    private RestTemplate restTemplateDocuments;
    private RestTemplate restTemplateFilter;

    @Autowired
    public ArticleCacheIapplClient(RestTemplateBuilder restTemplateBuilder,
                                   @Value("${ARTICLE_CACHE_USERNAME}") final String username,
                                   @Value("${ARTICLE_CACHE_PASSWORD}") final String password) {
        this.restTemplateDocuments = restTemplateBuilder
                .basicAuthorization(username, password)
                .messageConverters(prepareArticleCacheDocumentMessageConverters())
                .build();
        this.restTemplateFilter = restTemplateBuilder
                .basicAuthorization(username, password)
                .messageConverters(prepareArticleCacheFilterMessageConverters())
                .build();
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

    public String getArticleUrlPathByEan(String ean, String countryCode, String languageCode) {
        return getForArticleCacheURL(
                String.format(
                        articleCacheServiceHost + articleCacheFilterPath + "?salesBarcode=%s" + "&language=%s",
                        countryCode.toUpperCase(), ean, languageCode.toUpperCase()));

    }

    public String getArticleUrlPathByMmsArtNo(Long mmsArtNo, String countryCode, String languageCode) {
        return getForArticleCacheURL(
                String.format(
                        articleCacheServiceHost + articleCacheFilterPath + "?mmsArtNo=%d" + "&language=%s",
                        countryCode.toUpperCase(), mmsArtNo, languageCode.toUpperCase()));
    }

    public String getArticleUrlPathByArtVarBundle(Long articleNumber, Long variantNumber, Long bundleNumber,
                                                  String countryCode, String languageCode) {
        return getForArticleCacheURL(
                String.format(
                        articleCacheServiceHost + articleCacheFilterPath + "?mmsArtNo=%d" + "&variantNo=%d" + "&bundleNo=%d" + "&language=%s",
                        countryCode.toUpperCase(), articleNumber, variantNumber, bundleNumber, languageCode.toUpperCase()));
    }

    public String getArticleUrlPathBySubsysArtNo(Long subsysArtNo, String countryCode, String languageCode) {
        return getForArticleCacheURL(
                String.format(
                        articleCacheServiceHost + articleCacheFilterPath + "?subsysNo=%d" + "&language=%s",
                        countryCode.toUpperCase(), subsysArtNo, languageCode.toUpperCase()));
    }

    public List<String> findArticleUrlPathByMmsArtNo(Long mmsArtNo, String countryCode, String languageCode) {
        String[] articleCacheDocumentURLs = getForArticleCacheURL(

                String.format(
                        articleCacheServiceHost + articleCacheFilterPath + "?mmsArtNo=%s" + "&language=%s",
                        countryCode.toUpperCase(), mmsArtNo, languageCode.toUpperCase()))
                .split("\\r?\\n");
        return Arrays.asList(articleCacheDocumentURLs);
    }

    public ArticleCacheIhdsTree getArticleCacheDocumentByURL(String articleUrlPath) {

        return getForArticleCacheObject(
                articleCacheServiceHost + "/" + articleUrlPath,
                ArticleCacheIhdsTree.class
        );

    }

    private <T> T getForArticleCacheObject(String articleUrl, Class<T> responseType) {
        return restTemplateDocuments.getForObject(
                articleUrl,
                responseType);
    }

    private String getForArticleCacheURL(String articleUrl) {
        System.out.println("ArticleCacheIapplClient.getForArticleCacheURL URL: " + articleUrl);
        return restTemplateFilter.getForObject(
                articleUrl,
                String.class);
    }
}
