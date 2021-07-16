package net.metrosystems.inventoryaggregator.articleattributes.repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawArticleStoreMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.RawArticleStoreMasterDataForArticleId;
import net.metrosystems.inventoryaggregator.articlestoremasterdata.model.ArticleStoreMasterData;
import net.metrosystems.inventoryaggregator.articlestoremasterdata.service.AclArticleStoreMasterDataService;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.articleCacheStoreExceptions.model.ArticleCacheStoreExceptionsDataGroup;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.articleCacheStoreExceptions.model.ArticleCacheStoreExceptionsIhdsTree;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.service.ArticleCacheService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

@Repository
@Slf4j
@RequiredArgsConstructor
public class RawArticleStoreMasterDataRepository {

  private final AclArticleStoreMasterDataService aclArticleStoreMasterDataService;
  private final ArticleCacheService articleCacheService;

  @Qualifier("ForkJoinCustomPool")
  private final ForkJoinPool forkJoinPoolCustom;

  @Async("asyncExecutor")
  public CompletableFuture<RawArticleStoreMasterData> getRawArticleStoreMasterData(ArticleId articleId, String countryCode,
      String salesLine, Integer storeNo) {

    return CompletableFuture.completedFuture(
        getSynchronouslyRawArticleStoreMasterData(
            articleId, countryCode, salesLine, storeNo));
  }

  @Async("asyncExecutor")
  public CompletableFuture<List<RawArticleStoreMasterDataForArticleId>>
      getRawArticleStoreMasterDataForArticleIdList(
          List<ArticleId> articleIdList,
          String countryCode,
          String salesLine,
          String languageCode,
          Integer storeNo)
          throws ExecutionException, InterruptedException {
    List<RawArticleStoreMasterDataForArticleId> rawArticleStoreMasterDataForArticleIdList = forkJoinPoolCustom
            .submit(
                () ->
                    articleIdList.parallelStream()
                        .map(
                            articleId -> {
                              try {
                                return RawArticleStoreMasterDataForArticleId.builder()
                                    .articleId(articleId)
                                    .rawArticleStoreMasterData(
                                        getSynchronouslyRawArticleStoreMasterData(
                                            articleId,
                                            countryCode,
                                            salesLine,
                                            storeNo))
                                    .build();
                              } catch (Exception e) {
                                log.error(
                                    "RawArticleStoreMasterDataRepository.getRawArticleStoreMasterDataForArticleIdList error: {}",
                                    e.getMessage(),
                                    e);
                                return null;
                              }
                            })
                        .collect(Collectors.toList()))
            .get();
    return CompletableFuture.completedFuture(rawArticleStoreMasterDataForArticleIdList);
  }

  public RawArticleStoreMasterData getSynchronouslyRawArticleStoreMasterData(
      ArticleId articleId,
      String countryCode,
      String salesLine,
      Integer storeNo) {
    List<ArticleStoreMasterData> articleStoreMasterDataList =
        aclArticleStoreMasterDataService.getArticleStoreMasterDataByArtVarBundle(
            countryCode,
            salesLine,
            storeNo,
            Math.toIntExact(articleId.getArticleNumber()),
            Math.toIntExact(articleId.getVariantNumber()),
            Math.toIntExact(articleId.getBundleNumber()));

    ArticleCacheStoreExceptionsIhdsTree articleCacheStoreExceptionsIhdsTree =
        articleCacheService.getArticleCacheStoreExceptions(
            articleId.getArticleNumber(),
            articleId.getVariantNumber(),
            articleId.getBundleNumber(),
            countryCode,
            salesLine,
            storeNo);
    ArticleCacheStoreExceptionsDataGroup articleCacheStoreExceptionsDataGroup100 =
        new ArticleCacheStoreExceptionsDataGroup();
    ArticleCacheStoreExceptionsDataGroup articleCacheStoreExceptionsDataGroup200 =
        new ArticleCacheStoreExceptionsDataGroup();
    ArticleCacheStoreExceptionsDataGroup articleCacheStoreExceptionsDataGroup300 =
        new ArticleCacheStoreExceptionsDataGroup();

    if (articleCacheStoreExceptionsIhdsTree != null) {
      articleCacheStoreExceptionsDataGroup100 =
          articleCacheStoreExceptionsIhdsTree
              .getTransaction()
              .getArticleCacheIncident()
              .getArticleCacheStoreExceptionsDataGroupById(100);
      articleCacheStoreExceptionsDataGroup200 =
          articleCacheStoreExceptionsIhdsTree
              .getTransaction()
              .getArticleCacheIncident()
              .getArticleCacheStoreExceptionsDataGroupById(200);
      articleCacheStoreExceptionsDataGroup300 =
          articleCacheStoreExceptionsIhdsTree
              .getTransaction()
              .getArticleCacheIncident()
              .getArticleCacheStoreExceptionsDataGroupById(300);
    }

    return RawArticleStoreMasterData.builder()
        .departmentNumber(
            articleStoreMasterDataList == null || articleStoreMasterDataList.isEmpty()
                ? null
                : articleStoreMasterDataList.get(0).getDepartmentNumber())
        .shelfEdgeTicketType(
            articleStoreMasterDataList == null || articleStoreMasterDataList.isEmpty()
                ? null
                : articleStoreMasterDataList.get(0).getShelfEdgeTicketType())
        .salesBlockingCode(articleCacheStoreExceptionsDataGroup300.getSalesBlockingCode())
        .storePieceNetSellPrice(articleCacheStoreExceptionsDataGroup100.getNetSalesPricePerPiece())
        .storeBundleNetSellPrice(
            articleCacheStoreExceptionsDataGroup100.getNetSalesPricePerBundle())
        .storePieceGrossSellPrice(
            articleCacheStoreExceptionsDataGroup100.getGrossSalesPricePerPiece())
        .storeBundleGrossSellPrice(
            articleCacheStoreExceptionsDataGroup100.getGrossSalesPricePerBundle())
        .storePromotionPieceNetPrice(
            articleCacheStoreExceptionsDataGroup200.getPromotionNetSalesPricePerPiece())
        .storePromotionBundleNetPrice(
            articleCacheStoreExceptionsDataGroup200.getPromotionNetSalesPricePerBundle())
        .storePromotionPieceGrossPrice(
            articleCacheStoreExceptionsDataGroup200.getPromotionGrossSalesPricePerPiece())
        .storePromotionBundleGrossPrice(
            articleCacheStoreExceptionsDataGroup200.getPromotionGrossSalesPricePerBundle())
        .leadTime(
            articleStoreMasterDataList == null || articleStoreMasterDataList.isEmpty()
                ? null
                : articleStoreMasterDataList.get(0).getLeadTime())
        .orderProposalBlockingCode(CollectionUtils.isEmpty(articleStoreMasterDataList)
                    ? null
                    : articleStoreMasterDataList.get(0).getOrderProposalBlockingCode())
        .build();
  }

