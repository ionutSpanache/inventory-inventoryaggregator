package net.metrosystems.inventoryaggregator.articleattributes.repository;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawArticleStoreMasterData;
import net.metrosystems.inventoryaggregator.articlestoremasterdata.model.ArticleStoreMasterData;
import net.metrosystems.inventoryaggregator.articlestoremasterdata.service.AclArticleStoreMasterDataService;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.articleCacheStoreExceptions.model.ArticleCacheStoreExceptionsDataGroup;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.articleCacheStoreExceptions.model.ArticleCacheStoreExceptionsIhdsTree;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.articleCacheStoreExceptions.model.ArticleCacheStoreExceptionsIncident;
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
  private final ExecutorService forkJoinPoolCustom;

  @Async("asyncExecutor")
  public CompletableFuture<RawArticleStoreMasterData> getRawArticleStoreMasterData(ArticleId articleId, String countryCode,
      String salesLine, Integer storeNo) throws ExecutionException, InterruptedException {

    Map<ArticleId, RawArticleStoreMasterData> articleStoreMasterData = getSynchronouslyRawArticleStoreMasterData(
        List.of(articleId), countryCode, salesLine, storeNo);

    return CompletableFuture.completedFuture(
        articleStoreMasterData.isEmpty() ? RawArticleStoreMasterData.builder().build() : articleStoreMasterData.get(articleId));
  }

  @Async("asyncExecutor")
  public CompletableFuture<Map<ArticleId, ArticleStoreMasterData>> getArticleStoreMasterFromAcl(
      List<ArticleId> articleIdList, String countryCode, String salesLine, Integer storeNo) {

    List<ArticleStoreMasterData> articleStoreMasterDataList =
        aclArticleStoreMasterDataService.getArticleStoreMasterDataByArtVarBundle(
            countryCode, salesLine, storeNo, articleIdList);
    if (CollectionUtils.isEmpty(articleStoreMasterDataList)) {
        return CompletableFuture.completedFuture(Collections.emptyMap());
    }

    return CompletableFuture.completedFuture(
        articleStoreMasterDataList.stream()
            .filter(
                articleStoreMasterData ->
                    articleStoreMasterData != null
                        && articleStoreMasterData.getArticleNumber() != null
                        && articleStoreMasterData.getVariantNumber() != null
                        && articleStoreMasterData.getBundleNumber() != null)
            .collect(
                Collectors.toMap(
                    key ->
                        ArticleId.builder()
                            .articleNumber(Long.valueOf(key.getArticleNumber()))
                            .variantNumber(Long.valueOf(key.getVariantNumber()))
                            .bundleNumber(Long.valueOf(key.getBundleNumber()))
                            .build(),
                    value -> value)));
  }

  public Map<ArticleId, RawArticleStoreMasterData> getSynchronouslyRawArticleStoreMasterData(
      List<ArticleId> articleIdList, String countryCode, String salesLine, Integer storeNo)
      throws ExecutionException, InterruptedException {
    Map<ArticleId, ArticleStoreMasterData> articleIdArticleStoreMasterDataMap =
        aclArticleStoreMasterDataService
            .getArticleStoreMasterDataByArtVarBundle(countryCode, salesLine, storeNo, articleIdList)
            .stream()
            .collect(
                Collectors.toMap(
                    key ->
                        ArticleId.builder()
                            .articleNumber(Long.valueOf(key.getArticleNumber()))
                            .variantNumber(Long.valueOf(key.getVariantNumber()))
                            .bundleNumber(Long.valueOf(key.getBundleNumber()))
                            .build(),
                    value -> value));

    Map<ArticleId, ArticleCacheStoreExceptionsIhdsTree>
        articleIdArticleCacheStoreExceptionsIhdsTreeMap =
            getArticleIdArticleCacheStoreExceptionIhdsTree(
                    countryCode, salesLine, storeNo, new HashSet<>(articleIdList))
                .get();

    return getArticleIdRawArticleStoreMasterDataMap(
            articleIdArticleStoreMasterDataMap, articleIdArticleCacheStoreExceptionsIhdsTreeMap);
  }

    public Map<ArticleId, RawArticleStoreMasterData> getArticleIdRawArticleStoreMasterDataMap(
            Map<ArticleId, ArticleStoreMasterData> articleIdArticleStoreMasterDataMap,
            Map<ArticleId, ArticleCacheStoreExceptionsIhdsTree>
                    articleIdArticleCacheStoreExceptionsIhdsTreeMap) {
        Map<ArticleId ,ArticleCacheStoreExceptionsDataGroup> articleIdArticleCacheStoreExceptionsDataGroup100Map = new HashMap<>();
        Map<ArticleId ,ArticleCacheStoreExceptionsDataGroup> articleIdArticleCacheStoreExceptionsDataGroup200Map = new HashMap<>();
        Map<ArticleId ,ArticleCacheStoreExceptionsDataGroup> articleIdArticleCacheStoreExceptionsDataGroup300Map = new HashMap<>();

        articleIdArticleCacheStoreExceptionsIhdsTreeMap.forEach(
                (articleId, articleCacheStoreExceptionsIhdsTree) -> {
                    if(articleCacheStoreExceptionsIhdsTree != null) {

                        ArticleCacheStoreExceptionsIncident articleCacheStoreExceptionsIncident =
                                articleCacheStoreExceptionsIhdsTree.getTransaction().getArticleCacheIncident();

                        articleIdArticleCacheStoreExceptionsDataGroup100Map.put(
                                articleId, articleCacheStoreExceptionsIncident.getArticleCacheStoreExceptionsDataGroupById(100));
                        articleIdArticleCacheStoreExceptionsDataGroup200Map.put(
                                articleId, articleCacheStoreExceptionsIncident.getArticleCacheStoreExceptionsDataGroupById(200));
                        articleIdArticleCacheStoreExceptionsDataGroup300Map.put(
                                articleId, articleCacheStoreExceptionsIncident.getArticleCacheStoreExceptionsDataGroupById(300));
                    }
                }
        );

        Map<ArticleId, RawArticleStoreMasterData> result = new HashMap<>();

        articleIdArticleStoreMasterDataMap.forEach(
                (articleId, articleStoreMasterData) -> {

                    ArticleCacheStoreExceptionsDataGroup articleIdArticleCacheStoreExceptionsDataGroup100 =
                            articleIdArticleCacheStoreExceptionsDataGroup100Map.get(articleId);
                    ArticleCacheStoreExceptionsDataGroup articleIdArticleCacheStoreExceptionsDataGroup200 =
                            articleIdArticleCacheStoreExceptionsDataGroup200Map.get(articleId);
                    ArticleCacheStoreExceptionsDataGroup articleIdArticleCacheStoreExceptionsDataGroup300 =
                            articleIdArticleCacheStoreExceptionsDataGroup300Map.get(articleId);


                    RawArticleStoreMasterData rawArticleStoreMasterData =
                            RawArticleStoreMasterData.builder()
                                    .departmentNumber(articleStoreMasterData.getDepartmentNumber())
                                    .shelfEdgeTicketType(articleStoreMasterData.getShelfEdgeTicketType())
                                    .leadTime(articleStoreMasterData.getLeadTime())
                                    .orderProposalBlockingCode(articleStoreMasterData.getOrderProposalBlockingCode())
                                    .build();

                    if(articleIdArticleCacheStoreExceptionsDataGroup100 != null){
                        rawArticleStoreMasterData.setStorePieceNetSellPrice(articleIdArticleCacheStoreExceptionsDataGroup100.getNetSalesPricePerPiece());
                        rawArticleStoreMasterData.setStoreBundleNetSellPrice(articleIdArticleCacheStoreExceptionsDataGroup100.getNetSalesPricePerBundle());
                        rawArticleStoreMasterData.setStorePieceGrossSellPrice(articleIdArticleCacheStoreExceptionsDataGroup100.getGrossSalesPricePerPiece());
                        rawArticleStoreMasterData.setStoreBundleGrossSellPrice(articleIdArticleCacheStoreExceptionsDataGroup100.getGrossSalesPricePerBundle());
                        rawArticleStoreMasterData.setStorePriceValidFrom(articleIdArticleCacheStoreExceptionsDataGroup100.getValidFrom());
                        rawArticleStoreMasterData.setStorePriceValidTo(articleIdArticleCacheStoreExceptionsDataGroup100.getValidTo());
                    }
                    if(articleIdArticleCacheStoreExceptionsDataGroup200 != null){
                        rawArticleStoreMasterData.setStorePromotionPieceNetPrice(articleIdArticleCacheStoreExceptionsDataGroup200.getPromotionNetSalesPricePerPiece());
                        rawArticleStoreMasterData.setStorePromotionBundleNetPrice(articleIdArticleCacheStoreExceptionsDataGroup200.getPromotionNetSalesPricePerBundle());
                        rawArticleStoreMasterData.setStorePromotionPieceGrossPrice(articleIdArticleCacheStoreExceptionsDataGroup200.getPromotionGrossSalesPricePerPiece());
                        rawArticleStoreMasterData.setStorePromotionBundleGrossPrice(articleIdArticleCacheStoreExceptionsDataGroup200.getPromotionGrossSalesPricePerBundle());
                        rawArticleStoreMasterData.setStorePromotionValidFrom(articleIdArticleCacheStoreExceptionsDataGroup200.getValidFrom());
                        rawArticleStoreMasterData.setStorePromotionValidTo(articleIdArticleCacheStoreExceptionsDataGroup200.getValidTo());
                    }
                    if(articleIdArticleCacheStoreExceptionsDataGroup300 != null){
                        rawArticleStoreMasterData.setSalesBlockingCode(articleIdArticleCacheStoreExceptionsDataGroup300.getSalesBlockingCode());
                    }

                    result.put(articleId, rawArticleStoreMasterData);
                }
        );
        return result;
    }

  @Async("asyncExecutor")
  public CompletableFuture<Map<ArticleId, ArticleCacheStoreExceptionsIhdsTree>>
      getArticleIdArticleCacheStoreExceptionIhdsTree(
          String countryCode, String salesLine, Integer storeNo, Set<ArticleId> articleIds)
          throws ExecutionException, InterruptedException {

    Map<ArticleId, ArticleCacheStoreExceptionsIhdsTree>
        articleIdArticleCacheStoreExceptionsIhdsTreeMap = new HashMap<>();

    forkJoinPoolCustom
        .submit(
            () ->
                articleIds.parallelStream()
                    .forEach(
                        articleId -> {
                          try {
                            articleIdArticleCacheStoreExceptionsIhdsTreeMap.put(
                                articleId,
                                articleCacheService.getArticleCacheStoreExceptions(
                                    articleId.getArticleNumber(),
                                    articleId.getVariantNumber(),
                                    articleId.getBundleNumber(),
                                    countryCode,
                                    salesLine,
                                    storeNo));
                          } catch (Exception e) {
                            log.error(
                                "RawArticleStoreMasterDataRepository.getRawArticleStoreMasterDataForArticleIdList error: {}",
                                e.getMessage(),
                                e);
                          }
                        }))
        .get();

    return CompletableFuture.completedFuture(articleIdArticleCacheStoreExceptionsIhdsTreeMap);
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
            .storePriceValidFrom(articleCacheStoreExceptionsDataGroup100.getValidFrom())
            .storePromotionValidTo(articleCacheStoreExceptionsDataGroup100.getValidTo())
            .storePromotionPieceNetPrice(
                articleCacheStoreExceptionsDataGroup200.getPromotionNetSalesPricePerPiece())
            .storePromotionBundleNetPrice(
                articleCacheStoreExceptionsDataGroup200.getPromotionNetSalesPricePerBundle())
            .storePromotionPieceGrossPrice(
                articleCacheStoreExceptionsDataGroup200.getPromotionGrossSalesPricePerPiece())
            .storePromotionBundleGrossPrice(
                articleCacheStoreExceptionsDataGroup200.getPromotionGrossSalesPricePerBundle())
            .storePromotionValidFrom(articleCacheStoreExceptionsDataGroup200.getValidFrom())
            .storePromotionValidTo(articleCacheStoreExceptionsDataGroup200.getValidTo())
            .leadTime(null)
            .build();

    return CompletableFuture.completedFuture(rawArticleStoreMasterData);
  }

}
