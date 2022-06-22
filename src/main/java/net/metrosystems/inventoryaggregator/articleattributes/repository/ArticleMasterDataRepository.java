package net.metrosystems.inventoryaggregator.articleattributes.repository;

import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.*;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.document.model.Documents;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.document.service.DocumentsService;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard.IduItemDerivedStandard;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.model.ArticleCacheFullData;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.service.ArticleCacheService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ArticleMasterDataRepository {

  private final ArticleCacheService articleCacheService;
  private final DocumentsService documentsService;

  @Qualifier("ForkJoinCustomPool")
  private final ForkJoinPool forkJoinPoolCustom;

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
        transformArticleCacheFullToArticleMasterData(articleCacheFullData);

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

  @Async("asyncExecutor")
  public CompletableFuture<PromotionArticleData> getPromotionArticleData(
      String countryCode, Integer storeId, ArticleId articleId) {
    PromotionArticleData promotionArticleData =
        getPromotionArticle(countryCode, storeId, articleId);

    return CompletableFuture.completedFuture(promotionArticleData);
  }

  private PromotionArticleData getPromotionArticle(
      String countryCode, Integer storeId, ArticleId articleId) {

    return articleCacheService.getPromotionArticleData(countryCode, articleId, storeId);
  }

  @Async("asyncExecutor")
  public CompletableFuture<List<PromotionArticleDataForArticleId>>
      getPromotionDataArticlesForArticleIds(
          List<ArticleId> articleIdList, String countryCode, Integer storeId) throws Exception {
    List<PromotionArticleDataForArticleId> promotionArticleDataList =
        forkJoinPoolCustom
            .submit(
                () ->
                    articleIdList.parallelStream()
                        .map(
                            articleId -> {
                              PromotionArticleData promotionArticleData;
                              try {
                                promotionArticleData =
                                    getPromotionArticle(countryCode, storeId, articleId);
                              } catch (Exception e) {
                                log.error(
                                    "ArticleMasterDataRepository.getArticleMasterDataForArticleIdList error: {}",
                                    e.getMessage(),
                                    e);
                                return null;
                              }
                              return PromotionArticleDataForArticleId.builder()
                                  .articleId(articleId)
                                  .promotionArticleData(promotionArticleData)
                                  .build();
                            })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
            .get();
    return CompletableFuture.completedFuture(promotionArticleDataList);
  }

  @Async("asyncExecutor")
  public CompletableFuture<String> getChannelDeliveryFlag(
      String countryCode, ArticleId articleId, Integer storeNumber) {
    String channelDeliveryFlag =
        getDeliveryFlag(countryCode, articleId, storeNumber).orElse(StringUtils.EMPTY);

    return CompletableFuture.completedFuture(channelDeliveryFlag);
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
    Map<ArticleId, String> articlesDocumentUrlMap =
        documentsService.getDocumentsForMultipleArticles(
            countryCode, salesLine, languageCode, articleIdList);
    forkJoinPoolCustom
        .submit(
            () ->
                articleIdList.parallelStream()
                    .forEach(
                        articleId -> {
                          ArticleCacheFullData articleCacheFullData = null;
                          try {
                            articleCacheFullData =
                                articleCacheService
                                    .getArticleCacheFullDataByArtVarBundleForArticleId(
                                        storeNo, articlesDocumentUrlMap.get(articleId));
                          } catch (Exception e) {
                            log.error(
                                "ArticleMasterDataRepository.getArticleCacheFullDataByArtVarBundleForArticleId error: {}",
                                e.getMessage(),
                                e);
                          }
                          if (articleCacheFullData != null)
                            result.put(
                                articleId,
                                transformArticleCacheFullToArticleMasterData(articleCacheFullData));
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

  private List<IduItemDerivedStandard> getIduItemDerivedStandards(
      String countryCode, ArticleId articleId) {

    return articleCacheService.getIduItemDerivedStandards(countryCode, articleId);
  }

  private Optional<String> getDeliveryFlag(
      String countryCode, ArticleId articleId, Integer storeNumber) {

    return articleCacheService.getChannelDeliveryFlag(countryCode, articleId, storeNumber);
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
      ArticleCacheFullData articleCacheFullData) {
    if (articleCacheFullData != null) {
      return new ArticleMasterData()
          .setArticleDescription(articleCacheFullData.getArticleDescription())
          .setBarcode(articleCacheFullData.getBarcode())
          .setCommercialSupplier(articleCacheFullData.getCommercialSupplier())
          .setPresentationMerchandiseGroup(articleCacheFullData.getPresentationMerchandiseGroup())
          .setPromotionId(articleCacheFullData.getPromotionId())
          .setSellingBundleContent(articleCacheFullData.getSellingBundleContent())
          .setWeightArticle(articleCacheFullData.getWeightArticle())
          .setNetWeight(articleCacheFullData.getNetWeight())
          .setSubsystemArticleNumber(articleCacheFullData.getSubsysArticleNumber())
          .setVatPercent(articleCacheFullData.getVatPercent())
          .setPieceNetSellPrice(articleCacheFullData.getPieceNetSellPrice())
          .setBundleNetSellPrice(articleCacheFullData.getBundleNetSellPrice())
          .setPromotionBundleNetPrice(articleCacheFullData.getPromotionBundleNetPrice())
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
          .setIsTradeItemExcludedFromDiscounts(
              articleCacheFullData.getIsTradeItemExcludedFromDiscounts())
          .setGradeCodeReferenceFruitsVegetables(
              articleCacheFullData.getGradeCodeReferenceFruitsVegetables())
          .setPackageFactor(articleCacheFullData.getPackageFactor())
          .setAlcoholClass(articleCacheFullData.getAlcoholClass())
          .setTraceabilityIndicator(articleCacheFullData.getTraceabilityIndicator())
          .setCoreAssortmentIndicator(articleCacheFullData.getCoreAssortmentIndicator());
    }
    return null;
  }
}
