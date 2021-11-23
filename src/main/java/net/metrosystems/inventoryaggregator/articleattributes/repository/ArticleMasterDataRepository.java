package net.metrosystems.inventoryaggregator.articleattributes.repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.*;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.ArticleMasterDataForArticleId;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard.IduItemDerivedStandard;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.model.ArticleCacheFullData;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.service.ArticleCacheService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

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
      return new ArticleMasterData(
          articleCacheFullData.getArticleDescription(),
          articleCacheFullData.getBarcode(),
          articleCacheFullData.getCommercialSupplier(),
          articleCacheFullData.getPresentationMerchandiseGroup(),
          articleCacheFullData.getPromotionId(),
          articleCacheFullData.getSellingBundleContent(),
          articleCacheFullData.getWeightArticle(),
          articleCacheFullData.getSubsysArticleNumber(),
          articleCacheFullData.getVatPercent(),
          articleCacheFullData.getPieceNetSellPrice(),
          articleCacheFullData.getBundleNetSellPrice(),
          articleCacheFullData.getPromotionBundleNetPrice(),
          articleCacheFullData.getVariantDescription(),
          articleCacheFullData.getArticleType(),
          articleCacheFullData.getArticleClass(),
          articleCacheFullData.getFoodNonfoodIndicator(),
          articleCacheFullData.getPresentationMainMerchandiseGroup(),
          articleCacheFullData.getPresentationSubMerchandiseGroup(),
          articleCacheFullData.getSupplierNumber(),
          articleCacheFullData.getProcurementCode(),
          articleCacheFullData.getAssortmentCode(),
          articleCacheFullData.getBlockingCode(),
          articleCacheFullData.getActivationStatus(),
          articleCacheFullData.getExpiryDays(),
          articleCacheFullData.getPieceNetBuyingPrice(),
          articleCacheFullData.getBundleNetBuyingPrice(),
          articleCacheFullData.getPieceNetNetBuyingPrice(),
          articleCacheFullData.getBundleNetNetBuyingPrice(),
          articleCacheFullData.getArticleNoEmptiesSales(),
          articleCacheFullData.getBrandName(),
          articleCacheFullData.getMinimumRetailPrice(),
          articleCacheFullData.getCountryOfOrigin(),
          articleCacheFullData.getIsTradeItemExcludedFromDiscounts());
    }
    return null;
  }
}
