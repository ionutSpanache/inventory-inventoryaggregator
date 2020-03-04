package inventory.aggregator.rawarticledata.repository;

import inventory.aggregator.articlestoremasterdata.model.ArticleStoreMasterData;
import inventory.aggregator.articlestoremasterdata.service.AclArticleStoreMasterDataService;
import inventory.aggregator.common.article_information.articlecache.client.articleCacheStoreExceptions.model.ArticleCacheStoreExceptionsDataGroup;
import inventory.aggregator.common.article_information.articlecache.client.articleCacheStoreExceptions.model.ArticleCacheStoreExceptionsIhdsTree;
import inventory.aggregator.common.article_information.articlecache.service.ArticleCacheService;
import inventory.aggregator.rawarticledata.model.ArticleId;
import inventory.aggregator.rawarticledata.model.DiscountArticleStoreMasterData;
import inventory.aggregator.rawarticledata.model.RawArticleStoreMasterData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Repository
public class RawArticleStoreMasterDataRepository {
    private final AclArticleStoreMasterDataService aclArticleStoreMasterDataService;
    private final ArticleCacheService articleCacheService;

    @Autowired
    public RawArticleStoreMasterDataRepository(AclArticleStoreMasterDataService aclArticleStoreMasterDataService, ArticleCacheService articleCacheService){
        this.aclArticleStoreMasterDataService = aclArticleStoreMasterDataService;
        this.articleCacheService = articleCacheService;
    }

    @Async
    public CompletableFuture<RawArticleStoreMasterData> getRawArticleStoreMasterData(ArticleId articleId, String countryCode, String salesLine, String languageCode, Integer storeNo) {
        List<ArticleStoreMasterData> articleStoreMasterDataList = aclArticleStoreMasterDataService.getArticleStoreMasterDataByArtVarBundle(
                countryCode, salesLine, storeNo, Math.toIntExact(articleId.getArticleNumber()),
                Math.toIntExact(articleId.getVariantNumber()),
                Math.toIntExact(articleId.getBundleNumber()));

        ArticleCacheStoreExceptionsIhdsTree articleCacheStoreExceptionsIhdsTree =
                articleCacheService.getArticleCacheStoreExceptions(articleId.getArticleNumber(), articleId.getVariantNumber(), articleId.getBundleNumber()
                        , countryCode, salesLine, languageCode, storeNo);
        ArticleCacheStoreExceptionsDataGroup articleCacheStoreExceptionsDataGroup100 = new ArticleCacheStoreExceptionsDataGroup();
        ArticleCacheStoreExceptionsDataGroup articleCacheStoreExceptionsDataGroup200 = new ArticleCacheStoreExceptionsDataGroup();
        ArticleCacheStoreExceptionsDataGroup articleCacheStoreExceptionsDataGroup300 = new ArticleCacheStoreExceptionsDataGroup();

        if (articleCacheStoreExceptionsIhdsTree != null) {
            articleCacheStoreExceptionsDataGroup100 = articleCacheStoreExceptionsIhdsTree.getTransaction().getArticleCacheIncident().getArticleCacheStoreExceptionsDataGroupById(100);
            articleCacheStoreExceptionsDataGroup200 = articleCacheStoreExceptionsIhdsTree.getTransaction().getArticleCacheIncident().getArticleCacheStoreExceptionsDataGroupById(200);
            articleCacheStoreExceptionsDataGroup300 = articleCacheStoreExceptionsIhdsTree.getTransaction().getArticleCacheIncident().getArticleCacheStoreExceptionsDataGroupById(300);
        }

        RawArticleStoreMasterData rawArticleStoreMasterData = RawArticleStoreMasterData.builder()
                .departmentNumber(Optional.ofNullable(articleStoreMasterDataList).map(value -> value.get(0).getDepartmentNumber()).orElse(null))
                .shelfEdgeTicketType(Optional.ofNullable(articleStoreMasterDataList).map(value -> value.get(0).getShelfEdgeTicketType()).orElse(null))
                .salesBlockingCode(articleCacheStoreExceptionsDataGroup300.getSalesBlockingCode())
                .storePieceNetSellPrice(articleCacheStoreExceptionsDataGroup100.getNetSalesPricePerPiece())
                .storeBundleNetSellPrice(articleCacheStoreExceptionsDataGroup100.getNetSalesPricePerBundle())
                .storePieceGrossSellPrice(articleCacheStoreExceptionsDataGroup100.getGrossSalesPricePerPiece())
                .storeBundleGrossSellPrice(articleCacheStoreExceptionsDataGroup100.getGrossSalesPricePerBundle())
                .storePromotionPieceNetPrice(articleCacheStoreExceptionsDataGroup200.getPromotionNetSalesPricePerPiece())
                .storePromotionBundleNetPrice(articleCacheStoreExceptionsDataGroup200.getPromotionNetSalesPricePerBundle())
                .storePromotionPieceGrossPrice(articleCacheStoreExceptionsDataGroup200.getPromotionGrossSalesPricePerPiece())
                .storePromotionBundleGrossPrice(articleCacheStoreExceptionsDataGroup200.getPromotionGrossSalesPricePerBundle())
                .leadTime(Optional.ofNullable(articleStoreMasterDataList).map(value -> value.get(0).getLeadTime()).orElse(null))
                .build();

        return CompletableFuture.completedFuture(rawArticleStoreMasterData);
    }

