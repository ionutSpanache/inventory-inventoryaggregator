package net.metrosystems.inventoryaggregator.common.article_information.articlecache.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.MinimumRetailPrice;
import net.metrosystems.inventoryaggregator.articleattributes.model.ProductLifeCycleData;
import net.metrosystems.inventoryaggregator.articleattributes.model.PromotionArticleData;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.document.model.Documents;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.document.service.DocumentsService;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.aricle_cache_isu_istem_store_merchandise.IsuItemStoreMerchandise;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.aricle_cache_isu_istem_store_merchandise.StoreInformationGroup;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.articleCacheStoreExceptions.ArticleCacheStoreExceptionsClient;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.articleCacheStoreExceptions.model.ArticleCacheStoreExceptionsIhdsTree;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_aaa.ArticleCacheAAAClient;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard.ContentIduItemDerivedStandard;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard.IduItemDerivedStandard;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard.IsTradeItemExcludedFromDiscounts;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheDataGroup;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheIhdsTree;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheReaderDigest;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_promotion_article.PromotionArticle;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_promotion_article.PromotionArticleDataGroup;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.model.ArticleCacheFullData;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.model.ArticleVariantBundleIdentifier;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.model.IsTradeItem;
import net.metrosystems.inventoryaggregator.common.model.ArticleNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Service
public class ArticleCacheService {

  private static final String PAKISTAN_COUNTRY_CODE = "PK";
  private static final Double ZERO = 0.0;
  private static final String IHDS_TREE = "erp_ihds_tree";
  private static final String BUNDLE_NO = "bundleno";
  private static final String MMS_ARTNO = "mmsartno";
  private static final String VARIANT_NO = "variantno";
  private static final String COMPANY = "company";
  private static final String ENVIRONMENT = "environment";
  private static final String ARTICLE_CACHE_PP2_ENVIRONMENT = "pp2";
  private static final String ARTICLE_CACHE_PP1_ENVIRONMENT = "pp1";
  private static final String ARTICLE_CACHE_PROD_ENVIRONMENT = "prod";
  private static final String K8S_DEV_ENVIRONMENT = "dev";
  private static final String K8S_PP_ENVIRONMENT = "pp";
  private static final String LOCAL_ENVIRONMENT = "local";
  private static final String LANGUAGE = "language";
  private static final Set<String> COUNTRY_CODES = Set.of("IN", "CZ", "SK");
  private final DocumentsService documentsService;
  private final ArticleCacheAAAClient articleCacheClient;
  private final ArticleCacheStoreExceptionsClient articleCacheStoreExceptionsClient;
  @Value("${ARTICLE_CACHE_STORE_EXCEPTIONS_PATH}")
  private String articleCacheStoreExceptionsPath;
  @Value("${ARTICLE_CACHE_DOCUMENTS}")
  private String articleCacheErpPath;

  @Value("${app.stage}")
  private String k8sEnvironment;

  @Autowired
  public ArticleCacheService(
      DocumentsService documentsService,
      ArticleCacheAAAClient articleCacheClient,
      ArticleCacheStoreExceptionsClient articleCacheStoreExceptionsClient) {
    this.documentsService = documentsService;
    this.articleCacheClient = articleCacheClient;
    this.articleCacheStoreExceptionsClient = articleCacheStoreExceptionsClient;
  }

  public List<ArticleVariantBundleIdentifier> getArticleVariantBundleListForEan(
      String ean, String countryCode, String salesLine, String languageCode) throws Exception {
    return getArticleVariantBundleIdentifierListFromFilterUrl(
        articleCacheClient.getArticleUrlPathByEan(ean, countryCode, salesLine, languageCode));
  }

  public List<ArticleVariantBundleIdentifier> getArticleVariantBundleListForSubsys(
      Long subsysArtNo, String countryCode, String salesLine, String languageCode)
      throws Exception {
    return getArticleVariantBundleIdentifierListFromFilterUrl(
        articleCacheClient.getArticleUrlPathBySubsysArtNo(
            subsysArtNo, countryCode, salesLine, languageCode));
  }

