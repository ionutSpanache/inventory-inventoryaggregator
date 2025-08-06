package net.metrosystems.inventoryaggregator.articleattributes.repository;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleMasterId;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleStoreMasterData;
import net.metrosystems.inventoryaggregator.articleattributes.model.RawArticleStoreMasterData;
import net.metrosystems.inventoryaggregator.articlestoremasterdata.model.AclStoreMasterData;
import net.metrosystems.inventoryaggregator.articlestoremasterdata.service.AclArticleStoreMasterDataService;
import net.metrosystems.inventoryaggregator.common.model.StoreId;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

@Repository
@Slf4j
@RequiredArgsConstructor
public class RawArticleStoreMasterDataRepository {

  private final AclArticleStoreMasterDataService aclArticleStoreMasterDataService;

  @Async("asyncExecutor")
  public CompletableFuture<AclStoreMasterData> getAclStoreMasterData(
      ArticleId articleId, String countryCode, String salesLine, Integer storeNo) {

    Map<ArticleId, AclStoreMasterData> aclStoreMasterDataMap =
        getSynchronouslyAclStoreMasterData(List.of(articleId), countryCode, salesLine, storeNo);

    return CompletableFuture.completedFuture(
        aclStoreMasterDataMap.isEmpty()
            ? AclStoreMasterData.builder().build()
            : aclStoreMasterDataMap.get(articleId));
  }

  @Async("asyncExecutor")
  public CompletableFuture<Map<ArticleId, AclStoreMasterData>> getAclStoreMasterDataMap(
      List<ArticleId> articleIdList, String countryCode, String salesLine, Integer storeNo) {
    StoreId storeId = new StoreId(countryCode, salesLine, storeNo);
    List<AclStoreMasterData> articleStoreMasterDataList =
        aclArticleStoreMasterDataService.getAclStoreMasterDataByArtVarBundle(
            storeId, articleIdList);
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

  public @NotNull Map<ArticleId, AclStoreMasterData> getSynchronouslyAclStoreMasterData(
      List<ArticleId> articleIdList, String countryCode, String salesLine, Integer storeNo) {
    StoreId storeId = new StoreId(countryCode, salesLine, storeNo);
    return aclArticleStoreMasterDataService
        .getAclStoreMasterDataByArtVarBundle(storeId, articleIdList)
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
  }

  public Map<ArticleId, RawArticleStoreMasterData> getArticleIdRawArticleStoreMasterDataMap(
      Map<ArticleId, AclStoreMasterData> articleIdAclStoreMasterDataMap,
      Map<ArticleId, ArticleMasterId> articleIdArticleMasterIdMap) {

    Map<ArticleId, RawArticleStoreMasterData> result = new HashMap<>();

    articleIdAclStoreMasterDataMap.forEach(
        (articleId, aclStoreMasterData) ->
            result.put(
                articleId,
                getRawArticleStoreMasterData(
                    articleIdArticleMasterIdMap.get(articleId), aclStoreMasterData)));

    return result;
  }

  public RawArticleStoreMasterData getRawArticleStoreMasterData(
      ArticleMasterId articleMasterId, AclStoreMasterData aclStoreMasterData) {

    RawArticleStoreMasterData rawArticleStoreMasterData =
        RawArticleStoreMasterData.builder()
            .departmentNumber(aclStoreMasterData.getDepartmentNumber())
            .shelfEdgeTicketType(aclStoreMasterData.getShelfEdgeTicketType())
            .leadTime(aclStoreMasterData.getLeadTime())
            .orderProposalBlockingCode(aclStoreMasterData.getOrderProposalBlockingCode())
            .departmentName(aclStoreMasterData.getDepartmentName())
            .build();

    if (articleMasterId != null && articleMasterId.getArticleStoreMasterData() != null) {
      ArticleStoreMasterData articleStoreMasterData = articleMasterId.getArticleStoreMasterData();
      rawArticleStoreMasterData
          .setSalesBlockingCode(articleStoreMasterData.getSalesBlockingCode())
          .setStorePieceNetSellPrice(articleStoreMasterData.getStorePieceNetSellPrice())
          .setStoreBundleNetSellPrice(articleStoreMasterData.getStoreBundleNetSellPrice())
          .setStorePieceGrossSellPrice(articleStoreMasterData.getStorePieceGrossSellPrice())
          .setStoreBundleGrossSellPrice(articleStoreMasterData.getStoreBundleGrossSellPrice())
          .setStorePriceValidFrom(articleStoreMasterData.getStorePriceValidFrom())
          .setStorePriceValidTo(articleStoreMasterData.getStorePriceValidTo())
          .setStorePromotionPieceNetPrice(articleStoreMasterData.getStorePromotionPieceNetPrice())
          .setStorePromotionBundleNetPrice(articleStoreMasterData.getStorePromotionBundleNetPrice())
          .setStorePromotionPieceGrossPrice(
              articleStoreMasterData.getStorePromotionPieceGrossPrice())
          .setStorePromotionBundleGrossPrice(
              articleStoreMasterData.getStorePromotionBundleGrossPrice())
          .setStorePromotionValidFrom(articleStoreMasterData.getStorePromotionValidFrom())
          .setStorePromotionValidTo(articleStoreMasterData.getStorePromotionValidTo());
    }
    return rawArticleStoreMasterData;
  }
}
