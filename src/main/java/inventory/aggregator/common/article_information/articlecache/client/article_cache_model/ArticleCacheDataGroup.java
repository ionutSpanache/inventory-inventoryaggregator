package inventory.aggregator.common.article_information.articlecache.client.article_cache_model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "DataGroup")
@AllArgsConstructor
@NoArgsConstructor
public class ArticleCacheDataGroup {

    /*Data Group 10*/
    private int id;
    public int getId() {
        return id;
    }
    @XmlAttribute(name = "id")
    public void setId(int id) {
        this.id = id;
    }

    private String ownBrandId;
    public String getOwnBrandId() {
        return ownBrandId;
    }
    @XmlElement(name = "OWNBRANDID")
    public void setOwnBrandId(String ownBrandId) {
        this.ownBrandId = ownBrandId;
    }

    /*Data Group 100*/
    private Double vatPercent;
    public Double getVatPercent() {
        return vatPercent;
    }
    @XmlElement(name = "VATPERCENT")
    public void setVatPercent(Double vatPercent) {
        this.vatPercent = vatPercent;
    }

    private Long presentationMainMerchandiseGroup;
    public Long getPresentationMainMerchandiseGroup() {
        return presentationMainMerchandiseGroup;
    }
    @XmlElement(name = "PRESENTATIONMERCHANDISEMAINGROUP")
    public void setPresentationMainMerchandiseGroup(Long presentationMainMerchandiseGroup) {
        this.presentationMainMerchandiseGroup = presentationMainMerchandiseGroup;
    }

    private Integer presentationMerchandiseGroup;
    public Integer getPresentationMerchandiseGroup() {
        return presentationMerchandiseGroup;
    }
    @XmlElement(name = "PRESENTATIONMERCHANDISEGROUP")
    public void setPresentationMerchandiseGroup(Integer presentationMerchandiseGroup) {
        this.presentationMerchandiseGroup = presentationMerchandiseGroup;
    }

    private Long presentationSubMerchandiseGroup ;
    public Long getPresentationSubMerchandiseGroup () {
        return presentationSubMerchandiseGroup ;
    }
    @XmlElement(name = "PRESENTATIONMERCHANDISESUBGROUP")
    public void setPresentationSubMerchandiseGroup (Long presentationSubMerchandiseGroup ) {
        this.presentationSubMerchandiseGroup  = presentationSubMerchandiseGroup ;
    }

    /*Data Group 200*/
    private List<String> singleItemSalesBarcode;
    public List<String> getSingleItemSalesBarcode() {
        return singleItemSalesBarcode;
    }
    @XmlElement(name = "SINGLEITEMSALESBARCODE")
    public void setSingleItemSalesBarcode(List<String> singleItemSalesBarcode) {
        this.singleItemSalesBarcode = singleItemSalesBarcode;
    }

    private Byte salesBundleIndicator;
    public Byte getSalesBundleIndicator() {
        return salesBundleIndicator;
    }
    @XmlElement(name = "SALESBUNDLEINDICATOR")
    public void setSalesBundleIndicator(Byte salesBundleIndicator) {
        this.salesBundleIndicator = salesBundleIndicator;
    }

    /*Data Group 310*/
    private Integer sellingBundleContent;
    public Integer getSellingBundleContent() {
        return sellingBundleContent;
    }
    @XmlElement(name = "SELLINGBUNDLECONTENT")
    public void setSellingBundleContent(Integer sellingBundleContent) {
        this.sellingBundleContent = sellingBundleContent;
    }

    private Byte priceCheckCodeMccSalesLine;
    public Byte getPriceCheckCodeMccSalesLine() {
        return priceCheckCodeMccSalesLine;
    }
    @XmlElement(name = "PRICECHECKCODEMCCSALESLINE")
    public void setPriceCheckCodeMccSalesLine(Byte priceCheckCodeMccSalesLine) {
        this.priceCheckCodeMccSalesLine = priceCheckCodeMccSalesLine;
    }

    /*Data Group 320*/
    private Integer assignedStoreSupplier;
    public Integer getAssignedStoreSupplier() {
        return assignedStoreSupplier;
    }
    @XmlElement(name = "ASSIGNEDSTORESUPPLIER")
    public void setAssignedStoreSupplier(Integer assignedStoreSupplier) {
        this.assignedStoreSupplier = assignedStoreSupplier;
    }

    private String assignedStoreSupplierType;
    public String getAssignedStoreSupplierType() {
        return assignedStoreSupplierType;
    }
    @XmlElement(name = "ASSIGNEDSTORESUPPLIERTYPE")
    public void setAssignedStoreSupplierType(String assignedStoreSupplierType) {
        this.assignedStoreSupplierType = assignedStoreSupplierType;
    }

    private int hierarchySequenceStore;
    public int getHierarchySequenceStore() {
        return hierarchySequenceStore;
    }
    @XmlElement(name = "HIERARCHYSEQUENCESTORE")
    public void setHierarchySequenceStore(int hierarchySequenceStore) {
        this.hierarchySequenceStore = hierarchySequenceStore;
    }

