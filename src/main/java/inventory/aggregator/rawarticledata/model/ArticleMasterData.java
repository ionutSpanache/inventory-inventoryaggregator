package inventory.aggregator.rawarticledata.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleMasterData implements Serializable {
	private static final long serialVersionUID = -2971487066548844011L;
	private String  articleDescription;
	private String  barcode;
	private Integer commercialSupplier;
	private Integer presentationMerchandiseGroup;
	private String promotionId;
	private Integer sellingBundleContent;
	private Long subsystemArticleNumber;
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
    

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "ArticleMasterData [articleDescription=" + articleDescription
                + ", barcode=" + barcode
				+ ", commercialSupplier=" + commercialSupplier
                + ", presentationMainMerchandiseGroup=" + presentationMainMerchandiseGroup
                + ", presentationMerchandiseGroup=" + presentationMerchandiseGroup
                + ", presentationSubMerchandiseGroup=" + presentationSubMerchandiseGroup
				+ ", promotionId=" + promotionId
                + ", sellingBundleContent=" + sellingBundleContent
				+ ", subsystemArticleNumber=" + subsystemArticleNumber
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
