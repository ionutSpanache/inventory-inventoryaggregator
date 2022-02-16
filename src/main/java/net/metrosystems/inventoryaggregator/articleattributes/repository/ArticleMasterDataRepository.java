package net.metrosystems.inventoryaggregator.articleattributes.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.ProductLifeCycleData;
import net.metrosystems.inventoryaggregator.articleattributes.model.ProductLifeCycleForArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.ArticleMasterDataForArticleId;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard.IduItemDerivedStandard;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.model.ArticleCacheFullData;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.service.ArticleCacheService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ArticleMasterDataRepository {

  private final ArticleCacheService articleCacheService;

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
    public CompletableFuture<ProductLifeCycleData> getProductLifeCycleData(
            String countryCode, ArticleId articleId) {
        ProductLifeCycleData productLifeCycleData = getLifeCycleData(countryCode, articleId);

        return CompletableFuture.completedFuture(productLifeCycleData);
    }

    @Async("asyncExecutor")
    public CompletableFuture<String> getChannelDeliveryFlag(
            String countryCode, ArticleId articleId, Integer storeNumber) {
        String channelDeliveryFlag = getDeliveryFlag(countryCode, articleId, storeNumber)
                .orElse(StringUtils.EMPTY);

        return CompletableFuture.completedFuture(channelDeliveryFlag);
    }

  @Async("asyncExecutor")
  public CompletableFuture<List<ArticleMasterDataForArticleId>>
      getArticleMasterDataForArticleIdList(
          List<ArticleId> articleIdList,
          String countryCode,
          String salesLine,
          String languageCode,
          Integer storeNo)
          throws Exception {
    List<ArticleMasterDataForArticleId> articleMasterDataForArticleIds =
        forkJoinPoolCustom
            .submit(
                () ->
                    articleIdList.parallelStream()
                        .map(
                            articleId -> {
                              ArticleCacheFullData articleCacheFullData;
                              try {
                                articleCacheFullData =
                                    articleCacheService
                                        .getArticleCacheFullDataByArtVarBundleForArticleId(
                                            articleId.getArticleNumber(),
                                            articleId.getVariantNumber(),
                                            articleId.getBundleNumber(),
                                            countryCode,
                                            salesLine,
                                            languageCode,
                                            storeNo);
                              } catch (Exception e) {
                                log.error(
                                    "ArticleMasterDataRepository.getArticleMasterDataForArticleIdList error: {}",
                                    e.getMessage(),
                                    e);
                                return null;
                              }
                              return ArticleMasterDataForArticleId.builder()
                                  .articleId(articleId)
                                  .articleMasterData(
                                      transformArticleCacheFullToArticleMasterData(
                                          articleCacheFullData))
                                  .build();
                            })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
            .get();
    return CompletableFuture.completedFuture(articleMasterDataForArticleIds);
  }

    @Async("asyncExecutor")
    public CompletableFuture<List<ProductLifeCycleForArticleId>>
    getProductLifeCycleForArticleIds(
            List<ArticleId> articleIdList,
            String countryCode)
            throws Exception {
        List<ProductLifeCycleForArticleId> productLifeCycleForArticleIds =
                forkJoinPoolCustom
                        .submit(
                                () ->
                                        articleIdList.parallelStream()
                                                .map(
                                                        articleId -> {
                                                            ProductLifeCycleData productLifeCycleData;
                                                            try {
                                                                productLifeCycleData =
                                                                        getLifeCycleData(countryCode, articleId);
                                                            } catch (Exception e) {
                                                                log.error(
                                                                        "ArticleMasterDataRepository.getArticleMasterDataForArticleIdList error: {}",
                                                                        e.getMessage(),
                                                                        e);
                                                                return null;
                                                            }
                                                            return ProductLifeCycleForArticleId.builder()
                                                                    .articleId(articleId)
                                                                    .productLifeCycleData(productLifeCycleData)
                                                                    .build();
                                                        })
                                                .filter(Objects::nonNull)
                                                .collect(Collectors.toList()))
                        .get();
        return CompletableFuture.completedFuture(productLifeCycleForArticleIds);
    }


  private ProductLifeCycleData getLifeCycleData(String countryCode, ArticleId articleId) {

      return  articleCacheService.getArticleProductLifeCycleData(countryCode, articleId);
  }

    private Optional<String> getDeliveryFlag(String countryCode, ArticleId articleId, Integer storeNumber) {

        return  articleCacheService.getChannelDeliveryFlag(countryCode, articleId, storeNumber);
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
            String countryCode,
            ArticleId articleId) {

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
          .setAlcoholClass(articleCacheFullData.getAlcoholClass());

    }
    return null;
  }
}