  @Async("asyncExecutor")
  public CompletableFuture<RawArticleStoreMasterData> getDiscountArticleStoreMasterData(
      ArticleId articleId,
      String countryCode,
      String salesLine,
      String languageCode,
      Integer storeNo) {
    ArticleCacheStoreExceptionsIhdsTree articleCacheStoreExceptionsIhdsTree =
        articleCacheService.getArticleCacheStoreExceptions(
            articleId.getArticleNumber(),
            articleId.getVariantNumber(),
            articleId.getBundleNumber(),
            countryCode,
            salesLine,
            storeNo);
    ArticleCacheStoreExceptionsDataGroup articleCacheStoreExceptionsDataGroup100 =
        new ArticleCacheStoreExceptionsDataGroup();
    ArticleCacheStoreExceptionsDataGroup articleCacheStoreExceptionsDataGroup200 =
        new ArticleCacheStoreExceptionsDataGroup();
    ArticleCacheStoreExceptionsDataGroup articleCacheStoreExceptionsDataGroup300 =
        new ArticleCacheStoreExceptionsDataGroup();

    if (articleCacheStoreExceptionsIhdsTree != null) {
      articleCacheStoreExceptionsDataGroup100 =
          articleCacheStoreExceptionsIhdsTree
              .getTransaction()
              .getArticleCacheIncident()
              .getArticleCacheStoreExceptionsDataGroupById(100);
      articleCacheStoreExceptionsDataGroup200 =
          articleCacheStoreExceptionsIhdsTree
              .getTransaction()
              .getArticleCacheIncident()
              .getArticleCacheStoreExceptionsDataGroupById(200);
      articleCacheStoreExceptionsDataGroup300 =
          articleCacheStoreExceptionsIhdsTree
              .getTransaction()
              .getArticleCacheIncident()
              .getArticleCacheStoreExceptionsDataGroupById(300);
    }

    RawArticleStoreMasterData rawArticleStoreMasterData =
        RawArticleStoreMasterData.builder()
            .departmentNumber(null)
            .shelfEdgeTicketType(null)
            .salesBlockingCode(articleCacheStoreExceptionsDataGroup300.getSalesBlockingCode())
            .storePieceNetSellPrice(
                articleCacheStoreExceptionsDataGroup100.getNetSalesPricePerPiece())
            .storeBundleNetSellPrice(
                articleCacheStoreExceptionsDataGroup100.getNetSalesPricePerBundle())
            .storePieceGrossSellPrice(
                articleCacheStoreExceptionsDataGroup100.getGrossSalesPricePerPiece())
            .storeBundleGrossSellPrice(
                articleCacheStoreExceptionsDataGroup100.getGrossSalesPricePerBundle())
            .storePromotionPieceNetPrice(
                articleCacheStoreExceptionsDataGroup200.getPromotionNetSalesPricePerPiece())
            .storePromotionBundleNetPrice(
                articleCacheStoreExceptionsDataGroup200.getPromotionNetSalesPricePerBundle())
            .storePromotionPieceGrossPrice(
                articleCacheStoreExceptionsDataGroup200.getPromotionGrossSalesPricePerPiece())
            .storePromotionBundleGrossPrice(
                articleCacheStoreExceptionsDataGroup200.getPromotionGrossSalesPricePerBundle())
            .leadTime(null)
            .build();

    return CompletableFuture.completedFuture(rawArticleStoreMasterData);
  }

}
