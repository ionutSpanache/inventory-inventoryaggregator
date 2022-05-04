package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_aaa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.document.model.Documents;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.document.service.DocumentsService;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.ArticleCacheClient;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.aricle_cache_isu_istem_store_merchandise.IsuItemStoreMerchandise;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard.IduItemDerivedStandard;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheIhdsTree;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_promotion_article.PromotionArticle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class ArticleCacheAAAClient implements ArticleCacheClient {

  @Autowired DocumentsService documentsService;

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
  public Documents getArticleUrlPathByEan(
      String barCode, String countryCode, String salesLine, String languageCode) throws Exception {
    return documentsService.getDocumentsByBarcode(countryCode, salesLine, languageCode, barCode);
  }

  @Override
  public Documents getArticleUrlPathByMmsArtNo(
      Long mmsArtNo, String countryCode, String salesLine, String languageCode) throws Exception {

    return documentsService.getDocumentsByArticleNumber(
        countryCode, salesLine, languageCode, mmsArtNo);
  }

  @Override
  public Documents getArticleUrlPathByArtVarBundle(
      Long articleNumber,
      Long variantNumber,
      Long bundleNumber,
      String countryCode,
      String salesLine,
      String languageCode)
      throws Exception {
    return documentsService.getDocumentsByArticleNumberVarianBundleId(
        countryCode, salesLine, languageCode, articleNumber, variantNumber, bundleNumber);
  }

  @Override
  public Documents getArticleUrlPathBySubsysArtNo(
      Long subsysArtNo, String countryCode, String salesLine, String languageCode)
      throws Exception {
    return documentsService.getDocumentsBySubsysArticleNumber(
        countryCode, salesLine, languageCode, subsysArtNo);
  }

  @Override
  public Documents findArticleUrlPathByMmsArtNo(
      Long mmsArtNo, String countryCode, String salesLine, String languageCode) throws Exception {
    return getArticleUrlPathByMmsArtNo(mmsArtNo, countryCode, salesLine, languageCode);
  }

  @Override
  public ArticleCacheIhdsTree getArticleCacheDocumentByURL(String articleUrlPath) {
    return getForArticleCacheObject(
        articleCacheServiceHost + "/" + articleUrlPath, ArticleCacheIhdsTree.class);
  }

  @Override
  public PromotionArticle getPromotionArticle(String articleUrlPath) {
    return getForArticleCacheObject(
            articleCacheServiceHost + "/" + articleUrlPath, PromotionArticle.class);
  }

  @Override
  public IsuItemStoreMerchandise getChannelDeliveryFlagDocument(String articleUrlPath) {
    return getForArticleCacheObject(
            articleCacheServiceHost + "/" + articleUrlPath, IsuItemStoreMerchandise.class);
  }

  @Override
  public Optional<ArticleCacheIhdsTree> getSupplierInformation(
      String countryCode, String salesLine, Optional<String> environment, Long supplierNumber) {
    Documents articleUrlPathList =
        documentsService.getDocumentsBySupplier(countryCode, salesLine, supplierNumber);
    if (CollectionUtils.isEmpty(articleUrlPathList.getArticleKeys())) return Optional.empty();

    String url = articleCacheServiceHost + "/" + articleUrlPathList.getArticleKeys().get(0);
    return Optional.ofNullable(getForArticleCacheObject(url, ArticleCacheIhdsTree.class));
  }

  @Override
  public Documents getProductLifeCycleDocumentUrl(String countryCode, ArticleId articleId) {
    return documentsService.getIsuItemUrlByArtVarBundle(countryCode, articleId);
  }

  @Override
  public Documents getChannelDeliveryFlagUrl(String countryCode, ArticleId articleId) {
    return documentsService.getIsuItemStoreMerchandiseByArtVarBundle(countryCode, articleId);
  }


  @Override
  public Documents getMultiLanguageDocumentUrl(String countryCode, ArticleId articleId) {
    return documentsService.getIduItemDerivedUrlByArtVarBundle(countryCode, articleId, null);
  }

  @Override
  public IduItemDerivedStandard getIduItemDerivedStandardDocument(String articlePath) {
    return getForArticleCacheObject(
            articleCacheServiceHost + "/" + articlePath, IduItemDerivedStandard.class);
  }

  @Override
  public List<IduItemDerivedStandard> getMultipleIduItemDerivedStandardDocument(List<String> articlePaths) {
    List<IduItemDerivedStandard> iduItemDerivedStandards = new ArrayList<>();
    articlePaths.forEach(articlePath ->
        iduItemDerivedStandards.add(getForArticleCacheObject(
            articleCacheServiceHost + "/" + articlePath, IduItemDerivedStandard.class))
    );

    return iduItemDerivedStandards;
  }

  private <T> T getForArticleCacheObject(String articleUrl, Class<T> responseType) {
    try {
      return restTemplateArticleCache.getForObject(articleUrl, responseType);
    } catch (Exception e) {
      log.error(
          "ArticleCacheAAAClient.getForArticleCacheObject - error on GET api call: message {}",
          e.getMessage(),
          e);
      throw e;
    }
  }
}
