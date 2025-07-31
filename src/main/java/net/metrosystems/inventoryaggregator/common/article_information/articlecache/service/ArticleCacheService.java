package net.metrosystems.inventoryaggregator.common.article_information.articlecache.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.MinimumRetailPrice;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.articleCacheStoreExceptions.ArticleCacheStoreExceptionsClient;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.articleCacheStoreExceptions.model.ArticleCacheStoreExceptionsIhdsTree;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_aaa.ArticleCacheAAAClient;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheDataGroup;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheIhdsTree;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheReaderDigest;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.model.ArticleCacheFullData;
import net.metrosystems.inventoryaggregator.common.model.ArticleNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Service
public class ArticleCacheService {

  private static final String PAKISTAN_COUNTRY_CODE = "PK";
  private static final Double ZERO = 0.0;
  private static final String ARTICLE_CACHE_PP2_ENVIRONMENT = "pp2";
  private static final String ARTICLE_CACHE_PP1_ENVIRONMENT = "pp1";
  private static final String ARTICLE_CACHE_PROD_ENVIRONMENT = "prod";
  private static final String K8S_DEV_ENVIRONMENT = "dev";
  private static final String K8S_PP_ENVIRONMENT = "pp";
  private static final String LOCAL_ENVIRONMENT = "local";
  private static final Set<String> COUNTRY_CODES = Set.of("CZ", "SK");
  private final ArticleCacheAAAClient articleCacheClient;
  private final ArticleCacheStoreExceptionsClient articleCacheStoreExceptionsClient;

  @Value("${ARTICLE_CACHE_STORE_EXCEPTIONS_PATH}")
  private String articleCacheStoreExceptionsPath;

  @Value("${ARTICLE_CACHE_DOCUMENTS}")
  private String articleCacheErpPath;

  @Value("${app.stage}")
  private String k8sEnvironment;

  private static final Map<String, String> LANGUAGE_CODE_EXCEPTIONS = new HashMap<>();

  static {
    LANGUAGE_CODE_EXCEPTIONS.put("UA", "UK");
    LANGUAGE_CODE_EXCEPTIONS.put("KZ", "RU");
    LANGUAGE_CODE_EXCEPTIONS.put("MD", "RO");
    LANGUAGE_CODE_EXCEPTIONS.put("PK", "UR");
    LANGUAGE_CODE_EXCEPTIONS.put("RS", "SR");
    LANGUAGE_CODE_EXCEPTIONS.put("CZ", "CS");
  }

  @Autowired
  public ArticleCacheService(
      ArticleCacheAAAClient articleCacheClient,
      ArticleCacheStoreExceptionsClient articleCacheStoreExceptionsClient) {
    this.articleCacheClient = articleCacheClient;
    this.articleCacheStoreExceptionsClient = articleCacheStoreExceptionsClient;
  }



  public ArticleCacheFullData getArticleCacheFullDataByArtVarBundle(
      Long articleNumber,
      Long variantNumber,
      Long bundleNumber,
      String countryCode,
      String salesLine,
      String languageCode,
      Integer storeNo) {

    String languageCodeForAC = getCorrectLanguageCode(countryCode, languageCode);

    String articleUrlPath =
        String.format(
            articleCacheErpPath,
            countryCode.toLowerCase(),
            bundleNumber,
            salesLine.toLowerCase(),
            getEnvironment(countryCode),
            languageCodeForAC.toLowerCase(),
            articleNumber,
            variantNumber);
    try {
      ArticleCacheIhdsTree articleCacheIhdsTree =
          articleCacheClient.getArticleCacheDocumentByURL(articleUrlPath);
      return transformArticleCacheDocumentToArticleFullData(articleCacheIhdsTree, storeNo);
    } catch (Exception e) {
      if (e instanceof HttpClientErrorException httpClientErrorException
          && HttpStatus.CONFLICT == httpClientErrorException.getStatusCode()) {
        throw new ArticleNotFoundException();
      }
      throw e;
    }
  }

  private String getCorrectLanguageCode(String countryCode, String languageCode) {
    if ("EN".equalsIgnoreCase(languageCode)) {
      return LANGUAGE_CODE_EXCEPTIONS.getOrDefault(countryCode, countryCode);
    }
    return languageCode;
  }