  public List<ArticleVariantBundleIdentifier> getArticleVariantBundleListForArticleVariantBundle(
      Long articleNumber,
      Long variantNumber,
      Long bundleNumber,
      String countryCode,
      String salesLine,
      String languageCode)
      throws Exception {
    return getArticleVariantBundleIdentifierListFromFilterUrl(
        articleCacheClient.getArticleUrlPathByArtVarBundle(
            articleNumber, variantNumber, bundleNumber, countryCode, salesLine));
  }

  public List<ArticleVariantBundleIdentifier> getArticleVariantBundleListForArticleNumber(
      Long articleNumber, String countryCode, String salesLine)
      throws Exception {

    return getArticleVariantBundleIdentifierListFromFilterUrl(
        articleCacheClient.getArticleUrlPathByMmsArtNo(
            articleNumber, countryCode, salesLine));
  }

  private List<ArticleVariantBundleIdentifier> getArticleVariantBundleIdentifierListFromFilterUrl(
      Documents filterUrlString) {
    if (filterUrlString == null || CollectionUtils.isEmpty(filterUrlString.getArticleKeys())) {
      return Collections.emptyList();
    }

    List<ArticleVariantBundleIdentifier> articleVariantBundleIdentifierList = new ArrayList<>();
    for (String articleUrlPath : filterUrlString.getArticleKeys()) {
      ArticleId articleId = documentsService.tranformDocumentInArticleId(articleUrlPath);
      ArticleVariantBundleIdentifier articleVariantBundleIdentifier =
          new ArticleVariantBundleIdentifier();
      articleVariantBundleIdentifier.setArticleNumber(articleId.getArticleNumber());
      articleVariantBundleIdentifier.setVariantNumber(articleId.getVariantNumber());
      articleVariantBundleIdentifier.setBundleNumber(articleId.getBundleNumber());
      articleVariantBundleIdentifierList.add(articleVariantBundleIdentifier);
    }

    return articleVariantBundleIdentifierList;
  }

  public ArticleCacheFullData getArticleCacheFullDataByArtVarBundle(
      Long articleNumber,
      Long variantNumber,
      Long bundleNumber,
      String countryCode,
      String salesLine,
      String languageCode,
      Integer storeNo)
       {

    String articleUrlPath =
        String.format(
            articleCacheErpPath,
            countryCode.toLowerCase(),
            bundleNumber,
            salesLine.toLowerCase(),
            getEnvironment(countryCode),
            languageCode.toLowerCase(),
            articleNumber,
            variantNumber);
    try{
      ArticleCacheIhdsTree articleCacheIhdsTree = articleCacheClient.getArticleCacheDocumentByURL(articleUrlPath);
      return transformArticleCacheDocumentToArticleFullData(
          articleCacheIhdsTree,
          storeNo);
    }catch(Exception e){
      if(e instanceof HttpClientErrorException httpClientErrorException && HttpStatus.CONFLICT == httpClientErrorException.getStatusCode()){
        throw new ArticleNotFoundException();
      }
      throw e;
    }
  }