    @Async
    public CompletableFuture<DiscountArticleStoreMasterData> getDiscountArticleStoreMasterData(ArticleId articleId, String countryCode, String salesLine, String languageCode, Integer storeNo) {
        ArticleCacheStoreExceptionsIhdsTree articleCacheStoreExceptionsIhdsTree =
                articleCacheService.getArticleCacheStoreExceptions(articleId.getArticleNumber(), articleId.getVariantNumber(), articleId.getBundleNumber()
                        , countryCode, salesLine, languageCode, storeNo);
        ArticleCacheStoreExceptionsDataGroup articleCacheStoreExceptionsDataGroup100 = new ArticleCacheStoreExceptionsDataGroup();
        ArticleCacheStoreExceptionsDataGroup articleCacheStoreExceptionsDataGroup200 = new ArticleCacheStoreExceptionsDataGroup();
        ArticleCacheStoreExceptionsDataGroup articleCacheStoreExceptionsDataGroup300 = new ArticleCacheStoreExceptionsDataGroup();

        if (articleCacheStoreExceptionsIhdsTree != null) {
            articleCacheStoreExceptionsDataGroup100 = articleCacheStoreExceptionsIhdsTree.getTransaction().getArticleCacheIncident().getArticleCacheStoreExceptionsDataGroupById(100);
            articleCacheStoreExceptionsDataGroup200 = articleCacheStoreExceptionsIhdsTree.getTransaction().getArticleCacheIncident().getArticleCacheStoreExceptionsDataGroupById(200);
            articleCacheStoreExceptionsDataGroup300 = articleCacheStoreExceptionsIhdsTree.getTransaction().getArticleCacheIncident().getArticleCacheStoreExceptionsDataGroupById(300);
        }

        DiscountArticleStoreMasterData discountArticleStoreMasterData = DiscountArticleStoreMasterData.builder()
                .salesBlockingCode(articleCacheStoreExceptionsDataGroup300.getSalesBlockingCode())
                .storePieceNetSellPrice(articleCacheStoreExceptionsDataGroup100.getNetSalesPricePerPiece())
                .storeBundleNetSellPrice(articleCacheStoreExceptionsDataGroup100.getNetSalesPricePerBundle())
                .storePieceGrossSellPrice(articleCacheStoreExceptionsDataGroup100.getGrossSalesPricePerPiece())
                .storeBundleGrossSellPrice(articleCacheStoreExceptionsDataGroup100.getGrossSalesPricePerBundle())
                .storePromotionPieceNetPrice(articleCacheStoreExceptionsDataGroup200.getPromotionNetSalesPricePerPiece())
                .storePromotionBundleNetPrice(articleCacheStoreExceptionsDataGroup200.getPromotionNetSalesPricePerBundle())
                .storePromotionPieceGrossPrice(articleCacheStoreExceptionsDataGroup200.getPromotionGrossSalesPricePerPiece())
                .storePromotionBundleGrossPrice(articleCacheStoreExceptionsDataGroup200.getPromotionGrossSalesPricePerBundle())
                .build();

        return CompletableFuture.completedFuture(discountArticleStoreMasterData);
    }
}