  private String getEnvironment(String countryCode) {

    if (K8S_DEV_ENVIRONMENT.equalsIgnoreCase(k8sEnvironment)
        || K8S_PP_ENVIRONMENT.equalsIgnoreCase(k8sEnvironment)
        || LOCAL_ENVIRONMENT.equalsIgnoreCase(k8sEnvironment)) {

      if (COUNTRY_CODES.contains(countryCode)) {
        return ARTICLE_CACHE_PP2_ENVIRONMENT;
      } else {
        return ARTICLE_CACHE_PP1_ENVIRONMENT;
      }
    } else {
      return ARTICLE_CACHE_PROD_ENVIRONMENT;
    }
  }


  private ArticleCacheFullData transformArticleCacheDocumentToArticleFullData(
      ArticleCacheIhdsTree articleCacheDocument, Integer storeNo) {
    Integer hierarchySequenceStoreNo =
        articleCacheDocument
            .getTransaction()
            .getArticleCacheIncident()
            .getHierarchySequenceStoreNo(storeNo);
    if (hierarchySequenceStoreNo == null) {
      throw new ArticleNotFoundException();
    }
    ArticleCacheReaderDigest articleCacheReaderDigest =
        articleCacheDocument
            .getTransaction()
            .getArticleCacheIncident()
            .getArticleCacheReaderDigest();
    ArticleCacheDataGroup articleCacheDataGroup100 =
        articleCacheDocument
            .getTransaction()
            .getArticleCacheIncident()
            .getArticleCacheDataGroupWithoutStore(100);
    ArticleCacheDataGroup articleCacheDataGroup300 =
        articleCacheDocument
            .getTransaction()
            .getArticleCacheIncident()
            .getArticleCacheDataGroupWithoutStore(300);
    ArticleCacheDataGroup articleCacheDataGroup310 =
        articleCacheDocument
            .getTransaction()
            .getArticleCacheIncident()
            .getArticleCacheDataGroupWithoutStore(310);
    ArticleCacheDataGroup articleCacheDataGroup320 =
        articleCacheDocument
            .getTransaction()
            .getArticleCacheIncident()
            .getArticleCacheDataGroupWithStore(320, hierarchySequenceStoreNo);
    ArticleCacheDataGroup articleCacheDataGroup325 =
        articleCacheDocument
            .getTransaction()
            .getArticleCacheIncident()
            .getArticleCacheDataGroupWithStoreRef(325, hierarchySequenceStoreNo);
    ArticleCacheDataGroup articleCacheDataGroup200 =
        articleCacheDocument
            .getTransaction()
            .getArticleCacheIncident()
            .getArticleCacheDataGroupWithoutStore(200);

    return new ArticleCacheFullData(
        articleCacheReaderDigest.getMmsArtNo(),
        articleCacheReaderDigest.getVariantNo(),
        articleCacheReaderDigest.getBundleNo(),
        articleCacheReaderDigest.getArticleDescription(),
        articleCacheDocument.getTransaction().getArticleCacheIncident().getBarcodeForBundle(),
        articleCacheReaderDigest.getProducingSupplier(),
        articleCacheDataGroup100.getPresentationMerchandiseGroup(),
        articleCacheDataGroup310.getSellingBundleContent(),
        articleCacheDataGroup100.getWeightArticles(),
        articleCacheDataGroup200.getNetWeight(),
        articleCacheReaderDigest.getSubsysNo(),
        articleCacheDataGroup100.getVatPercent(),
        articleCacheDataGroup320.getPieceNetSellPrice(),
        articleCacheDataGroup320.getBundleNetSellPrice(),
        articleCacheReaderDigest.getVariantText(),
        articleCacheReaderDigest.getArticleType(),
        articleCacheDataGroup325.getArticleClass(),
        articleCacheDataGroup100.getFoodNonfoodIndicator(),
        articleCacheDataGroup100.getPresentationMainMerchandiseGroup(),
        articleCacheDataGroup100.getPresentationSubMerchandiseGroup(),
        articleCacheDataGroup320.getAssignedStoreSupplier(),
        articleCacheDataGroup325.getProcurementCd(),
        articleCacheDataGroup320.getAssortmentCode(),
        articleCacheDataGroup320.getBlockingCode(),
        articleCacheDataGroup320.getActivationStatus(),
        articleCacheDataGroup200.getExpiryDays(),
        articleCacheDataGroup325.getPieceNetBuyingPrice(),
        articleCacheDataGroup325.getBundleNetBuyingPrice(),
        articleCacheDataGroup325.getPieceNetNetBuyingPrice(),
        articleCacheDataGroup325.getBundleNetNetBuyingPrice(),
        articleCacheDataGroup300.getArticleNoEmptiesSales(),
        articleCacheReaderDigest.getBrandName(),
        calculateMinimumRetailPrice(
            articleCacheDocument, storeNo, articleCacheDataGroup310.getSellingBundleContent()),
        articleCacheDataGroup200.getCountryOfOrigin(),
        articleCacheDataGroup200.getPackageFactor(),
        articleCacheDataGroup100.getAlcoholClass(),
        articleCacheReaderDigest.getTraceabilityIndicator(),
        articleCacheDataGroup320.getCoreAssortmentIndicator() != null
            ? articleCacheDataGroup320.getCoreAssortmentIndicator()
            : 0,
        articleCacheDataGroup320.getAssignedStoreSupplierType(),
        articleCacheDataGroup310.getInOutIndicator(),
        articleCacheDataGroup320.getMinimumGrossSalesPricePerUnit(),
        articleCacheDataGroup200.getBasPriceContent(),
        articleCacheDataGroup200.getBasPriceContentMeasureUnit(),
        articleCacheDataGroup200.getBasPriceContentUnit(),
        articleCacheDataGroup200.getBasPriceFactor());
  }

