package inventory.aggregator.common.article_information.articlecache.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleStoreExceptions {
    private Integer salesBlockingCode;
    private Double storePieceNetSellPrice;
    private Double storeBundleNetSellPrice;
    private Double storePieceGrossSellPrice;
    private Double storeBundleGrossSellPrice;
    private Double storePromotionPieceNetPrice;
    private Double storePromotionBundleNetPrice;
    private Double storePromotionPieceGrossPrice;
    private Double storePromotionBundleGrossPrice;
}
