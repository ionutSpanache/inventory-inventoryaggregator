package net.metrosystems.inventoryaggregator.articleattributes.repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.ArticleMasterDataForArticleId;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.model.ArticleCacheFullData;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.service.ArticleCacheService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ArticleMasterDataRepository {
  private final ArticleCacheService articleCacheService;


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
  public CompletableFuture<List<ArticleMasterDataForArticleId>>
      getArticleMasterDataForArticleIdList(
          List<ArticleId> articleIdList,
          String countryCode,
          String salesLine,
          String languageCode,
          Integer storeNo)
          throws Exception {
    ForkJoinPool forkJoinPool = createForkJoinPool();
    List<ArticleMasterDataForArticleId> articleMasterDataForArticleIds =
        forkJoinPool
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
                        .collect(Collectors.toList()))
            .get();
    forkJoinPool.shutdown();
    return CompletableFuture.completedFuture(articleMasterDataForArticleIds);
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
          articleCacheFullData.getExpiryDays());
    }
    return null;
  }

  private static class CustomForkJoinWorkerThread extends ForkJoinWorkerThread {
    CustomForkJoinWorkerThread(ForkJoinPool pool) {
      super(pool);
      setContextClassLoader(Thread.currentThread().getContextClassLoader());
    }
  }

  private ForkJoinPool createForkJoinPool() {
    return new ForkJoinPool(
        //        ForkJoinPool.getCommonPoolParallelism(),
        10, CustomForkJoinWorkerThread::new, null, false);
  }
}