  private MinimumRetailPrice calculateMinimumRetailPrice(
      ArticleCacheIhdsTree articleCacheIhdsTree,
      Integer storeNumber,
      Integer sellingBundleContent) {
    String countryCode =
        articleCacheIhdsTree
            .getTransaction()
            .getArticleCacheIncident()
            .getArticleCacheDomain()
            .getPlkz();

    if (PAKISTAN_COUNTRY_CODE.equalsIgnoreCase(countryCode)) {
      Boolean hasMinimumRetailPrice =
          articleCacheIhdsTree
              .getTransaction()
              .getArticleCacheIncident()
              .getArticleCacheDataGroupWithoutStore(100)
              .getHasMinimumRetailPrice();

      if (hasMinimumRetailPrice != null && hasMinimumRetailPrice) {
        Double minimumRetailPriceForVatCalculation =
            articleCacheIhdsTree
                .getTransaction()
                .getArticleCacheIncident()
                .getArticleCacheDataGroup()
                .stream()
                .filter(articleCacheDataGroup -> articleCacheDataGroup.getStoreNo() == storeNumber)
                .findFirst()
                .map(ArticleCacheDataGroup::getMinimumRetailPriceForVatCalculation)
                .orElse(ZERO);
        return MinimumRetailPrice.builder()
            .indicator(Boolean.TRUE)
            .value(minimumRetailPriceForVatCalculation * sellingBundleContent)
            .build();
      }

      return MinimumRetailPrice.builder().indicator(Boolean.FALSE).value(ZERO).build();
    }

    return MinimumRetailPrice.builder().indicator(Boolean.FALSE).value(ZERO).build();
  }

  public ArticleCacheStoreExceptionsIhdsTree getArticleCacheStoreExceptions(
      Long articleNumber,
      Long variantNumber,
      Long bundleNumber,
      String countryCode,
      String salesLine,
      Integer storeNo) {

    String articleUrlPath =
        String.format(
            articleCacheStoreExceptionsPath,
            countryCode.toLowerCase(),
            bundleNumber,
            salesLine.toLowerCase(),
            "en",
            articleNumber,
            storeNo,
            variantNumber);
    return articleCacheStoreExceptionsClient.getArticleCacheDocumentByURL(articleUrlPath);
  }
}