    private Byte activationStatus;
    public Byte getActivationStatus() {
        return activationStatus;
    }
    @XmlElement(name = "ACTIVATIONSTATUS")
    public void setActivationStatus(Byte activationStatus) {
        this.activationStatus = activationStatus;
    }

    private Byte blockingCode;
    public Byte getBlockingCode() {
        if (this.blockingCode != null) {
            return blockingCode;
        }
        return null;
    }
    @XmlElement(name = "BLOCKINGCODE")
    public void setBlockingCode(Byte blockingCode) {
        this.blockingCode = blockingCode;
    }

    private Byte assortmentCode;
    public Byte getAssortmentCode() {
        return assortmentCode;
    }
    @XmlElement(name = "ASSORTMENTCD")
    public void setAssortmentCode(Byte assortmentCode) {
        this.assortmentCode = assortmentCode;
    }

    private Boolean isWeightArticle;
    public Boolean getWeightArticle() {
        return isWeightArticle;
    }
    @XmlElement(name = "WEIGHINGTYPE")
    public void setWeightArticle(Boolean weightArticle) {
        isWeightArticle = weightArticle;
    }

    private int storeNo;
    public int getStoreNo() {
        return storeNo;
    }
    @XmlElement(name = "STORENO")
    public void setStoreNo(int storeNo) {
        this.storeNo = storeNo;
    }

    private double pieceNetSellPrice;
    private double bundleNetSellPrice;
    private double promotionBundleNetPrice;

    public double getPieceNetSellPrice() {
        return pieceNetSellPrice;
    }
    @XmlElement(name = "NETSALESPRICEPERPIECE")
    public void setPieceNetSellPrice(double pieceNetSellPrice) {
        this.pieceNetSellPrice = pieceNetSellPrice;
    }

    public double getBundleNetSellPrice() {
        return bundleNetSellPrice;
    }
    @XmlElement(name = "NETSALESPRICEPERBUNDLE")
    public void setBundleNetSellPrice(double bundleNetSellPrice) {
        this.bundleNetSellPrice = bundleNetSellPrice;
    }

    public double getPromotionBundleNetPrice() {
        return promotionBundleNetPrice;
    }
    @XmlElement(name = "PROMOTIONNETSALESPRICEPERBUNDLE")
    public void setPromotionBundleNetPrice(double promotionBundleNetPrice) {
        this.promotionBundleNetPrice = promotionBundleNetPrice;
    }

    /*Data Group 325*/
    private int hierarchySequenceStoreRef;
    public int getHierarchySequenceStoreRef() {
        return hierarchySequenceStoreRef;
    }
    @XmlElement(name = "HIERARCHYSEQUENCESTORE_REF")
    public void setHierarchySequenceStoreRef(int hierarchySequenceStoreRef) {
        this.hierarchySequenceStoreRef = hierarchySequenceStoreRef;
    }

    private Byte procurementCd;
    public Byte getProcurementCd() {
        return procurementCd;
    }
    @XmlElement(name = "PROCUREMENTCD")
    public void setProcurementCd(Byte procurementCd) {
        this.procurementCd = procurementCd;
    }

    private String promotionId;
    public String getPromotionId() {
        return promotionId;
    }
    @XmlElement(name = "PROMOTIONID")
    public void setPromotionId(String promotionId) {
        this.promotionId = promotionId;
    }

	private String articleClass;
	public String getArticleClass() {
		return articleClass;
	}
	@XmlElement(name = "ARTICLECLASSDISPO")
	public void setArticleClass(String articleClass) {
		this.articleClass = articleClass;
	}

    @Override
    public String toString() {
        return "ArticleCacheDataGroup [id=" + id + ", presentationMerchandiseGroup=" + presentationMerchandiseGroup
                + ", ownBrandId=" + ownBrandId + ", vatPercent=" + vatPercent
                + ", singleItemSalesBarcode=" + singleItemSalesBarcode + ", salesBundleIndicator=" + salesBundleIndicator
                + ", sellingBundleContent=" + sellingBundleContent + ", priceCheckCodeMccSalesLine=" + priceCheckCodeMccSalesLine
                + ", assignedStoreSupplier=" + assignedStoreSupplier + ", assignedStoreSupplierType=" + assignedStoreSupplierType
                + ", hierarchySequenceStore=" + hierarchySequenceStore
                + ", activationStatus=" + activationStatus + ", blockingCode=" + blockingCode
                + ", isWeightArticle=" + isWeightArticle + ", storeNo=" + storeNo
                + ", pieceNetSellPrice=" + pieceNetSellPrice + ", bundleNetSellPrice=" + bundleNetSellPrice
                + ", promotionBundleNetPrice=" + promotionBundleNetPrice + ", hierarchySequenceStoreRef=" + hierarchySequenceStoreRef
                + ", procurementCd=" + procurementCd + ", promotionId=" + promotionId
				+ ", articleClass=" + articleClass
                + "]";
    }

}