  public Optional<IduItemDerivedStandard> getMultiLanguageDataByArtVarBundle(
      String countryCode,
      net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId articleId) {
    Documents articleUrlPathList =
        articleCacheClient.getMultiLanguageDocumentUrl(countryCode, articleId);
    if (CollectionUtils.isEmpty(articleUrlPathList.getArticleKeys())) {
      return Optional.empty();
    }
    try {
      return articleUrlPathList.getArticleKeys().stream()
          .map(articleCacheClient::getIduItemDerivedStandardDocument)
          .filter(
              iduItemDerivedStandard ->
                  iduItemDerivedStandard
                              .getContentIduItemDerivedStandard()
                              .getVariantSortDescriptionV2()
                          != null
                      && iduItemDerivedStandard
                              .getContentIduItemDerivedStandard()
                              .getMerchandiseArticleDescription()
                          != null)
          .findAny();
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  public List<IduItemDerivedStandard> getIduItemDerivedStandards(
      String countryCode,
      net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId articleId) {
    Documents articleUrlPathList =
        articleCacheClient.getMultiLanguageDocumentUrl(countryCode, articleId);

    if (articleUrlPathList == null
        || CollectionUtils.isEmpty(articleUrlPathList.getArticleKeys())) {
      return Collections.emptyList();
    }
    List<String> articleUrlPath = articleUrlPathList.getArticleKeys();

    return articleCacheClient.getMultipleIduItemDerivedStandardDocument(articleUrlPath);
  }

  public IduItemDerivedStandard getIduItemDerivedStandard(String documentUrl) {

    if (documentUrl == null) return null;

    return articleCacheClient.getIduItemDerivedStandardDocument(documentUrl);
  }

  public ProductLifeCycleData buildProductLifeCycleData(
      List<IduItemDerivedStandard> iduItemDerivedStandardList) {
    if (CollectionUtils.isEmpty(iduItemDerivedStandardList)) return null;
    Optional<IduItemDerivedStandard> documentWithProductLifecycle =
        iduItemDerivedStandardList.stream()
            .filter(
                iduItemDerivedStandard ->
                    iduItemDerivedStandard
                            .getContentIduItemDerivedStandard()
                            .getAssortmentClusterSpecificationV2()
                        != null)
            .findFirst();

    if (documentWithProductLifecycle.isEmpty()) return null;

    return mapToProductLifeCycleData(documentWithProductLifecycle.get());
  }

  public List<PromotionArticleData> getPromotionArticleData(
      String countryCode,
      net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId articleId,
      Integer storeNumber) {

    String documentUrl =
        "documents/"
            + countryCode.toLowerCase()
            + "/promotion-article"
            + "?bundleno="
            + articleId.getBundleNumber()
            + "&company=mcc"
            + "&environment="
            + getEnvironment(countryCode)
            + "&mmsartno="
            + articleId.getArticleNumber()
            + "&storeno="
            + storeNumber
            + "&variantno="
            + articleId.getVariantNumber();

    List<PromotionArticleData> promotionArticleData = new ArrayList<>();

    try {
      promotionArticleData =
          mapToPromotionArticleData(articleCacheClient.getPromotionArticle(documentUrl));
    } catch (Exception ex) {
      if(ex instanceof HttpClientErrorException exception && exception.getStatusCode() == HttpStatus.CONFLICT){
        log.debug(ex.getMessage(), ex);
      } else {
        log.error("PROMOTION_ARTICLE_NOT_FOUND " + documentUrl, ex);
      }
    }

    return promotionArticleData;
  }

  private List<PromotionArticleData> mapToPromotionArticleData(PromotionArticle promotionArticle) {

    List<PromotionArticleDataGroup> data =
        promotionArticle
            .getPromotionArticleTransaction()
            .getPromotionArticleIncident()
            .getPromotionArticleDataGroups()
            .stream()
            .filter(
                promotionArticleDataGroup ->
                    promotionArticleDataGroup.getId().equalsIgnoreCase("110"))
            .toList();

    List<PromotionArticleData> promotions = new ArrayList<>();

    if (!data.isEmpty()) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      data.forEach(
          t ->
              promotions.add(
                  PromotionArticleData.builder()
                      .promotionId(t.getPromotionId())
                      .promotionNetSalesPricePerBundle(t.getPromotionNetSalesPricePerBundle())
                      .promotionNetSalesPricePerPiece(t.getPromotionNetSalesPricePerPiece())
                      .promotionStart(LocalDate.parse(t.getPromotionActiveStartDate(), formatter))
                      .promotionEnd(LocalDate.parse(t.getPromotionActiveEndDate(), formatter))
                      .valid(t.getValidity())
                      .descriptions(t.getDescriptions())
                      .build()));
    }
    return promotions;
  }

  public Optional<StoreInformationGroup> getStoreInformationGroup(
      String countryCode,
      net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId articleId,
      Integer storeNumber) {
    Documents articleUrlPathList =
        articleCacheClient.getChannelDeliveryFlagUrl(countryCode, articleId);

    if (articleUrlPathList == null
        || CollectionUtils.isEmpty(articleUrlPathList.getArticleKeys())) {
      return Optional.empty();
    }
    String articleUrlPath = articleUrlPathList.getArticleKeys().get(0);

    IsuItemStoreMerchandise isuItemStoreMerchandise =
        articleCacheClient.getIsuItemStoreMerchandiseDocument(articleUrlPath);

    return mapToStoreInformationGroupForStore(isuItemStoreMerchandise, storeNumber);
  }

  private Optional<StoreInformationGroup> mapToStoreInformationGroupForStore(
      IsuItemStoreMerchandise isuItemStoreMerchandise, Integer storeNumber) {
    if (isuItemStoreMerchandise == null) return Optional.empty();
    return isuItemStoreMerchandise.getContentIsuItemStoreMerchandise().getStoreInformationGroups()
                == null
            ? Optional.empty()
            : isuItemStoreMerchandise
                .getContentIsuItemStoreMerchandise()
                .getStoreInformationGroups()
                .stream()
                .filter(
                    storeInformationGroup -> storeInformationGroup.getGroupingId() == storeNumber)
                .findFirst();
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

  public ArticleCacheFullData getArticleCacheFullDataByArtVarBundleForArticleId(
      Integer storeNo, String documentUrl) {

    if (documentUrl == null) return null;
    ArticleCacheIhdsTree articleCacheDocument =
        articleCacheClient.getArticleCacheDocumentByURL(documentUrl);

    if (articleCacheDocument == null
        || articleCacheDocument.getTransaction() == null
        || articleCacheDocument.getTransaction().getArticleCacheIncident() == null
        || articleCacheDocument
                .getTransaction()
                .getArticleCacheIncident()
                .getHierarchySequenceStoreNo(storeNo)
            == null) {
      return null;
    }

    return transformArticleCacheDocumentToArticleFullData(
        articleCacheDocument, storeNo);
  }

  public List<String> getArticleKeysForDerivedStandardDocument(
      String countryCode,
      Long articleNumber,
      Long bundleNumber,
      Long variantNumber,
      String salesLine) {

    return documentsService
        .getIduItemDerivedUrlByArtVarBundle(
            countryCode,
            net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId.builder()
                .articleNumber(articleNumber)
                .bundleNumber(bundleNumber)
                .variantNumber(variantNumber)
                .build(),
            salesLine)
        .getArticleKeys();
  }

  public IsTradeItemExcludedFromDiscounts isTradeItemExcludedFromDiscounts(
      List<String> articleKeys) {

    IsTradeItemExcludedFromDiscounts result = new IsTradeItemExcludedFromDiscounts();

    if (!CollectionUtils.isEmpty(articleKeys)) {
      articleKeys.forEach(
          articleKey -> {
            IduItemDerivedStandard iduItemDerivedStandard =
                articleCacheClient.getIduItemDerivedStandardDocument(articleKey);
            if (iduItemDerivedStandard != null
                && iduItemDerivedStandard.getContentIduItemDerivedStandard() != null) {
              IsTradeItemExcludedFromDiscounts isTradeItemExcludedFromDiscounts =
                  iduItemDerivedStandard
                      .getContentIduItemDerivedStandard()
                      .getIsTradeItemExcludedFromDiscounts();
              if (isTradeItemExcludedFromDiscounts != null
                  && Boolean.TRUE.equals(isTradeItemExcludedFromDiscounts.getValue())) {
                result.setValue(true);
                if (result.getValidFrom() == null
                    || isTradeItemExcludedFromDiscounts
                        .getValidFrom()
                        .after(result.getValidFrom())) {
                  result.setValidFrom(isTradeItemExcludedFromDiscounts.getValidFrom());
                }
              }
            }
          });
    }
    return result;
  }

  private ArticleCacheFullData transformArticleCacheDocumentToArticleFullData(
      ArticleCacheIhdsTree articleCacheDocument,
      Integer storeNo) {
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
        articleCacheDataGroup320.getMinimumGrossSalesPricePerUnit());
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

  private ProductLifeCycleData mapToProductLifeCycleData(
      IduItemDerivedStandard iduItemDerivedStandard) {

    Long presencePeriodEndWeek =
        iduItemDerivedStandard
                    .getContentIduItemDerivedStandard()
                    .getAssortmentClusterSpecificationV2()
                    .getPresencePeriodV3()
                == null
            ? null
            : iduItemDerivedStandard
                .getContentIduItemDerivedStandard()
                .getAssortmentClusterSpecificationV2()
                .getPresencePeriodV3()
                .getPresencePeriodEndWeekV2();

    Long presencePeriodEndYear =
        iduItemDerivedStandard
                    .getContentIduItemDerivedStandard()
                    .getAssortmentClusterSpecificationV2()
                    .getPresencePeriodV3()
                == null
            ? null
            : iduItemDerivedStandard
                .getContentIduItemDerivedStandard()
                .getAssortmentClusterSpecificationV2()
                .getPresencePeriodV3()
                .getPresencePeriodEndYearV2();

    Long presencePeriodStartWeek =
        iduItemDerivedStandard
                    .getContentIduItemDerivedStandard()
                    .getAssortmentClusterSpecificationV2()
                    .getPresencePeriodV3()
                == null
            ? null
            : iduItemDerivedStandard
                .getContentIduItemDerivedStandard()
                .getAssortmentClusterSpecificationV2()
                .getPresencePeriodV3()
                .getPresencePeriodStartWeekV2();

    Long presencePeriodStartYear =
        iduItemDerivedStandard
                    .getContentIduItemDerivedStandard()
                    .getAssortmentClusterSpecificationV2()
                    .getPresencePeriodV3()
                == null
            ? null
            : iduItemDerivedStandard
                .getContentIduItemDerivedStandard()
                .getAssortmentClusterSpecificationV2()
                .getPresencePeriodV3()
                .getPresencePeriodStartYearV2();

    return ProductLifeCycleData.builder()
        .assortmentCluster(
            iduItemDerivedStandard
                .getContentIduItemDerivedStandard()
                .getAssortmentClusterSpecificationV2()
                .getAssortmentClusterV2())
        .presencePeriodEndWeek(presencePeriodEndWeek)
        .presencePeriodEndYear(presencePeriodEndYear)
        .presencePeriodStartWeek(presencePeriodStartWeek)
        .presencePeriodStartYear(presencePeriodStartYear)
        .build();
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
    // todo: change "en" to country code
    return articleCacheStoreExceptionsClient.getArticleCacheDocumentByURL(articleUrlPath);
  }

  public List<Integer> getActiveStoresByByArtVarBundle(
      String countryCode, String salesLine, String languageCode, ArticleId articleId)
      throws Exception {

    List<Integer> activeStoresList;

    Documents articleUrlPathList =
        articleCacheClient.getArticleUrlPathByArtVarBundle(
            articleId.getArticleNumber(),
            articleId.getVariantNumber(),
            articleId.getBundleNumber(),
            countryCode,
            salesLine);
    if (CollectionUtils.isEmpty(articleUrlPathList.getArticleKeys())) {
      throw new ArticleNotFoundException();
    }
    String articleUrlPath = articleUrlPathList.getArticleKeys().get(0);
    ArticleCacheIhdsTree articleCacheIhdsTree =
        articleCacheClient.getArticleCacheDocumentByURL(articleUrlPath);

    activeStoresList =
        articleCacheIhdsTree
            .getTransaction()
            .getArticleCacheIncident()
            .getArticleCacheDataGroup()
            .stream()
            .filter(articleCacheDataGroup -> (articleCacheDataGroup.getId() == 320))
            .map(ArticleCacheDataGroup::getStoreNo)
            .toList();

    return activeStoresList;
  }

  public void enhanceArticleMasterData(
      ArticleMasterData articleMasterData, List<IduItemDerivedStandard> iduItemDerivedStandards) {
    if (articleMasterData == null || CollectionUtils.isEmpty(iduItemDerivedStandards)) return;
    articleMasterData
        .setIsTradeItemExcludedFromDiscounts(
                getIsTradeItem(iduItemDerivedStandards,"isTradeItemExcludedFromDiscounts"))
        .setGradeCodeReferenceFruitsVegetables(
            getGradeCodeReferenceFruitsVegetables(iduItemDerivedStandards))
        .setIsTradeItemInSellOut(getIsTradeItem(iduItemDerivedStandards,"isTradeItemInSellOut"))
        .setIsTradeItemSellableInPFSShop(getIsTradeItem(iduItemDerivedStandards,"isTradeItemSellableInPFSShop"))
        .setIsTradeItemDiscountMachine(getIsTradeItem(iduItemDerivedStandards,"isTradeItemDiscountMachine"))
        .setIsMerchandiseOwnBrand(getIsTradeItem(iduItemDerivedStandards,"merchandiseBrand"));
  }

  public String getGradeCodeReferenceFruitsVegetables(
      List<IduItemDerivedStandard> iduItemDerivedStandards) {

    if (CollectionUtils.isEmpty(iduItemDerivedStandards)) return null;

    for (IduItemDerivedStandard iduItemDerivedStandard : iduItemDerivedStandards) {
      if (iduItemDerivedStandard != null
          && iduItemDerivedStandard.getContentIduItemDerivedStandard() != null
          && iduItemDerivedStandard
                  .getContentIduItemDerivedStandard()
                  .getGradeCodeReferenceFruitsVegetables()
              != null) {
        return iduItemDerivedStandard
            .getContentIduItemDerivedStandard()
            .getGradeCodeReferenceFruitsVegetables()
            .getValue();
      }
    }
    return null;
  }

  public Boolean getIsTradeItem(
          List<IduItemDerivedStandard> iduItemDerivedStandards, String tradeIdem) {
    IsTradeItem resultIsTradeItem = new IsTradeItem();
    iduItemDerivedStandards.forEach(elem -> {
      if (elem.getContentIduItemDerivedStandard() != null) {
        IsTradeItem isTradeItem = getSpecificItem(elem
                .getContentIduItemDerivedStandard(), tradeIdem);
        if (isTradeItem != null
                && Boolean.TRUE.equals(isTradeItem.getValue())) {
          resultIsTradeItem.setValue(true);
          if (resultIsTradeItem.getValidFrom() == null
                  || isTradeItem.getValidFrom().after(resultIsTradeItem.getValidFrom())) {
            resultIsTradeItem.setValidFrom(isTradeItem.getValidFrom());
          }
        }
      }
    });

    if (Boolean.TRUE.equals(resultIsTradeItem.getValue())) {
      if (resultIsTradeItem.getValidFrom() == null) {
        return true;
      } else {
        LocalDate validFrom =
                resultIsTradeItem.getValidFrom().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return LocalDate.now().isAfter(validFrom);
      }
    }
    return false;
  }

  private IsTradeItem getSpecificItem(ContentIduItemDerivedStandard contentIduItemDerivedStandard, String tradeItem) {
    if("isTradeItemExcludedFromDiscounts".equals(tradeItem)
            && contentIduItemDerivedStandard.getIsTradeItemExcludedFromDiscounts() != null) {
      return new IsTradeItem(contentIduItemDerivedStandard.getIsTradeItemExcludedFromDiscounts());
    } else if("isTradeItemInSellOut".equals(tradeItem)
            && contentIduItemDerivedStandard.getIsTradeItemInSellOut() != null) {
      return new IsTradeItem(contentIduItemDerivedStandard.getIsTradeItemInSellOut());
    }else if("isTradeItemSellableInPFSShop".equals(tradeItem)
            && contentIduItemDerivedStandard.getIsTradeItemSellableInPFSShop() != null){
      return new IsTradeItem(contentIduItemDerivedStandard.getIsTradeItemSellableInPFSShop());
    }else if("isTradeItemDiscountMachine".equals(tradeItem)
            && contentIduItemDerivedStandard.getIsTradeItemDiscountMachine() != null) {
      return new IsTradeItem(contentIduItemDerivedStandard.getIsTradeItemDiscountMachine());
    }else if("merchandiseBrand".equals(tradeItem)
            && contentIduItemDerivedStandard.getMerchandiseBrand() != null) {
      return new IsTradeItem(contentIduItemDerivedStandard.getMerchandiseBrand());
    }
    return null;
  }
}
