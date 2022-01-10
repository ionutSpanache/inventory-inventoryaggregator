package net.metrosystems.inventoryaggregator.articleattributes.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawArticleStoreMasterData {
    private Integer departmentNumber;
    private String shelfEdgeTicketType;
    private Integer salesBlockingCode;
    private Double storePieceNetSellPrice;
    private Double storeBundleNetSellPrice;
    private Double storePieceGrossSellPrice;
    private Double storeBundleGrossSellPrice;
    private Date storePriceValidFrom;
    private Date storePriceValidTo;
    private Double storePromotionPieceNetPrice;
    private Double storePromotionBundleNetPrice;
    private Double storePromotionPieceGrossPrice;
    private Double storePromotionBundleGrossPrice;
    private Date storePromotionValidFrom;
    private Date storePromotionValidTo;
    private Integer leadTime;
    private Integer orderProposalBlockingCode;
}
