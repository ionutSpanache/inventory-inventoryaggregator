package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_aaa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.metrosystems.inventoryaggregator.articleId.client.article_cache_aaa.document.model.Documents;
import net.metrosystems.inventoryaggregator.articleId.client.article_cache_aaa.document.service.DocumentsService;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.ArticleCacheClient;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheIhdsTree;
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
public class ArticleCacheAAAClient implements ArticleCacheClient {

    @Autowired
    DocumentsService documentsService;

    @Value("${ARTICLE_CACHE_SERVICE_HOST}")
    private String articleCacheServiceHost;
    @Value("${ARTICLE_CACHE_FILTER_PATH}")
    private String articleCacheFilterPath;

    private final RestTemplate restTemplateArticleCache;

    @Autowired
    public ArticleCacheAAAClient(@Qualifier("RestTemplate-ArticleCache") RestTemplate restTemplateArticleCache) {
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


    @Override
    public Documents getArticleUrlPathByEan(String barCode, String countryCode, String salesLine, String languageCode) throws Exception {
        return documentsService.
                getDocumentsByBarcode(countryCode, salesLine, languageCode, barCode);
    }

    @Override
    public Documents getArticleUrlPathByMmsArtNo(Long mmsArtNo, String countryCode, String salesLine, String languageCode) throws Exception {

        return documentsService.
                getDocumentsByArticleNumber(countryCode, salesLine, languageCode, mmsArtNo);
    }

    @Override
    public Documents getArticleUrlPathByArtVarBundle(Long articleNumber, Long variantNumber, Long bundleNumber, String countryCode, String salesLine, String languageCode) throws Exception {
        return documentsService.
                getDocumentsByArticleNumberVarianBundleId(
                        countryCode, salesLine, languageCode, articleNumber, variantNumber, bundleNumber);

    }

    @Override
    public Documents getArticleUrlPathBySubsysArtNo(Long subsysArtNo, String countryCode, String salesLine, String languageCode) throws Exception {
        return documentsService.
                getDocumentsBySubsysArticleNumber(countryCode, salesLine, languageCode, subsysArtNo);
    }

    @Override
    public Documents findArticleUrlPathByMmsArtNo(Long mmsArtNo, String countryCode, String salesLine, String languageCode) throws Exception {
        return getArticleUrlPathByMmsArtNo(mmsArtNo, countryCode, salesLine, languageCode);
    }

    @Override
    public ArticleCacheIhdsTree getArticleCacheDocumentByURL(String articleUrlPath) {
        System.out.println("ArticleCacheAAAClient.getArticleCacheDocumentByURL url:" + articleCacheServiceHost + "/" + articleUrlPath);
        return getForArticleCacheObject(
                articleCacheServiceHost + "/" + articleUrlPath,
                ArticleCacheIhdsTree.class
        );
    }

    private <T> T getForArticleCacheObject(String articleUrl, Class<T> responseType) {
        return restTemplateArticleCache.getForObject(articleUrl, responseType);
    }

}
