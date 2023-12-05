package net.metrosystems.inventoryaggregator.articleattributes.repository;

import static java.time.LocalDate.now;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.PromotionArticleData;
import net.metrosystems.inventoryaggregator.articleattributes.model.PromotionArticleDataForArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.SimplePromotionArticleData;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.document.model.Documents;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.document.service.DocumentsService;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.aricle_cache_isu_istem_store_merchandise.StoreInformationGroup;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard.IduItemDerivedStandard;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_promotion_article.PromotionDescription;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.model.ArticleCacheFullData;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.service.ArticleCacheService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ArticleMasterDataRepository {

  private static final String DEFAULT_LANGUAGE_CODE = "EN";
  private final ArticleCacheService articleCacheService;
  private final DocumentsService documentsService;
  @Value("${ARTICLE_DOCUMENT_ACTIVE_PROMOTION_CHANNEL}")
  private final String promotionChannel;
  @Qualifier("ForkJoinCustomPool")
  private final ExecutorService forkJoinPoolCustom;

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

  @Async("asyncExecutor")
  public CompletableFuture<ArticleMasterData> getArticleMasterData(
      ArticleId articleId,
      String countryCode,
      String salesLine,
      String languageCode,
      Integer storeNo)
      throws Exception {
    ArticleCacheFullData articleCacheFullData =
        getArticleCacheFullDataByArtVarBundle(
            articleId.getArticleNumber(),
            articleId.getVariantNumber(),
            articleId.getBundleNumber(),
            countryCode,
            salesLine,
            languageCode,
            storeNo);

    ArticleMasterData articleMasterData =
        transformArticleCacheFullToArticleMasterData(
            articleCacheFullData, countryCode, storeNo, languageCode, articleId);

    return CompletableFuture.completedFuture(articleMasterData);
  }

  @Async("asyncExecutor")
  public CompletableFuture<Optional<IduItemDerivedStandard>> getMultiLanguageData(
      String countryCode, ArticleId articleId) {
    Optional<IduItemDerivedStandard> multiLanguageData =
        getMultiLanguageDataByArtVarBundle(countryCode, articleId);

    return CompletableFuture.completedFuture(multiLanguageData);
  }

  @Async("asyncExecutor")
  public CompletableFuture<List<IduItemDerivedStandard>> getIduItemDerivedStandardsAsync(
      String countryCode, ArticleId articleId) {
    List<IduItemDerivedStandard> iduItemDerivedStandards =
        getIduItemDerivedStandards(countryCode, articleId);

    return CompletableFuture.completedFuture(iduItemDerivedStandards);
  }

  public List<PromotionArticleData> getPromotionArticle(
      String countryCode, Integer storeNumber, ArticleId articleId) {

    return articleCacheService.getPromotionArticleData(countryCode, articleId, storeNumber);
  }

  @Async("asyncExecutor")
  public CompletableFuture<List<PromotionArticleDataForArticleId>>
      getPromotionDataArticlesForArticleIds(
          List<ArticleId> articleIdList, String countryCode, Integer storeId)
          throws ExecutionException, InterruptedException {
    List<PromotionArticleDataForArticleId> promotionArticleDataList =
        forkJoinPoolCustom
            .submit(
                () ->
                    articleIdList.parallelStream()
                        .map(
                            articleId -> {
                              List<PromotionArticleData> promotionArticleData;
                              promotionArticleData =
                                  getPromotionArticle(countryCode, storeId, articleId).stream()
                                      .filter(t -> Boolean.TRUE.equals(t.getValid()))
                                      .toList();
                              return PromotionArticleDataForArticleId.builder()
                                  .articleId(articleId)
                                  .promotionArticleData(promotionArticleData)
                                  .build();
                            })
                        .filter(Objects::nonNull)
                        .toList())
            .get();
    return CompletableFuture.completedFuture(promotionArticleDataList);
  }

  @Async("asyncExecutor")
  public CompletableFuture<StoreInformationGroup> getDeliveryFlagAndStoreInformation(
      String countryCode, ArticleId articleId, Integer storeNumber) {
    StoreInformationGroup storeInformationGroup =
        getStoreSupplierInformationGroup(countryCode, articleId, storeNumber)
            .orElse(new StoreInformationGroup());

    return CompletableFuture.completedFuture(storeInformationGroup);
  }

  @Async("asyncExecutor")
  public CompletableFuture<Map<ArticleId, ArticleMasterData>> getArticleMasterDataForArticleIdList(
      List<ArticleId> articleIdList,
      String countryCode,
      String salesLine,
      String languageCode,
      Integer storeNo)
      throws Exception {
    Map<ArticleId, ArticleMasterData> result = new ConcurrentHashMap<>();

    String languageCodeForAC = getCorrectLanguageCode(countryCode, languageCode);

    forkJoinPoolCustom
        .submit(
            () ->
                articleIdList.parallelStream()
                    .forEach(
                        articleId -> {
                          String articleUrlPath =
                              String.format(
                                  articleCacheErpPath,
                                  countryCode.toLowerCase(),
                                  articleId.getBundleNumber(),
                                  salesLine.toLowerCase(),
                                  getEnvironment(countryCode),
                                  languageCodeForAC.toLowerCase(),
                                  articleId.getArticleNumber(),
                                  articleId.getVariantNumber());

                          ArticleCacheFullData articleCacheFullData = null;
                          try {
                            articleCacheFullData =
                                articleCacheService
                                    .getArticleCacheFullDataByArtVarBundleForArticleId(
                                        storeNo, articleUrlPath);
                          } catch (Exception e) {
                            log.error(
                                "ArticleMasterDataRepository.getArticleCacheFullDataByArtVarBundleForArticleId error: {}",
                                e.getMessage(),
                                e);
                          }
                          if (articleCacheFullData != null)
                            result.put(
                                articleId,
                                transformArticleCacheFullToArticleMasterData(
                                    articleCacheFullData,
                                    countryCode,
                                    storeNo,
                                    languageCode,
                                    articleId));
                        }))
        .get();
    return CompletableFuture.completedFuture(result);
  }


  @Async("asyncExecutor")
  public CompletableFuture<Map<ArticleId, List<IduItemDerivedStandard>>> getIduItemDerivedStandards(
      List<ArticleId> articleIdList, String countryCode) throws Exception {

    Documents documents =
        documentsService.getIduItemDerivedUrlForMultipleArticles(countryCode, articleIdList, null);

    if (documents == null || CollectionUtils.isEmpty(documents.getArticleKeys()))
      return CompletableFuture.completedFuture(Collections.emptyMap());

    List<IduItemDerivedStandard> iduItemDerivedStandardList = new ArrayList<>();

    forkJoinPoolCustom
        .submit(
            () ->
                documents.getArticleKeys().parallelStream()
                    .forEach(
                        documentUrl -> {
                          try {
                            iduItemDerivedStandardList.add(
                                articleCacheService.getIduItemDerivedStandard(documentUrl));
                          } catch (Exception e) {
                            log.error(
                                "ArticleMasterDataRepository.getIduItemDerivedStandards error: {}",
                                e.getMessage(),
                                e);
                          }
                        }))
        .get();

    return CompletableFuture.completedFuture(
        iduItemDerivedStandardList.stream()
            .collect(
                Collectors.groupingBy(
                    iduItemDerivedStandard ->
                        ArticleId.builder()
                            .articleNumber(iduItemDerivedStandard.getDomain().getArticleNumber())
                            .variantNumber(iduItemDerivedStandard.getDomain().getVariantNumber())
                            .bundleNumber(iduItemDerivedStandard.getDomain().getBundleNumber())
                            .build())));
  }

  private String getCorrectLanguageCode(String countryCode, String languageCode) {
    if("EN".equalsIgnoreCase(languageCode)){
      return LANGUAGE_CODE_EXCEPTIONS.containsKey(countryCode)
          ? LANGUAGE_CODE_EXCEPTIONS.get(countryCode)
          : countryCode;
    }
    return languageCode;
  }


  private List<IduItemDerivedStandard> getIduItemDerivedStandards(
      String countryCode, ArticleId articleId) {

    return articleCacheService.getIduItemDerivedStandards(countryCode, articleId);
  }

  private Optional<StoreInformationGroup> getStoreSupplierInformationGroup(
      String countryCode, ArticleId articleId, Integer storeNumber) {

    return articleCacheService.getStoreInformationGroup(countryCode, articleId, storeNumber);
  }

  private ArticleCacheFullData getArticleCacheFullDataByArtVarBundle(
      Long articleNumber,
      Long variantNumber,
      Long bundleNumber,
      String countryCode,
      String salesLine,
      String languageCode,
      Integer storeNo)
      throws Exception {

    return articleCacheService.getArticleCacheFullDataByArtVarBundle(
        articleNumber, variantNumber, bundleNumber, countryCode, salesLine, languageCode, storeNo);
  }

  private Optional<IduItemDerivedStandard> getMultiLanguageDataByArtVarBundle(
      String countryCode, ArticleId articleId) {

    return articleCacheService.getMultiLanguageDataByArtVarBundle(countryCode, articleId);
  }

  private ArticleMasterData transformArticleCacheFullToArticleMasterData(
          ArticleCacheFullData articleCacheFullData,
          String countryCode,
          Integer storeNumber,
          String languageCode,
          ArticleId articleId) {

    if (articleCacheFullData != null) {

      SimplePromotionArticleData promotion =
              getActivePromotionForToday(countryCode, storeNumber, languageCode, articleId);

      return new ArticleMasterData()
              .setArticleDescription(articleCacheFullData.getArticleDescription())
              .setBarcode(articleCacheFullData.getBarcode())
              .setCommercialSupplier(articleCacheFullData.getCommercialSupplier())
              .setPresentationMerchandiseGroup(articleCacheFullData.getPresentationMerchandiseGroup())
              .setPromotionId(promotion.getPromotionId())
              .setPromotionDescription(promotion.getPromotionDescription())
              .setPromotionStartDate(promotion.getPromotionStart())
              .setPromotionEndDate(promotion.getPromotionEnd())
              .setSellingBundleContent(articleCacheFullData.getSellingBundleContent())
              .setWeightArticle(articleCacheFullData.getWeightArticle())
              .setNetWeight(articleCacheFullData.getNetWeight())
              .setSubsystemArticleNumber(articleCacheFullData.getSubsysArticleNumber())
              .setVatPercent(articleCacheFullData.getVatPercent())
              .setPieceNetSellPrice(articleCacheFullData.getPieceNetSellPrice())
              .setBundleNetSellPrice(articleCacheFullData.getBundleNetSellPrice())
              .setPromotionBundleNetPrice(
                      promotion.getPromotionNetSalesPricePerBundle() != null
                              ? promotion.getPromotionNetSalesPricePerBundle()
                              : 0)
              .setVariantDescription(articleCacheFullData.getVariantDescription())
              .setArticleType(articleCacheFullData.getArticleType())
              .setArticleClass(articleCacheFullData.getArticleClass())
              .setFoodNonfoodIndicator(articleCacheFullData.getFoodNonfoodIndicator())
              .setPresentationMainMerchandiseGroup(
                      articleCacheFullData.getPresentationMainMerchandiseGroup())
              .setPresentationSubMerchandiseGroup(
                      articleCacheFullData.getPresentationSubMerchandiseGroup())
              .setSupplierNumber(articleCacheFullData.getSupplierNumber())
              .setProcurementCode(articleCacheFullData.getProcurementCode())
              .setAssortmentCode(articleCacheFullData.getAssortmentCode())
              .setBlockingCode(articleCacheFullData.getBlockingCode())
              .setActivationStatus(articleCacheFullData.getActivationStatus())
              .setExpiryDays(articleCacheFullData.getExpiryDays())
              .setPieceNetBuyingPrice(articleCacheFullData.getPieceNetBuyingPrice())
              .setBundleNetBuyingPrice(articleCacheFullData.getBundleNetBuyingPrice())
              .setPieceNetNetBuyingPrice(articleCacheFullData.getPieceNetNetBuyingPrice())
              .setBundleNetNetBuyingPrice(articleCacheFullData.getBundleNetNetBuyingPrice())
              .setArticleNoEmptiesSales(articleCacheFullData.getArticleNoEmptiesSales())
              .setBrandName(articleCacheFullData.getBrandName())
              .setMinimumRetailPrice(articleCacheFullData.getMinimumRetailPrice())
              .setCountryOfOrigin(articleCacheFullData.getCountryOfOrigin())
              .setPackageFactor(articleCacheFullData.getPackageFactor())
              .setAlcoholClass(articleCacheFullData.getAlcoholClass())
              .setTraceabilityIndicator(articleCacheFullData.getTraceabilityIndicator())
              .setCoreAssortmentIndicator(articleCacheFullData.getCoreAssortmentIndicator())
              .setAssignedStoreSupplierType(articleCacheFullData.getAssignedStoreSupplierType())
              .setInOutIndicator(articleCacheFullData.getInOutIndicator())
              .setMinimumGrossSalesPricePerUnit(
                      articleCacheFullData.getMinimumGrossSalesPricePerUnit())
              .setBasPriceContent(articleCacheFullData.getBasPriceContent())
              .setBasPriceContentMeasureUnit(articleCacheFullData.getBasPriceContentMeasureUnit())
              .setBasPriceContentUnit(articleCacheFullData.getBasPriceContentUnit())
              .setBasPriceBaseFactor(articleCacheFullData.getBasPriceBaseFactor());
    }
    return null;
  }

  public SimplePromotionArticleData getActivePromotionForToday(
      String countryCode, Integer storeNumber, String languageCode, ArticleId articleId) {
    PromotionArticleData promotionArticleData =
        getPromotionArticle(countryCode, storeNumber, articleId).stream()
            .filter(promotion -> promotion.getValid() != null && promotion.getValid())
            .filter(
                promotion ->
                    isPromotionActiveToday(
                        promotion.getPromotionStart(), promotion.getPromotionEnd(), promotion.getPromotionChannel()))
            .min(Comparator.comparing(PromotionArticleData::getPromotionEnd))
            .orElse(PromotionArticleData.builder().build());
    Optional<PromotionDescription> promotionDescription = Optional.empty();
    if (!CollectionUtils.isEmpty(promotionArticleData.getDescriptions())) {
      promotionDescription =
          promotionArticleData.getDescriptions().stream()
              .filter(promotionDesc -> (promotionDesc.getLanguage().equalsIgnoreCase(languageCode)))
              .findFirst();
      if (promotionDescription.isEmpty()) {
        promotionDescription =
            promotionArticleData.getDescriptions().stream()
                .filter(
                    promotionDesc ->
                        (promotionDesc.getLanguage().equalsIgnoreCase(DEFAULT_LANGUAGE_CODE)))
                .findFirst();
      }
    }

    return SimplePromotionArticleData.builder()
        .promotionId(promotionArticleData.getPromotionId())
        .promotionDescription(
            promotionDescription.map(PromotionDescription::getDescription).orElse(null))
        .promotionStart(promotionArticleData.getPromotionStart())
        .promotionEnd(promotionArticleData.getPromotionEnd())
        .promotionNetSalesPricePerBundle(promotionArticleData.getPromotionNetSalesPricePerBundle())
        .build();
  }

  private boolean isPromotionActiveToday(LocalDate startDate, LocalDate endDate, String promotionChannelInput) {
    if (!StringUtils.containsIgnoreCase(promotionChannel,promotionChannelInput)) {
      return false;
    }
    if (startDate != null && endDate != null) {
      return ((now().isAfter(startDate) || now().equals(startDate))
          && (now().isBefore(endDate) || now().equals(endDate)));
    }
    return false;
  }

  private String getEnvironment(String countryCode) {
    if ("dev".equalsIgnoreCase(k8sEnvironment)
        || "pp".equalsIgnoreCase(k8sEnvironment)
        || "local".equalsIgnoreCase(k8sEnvironment)) {

      if (Set.of("IN", "CZ", "SK").contains(countryCode)) {
        return "pp2";
      } else {
        return "pp1";
      }
    } else {
      return "prod";
    }
  }
}
