package inventory.aggregator.rawarticledata.repository;

import inventory.aggregator.common.article_information.articlecache.model.ArticleCacheFullDataForGapCheck;
import inventory.aggregator.common.article_information.articlecache.service.ArticleCacheService;
import inventory.aggregator.rawarticledata.model.ArticleId;
import inventory.aggregator.rawarticledata.model.ArticleMasterData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.concurrent.CompletableFuture;

@Repository
public class ArticleMasterDataRepository {
    private final ArticleCacheService articleCacheService;

    @Autowired
    public ArticleMasterDataRepository(ArticleCacheService articleCacheService){
        this.articleCacheService = articleCacheService;
    }

    @Async
    public CompletableFuture<ArticleMasterData> getArticleMasterData(ArticleId articleId, String countryCode, String salesLine, String languageCode, Integer storeNo) throws Exception {
        ArticleCacheFullDataForGapCheck articleCacheFullDataForGapCheck = getArticleCacheFullDataForGapCheckByArtVarBundle(
                articleId.getArticleNumber(),
                articleId.getVariantNumber(),
                articleId.getBundleNumber(),
                countryCode, salesLine, languageCode, storeNo);
        ArticleMasterData articleMasterData = transformArticleCacheFullDataForGapCheckToArticleMasterData(
                articleCacheFullDataForGapCheck);

        return CompletableFuture.completedFuture(articleMasterData);
    }

    private ArticleCacheFullDataForGapCheck getArticleCacheFullDataForGapCheckByArtVarBundle(
            Long articleNumber,
            Long variantNumber,
            Long bundleNumber,
            String countryCode,
            String salesLine,
            String languageCode,
            Integer storeNo) throws Exception {

        return articleCacheService.getArticleCacheFullDataForGapCheckByArtVarBundle(
                        articleNumber,
                        variantNumber,
                        bundleNumber,
                        countryCode,
                        salesLine,
                        languageCode,
                        storeNo
                );
    }

    private ArticleMasterData transformArticleCacheFullDataForGapCheckToArticleMasterData(
            ArticleCacheFullDataForGapCheck articleCacheFullDataForGapCheck) {
        if (articleCacheFullDataForGapCheck != null) {
            return new ArticleMasterData(
                    articleCacheFullDataForGapCheck.getArticleDescription(),
                    articleCacheFullDataForGapCheck.getBarcode(),
                    articleCacheFullDataForGapCheck.getCommercialSupplier(),
                    articleCacheFullDataForGapCheck.getPresentationMerchandiseGroup(),
                    articleCacheFullDataForGapCheck.getPromotionId(),
                    articleCacheFullDataForGapCheck.getSellingBundleContent(),
                    articleCacheFullDataForGapCheck.getSubsysArticleNumber(),
                    articleCacheFullDataForGapCheck.getVatPercent(),
                    articleCacheFullDataForGapCheck.getPieceNetSellPrice(),
                    articleCacheFullDataForGapCheck.getBundleNetSellPrice(),
                    articleCacheFullDataForGapCheck.getPromotionBundleNetPrice(),
                    articleCacheFullDataForGapCheck.getVariantDescription(),
                    articleCacheFullDataForGapCheck.getArticleClass(),
                    articleCacheFullDataForGapCheck.getPresentationMainMerchandiseGroup(),
                    articleCacheFullDataForGapCheck.getPresentationSubMerchandiseGroup(),
                    articleCacheFullDataForGapCheck.getSupplierNumber(),
                    articleCacheFullDataForGapCheck.getProcurementCode()
            );
        }
        return null;
    }
}
