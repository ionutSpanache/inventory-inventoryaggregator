package inventory.aggregator.rawarticledata.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Double storePromotionPieceNetPrice;
    private Double storePromotionBundleNetPrice;
    private Double storePromotionPieceGrossPrice;
    private Double storePromotionBundleGrossPrice;
    private Integer leadTime;
}
