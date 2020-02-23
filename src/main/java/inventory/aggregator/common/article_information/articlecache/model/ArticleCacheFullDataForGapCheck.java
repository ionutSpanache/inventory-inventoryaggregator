package inventory.aggregator.common.article_information.articlecache.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleCacheFullDataForGapCheck {

    private Long articleNumber;
    private Long variantNumber;
    private Long bundleNumber;
    private String articleDescription;
    private String barcode;
    private Integer commercialSupplier;
    private Integer presentationMerchandiseGroup;
    private String promotionId;
    private Integer sellingBundleContent;
    private Boolean isWeightArticle;
    private Long subsysArticleNumber;
    private Double vatPercent;
    private double pieceNetSellPrice;
    private double bundleNetSellPrice;
    private double promotionBundleNetPrice;
    private String variantDescription;
    private String articleClass;
    private Long presentationMainMerchandiseGroup;
    private Long presentationSubMerchandiseGroup;
    private Integer supplierNumber;
    private Byte procurementCode;

    @Override
    public String toString() {
        return "ArticleCacheFullDataForGapCheck [articleNumber=" + articleNumber
				+ ", variantNumber=" + variantNumber
                + ", bundleNumber=" + bundleNumber
				+ ", articleDescription=" + articleDescription
				+ ", barcode=" + barcode
				+ ", commercialSupplier=" + commercialSupplier
				+ ", presentationMainMerchandiseGroup=" + presentationMainMerchandiseGroup
				+ ", presentationMerchandiseGroup=" + presentationMerchandiseGroup
				+ ", presentationSubMerchandiseGroup=" + presentationSubMerchandiseGroup
                + ", promotionId=" + promotionId
				+ ", sellingBundleContent=" + sellingBundleContent
                + ", isWeightArticle=" + isWeightArticle
				+ ", subsysArticleNumber=" + subsysArticleNumber
                + ", vatPercent=" + vatPercent
				+ ", pieceNetSellPrice=" + pieceNetSellPrice
				+ ", bundleNetSellPrice=" + bundleNetSellPrice
				+ ", promotionBundleNetPrice=" + promotionBundleNetPrice
				+ ", variantDescription=" + variantDescription
				+ ", articleClass=" + articleClass
				+ ", supplierNumber=" + supplierNumber
				+ ", procurementCode=" + procurementCode
				+ "]";
    }
}
