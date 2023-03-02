package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_model;

import java.math.BigDecimal;
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

  private Double weightArticles;

  public Double getWeightArticles() {
    return weightArticles;
  }

  @XmlElement(name = "WEIGHTARTICLE")
  public void setWeightArticles(Double weightArticles) {
    this.weightArticles = weightArticles;
  }

  private String foodNonfoodIndicator;

  public String getFoodNonfoodIndicator() {
    return foodNonfoodIndicator;
  }

  @XmlElement(name = "INDICATORFOODNONFOOD")
  public void setFoodNonfoodIndicator(String foodNonfoodIndicator) {
    this.foodNonfoodIndicator = foodNonfoodIndicator;
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

  private Long presentationSubMerchandiseGroup;

  public Long getPresentationSubMerchandiseGroup() {
    return presentationSubMerchandiseGroup;
  }

  @XmlElement(name = "PRESENTATIONMERCHANDISESUBGROUP")
  public void setPresentationSubMerchandiseGroup(Long presentationSubMerchandiseGroup) {
    this.presentationSubMerchandiseGroup = presentationSubMerchandiseGroup;
  }

  private Boolean hasMinimumRetailPrice;

  @XmlElement(name = "ISTRADEITEMWITHMINIMUMRETAILPRICEFORVATCALCULATION")
  public void setHasMinimumRetailPrice(Boolean hasMinimumRetailPrice) {
    this.hasMinimumRetailPrice = hasMinimumRetailPrice;
  }

  public Boolean getHasMinimumRetailPrice() {
    return  hasMinimumRetailPrice;
  }

  private String alcoholClass;

  public String getAlcoholClass() {
    return alcoholClass;
  }

  @XmlElement(name = "ALCOHOLCLASS")
  public void setAlcoholClass(String alcoholClass) {
    this.alcoholClass = alcoholClass;
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

  private Double netWeight;

  public Double getNetWeight() {
    return netWeight;
  }

  @XmlElement(name = "NETPIECEWEIGHT")
  public void setNetWeight(Double netWeight) {
    this.netWeight = netWeight;
  }

  private String generalSupplierName;

  public String getGeneralSupplierName() {
    return generalSupplierName;
  }

  @XmlElement(name = "GENERAL_SUPPLIER_NAME")
  public void setGeneralSupplierName(String generalSupplierName) {
    this.generalSupplierName = generalSupplierName;
  }

  private Byte salesBundleIndicator;

  public Byte getSalesBundleIndicator() {
    return salesBundleIndicator;
  }

  @XmlElement(name = "SALESBUNDLEINDICATOR")
  public void setSalesBundleIndicator(Byte salesBundleIndicator) {
    this.salesBundleIndicator = salesBundleIndicator;
  }

  private Integer expiryDays;

  public Integer getExpiryDays() {
    return expiryDays;
  }

  @XmlElement(name = "EXPIRYDAYS")
  public void setExpiryDays(Integer expiryDays) {
    this.expiryDays = expiryDays;
  }

  private String countryOfOrigin;

  public String getCountryOfOrigin() {
    return countryOfOrigin;
  }

  @XmlElement(name = "COUNTRYOFORIGIN")
  public void setCountryOfOrigin(String countryOfOrigin) {
    this.countryOfOrigin = countryOfOrigin;
  }

  private Integer packageFactor;

  public Integer getPackageFactor() {
    return packageFactor;
  }

  @XmlElement(name = "PACKAGEFACTOR")
  public void setPackageFactor(Integer packageFactor) {
    this.packageFactor = packageFactor;
  }

  /*Data Group 300*/
  private Long articleNoEmptiesSales;

  public Long getArticleNoEmptiesSales() {
    return articleNoEmptiesSales;
  }

  @XmlElement(name = "ARTICLENOEMPTIESSALES")
  public void setArticleNoEmptiesSales(Long articleNoEmptiesSales) {
    this.articleNoEmptiesSales = articleNoEmptiesSales;
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

  private Integer inOutIndicator;

  public Integer getInOutIndicator() {
    return inOutIndicator;
  }

  @XmlElement(name = "INOUTINDICATORMCCSALESLINE")
  public void setInOutIndicator(Integer inOutIndicator) {
    this.inOutIndicator = inOutIndicator;
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

  private String assortmentCode;

  public String getAssortmentCode() {
    return assortmentCode;
  }

  @XmlElement(name = "ASSORTMENTCD")
  public void setAssortmentCode(String assortmentCode) {
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

  private Double minimumRetailPriceForVatCalculation;

  public Double getMinimumRetailPriceForVatCalculation() {
    return minimumRetailPriceForVatCalculation;
  }

  @XmlElement(name = "MINIMUMRETAILPRICEFORVATCALCULATIONPERPIECE")
  public void setMinimumRetailPriceForVatCalculation(Double minimumRetailPriceForVatCalculation) {
    this.minimumRetailPriceForVatCalculation = minimumRetailPriceForVatCalculation;
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

  private String promotionEnd;

  public String getPromotionEnd() {
    return promotionEnd;
  }

  @XmlElement(name = "PROMOTIONEND")
  public void setPromotionEnd(String promotionEnd) {
    this.promotionEnd = promotionEnd;
  }

  private String articleClass;

  public String getArticleClass() {
    return articleClass;
  }

  @XmlElement(name = "ARTICLECLASSDISPO")
  public void setArticleClass(String articleClass) {
    this.articleClass = articleClass;
  }

  private double pieceNetBuyingPrice;

  public double getPieceNetBuyingPrice() {
    return pieceNetBuyingPrice;
  }

  @XmlElement(name = "NETBUYINGPRICEPERPIECE")
  public void setPieceNetBuyingPrice(double pieceNetBuyingPrice) {
    this.pieceNetBuyingPrice = pieceNetBuyingPrice;
  }

  private double bundleNetBuyingPrice;

  public double getBundleNetBuyingPrice() {
    return bundleNetBuyingPrice;
  }

  @XmlElement(name = "NETBUYINGPRICEPERSALESBUNDLE")
  public void setBundleNetBuyingPrice(double bundleNetBuyingPrice) {
    this.bundleNetBuyingPrice = bundleNetBuyingPrice;
  }

  private double pieceNetNetBuyingPrice;

  public double getPieceNetNetBuyingPrice() {
    return pieceNetNetBuyingPrice;
  }

  @XmlElement(name = "NETNETBUYINGPRICEPERPIECE")
  public void setPieceNetNetBuyingPrice(double pieceNetNetBuyingPrice) {
    this.pieceNetNetBuyingPrice = pieceNetNetBuyingPrice;
  }

  private double bundleNetNetBuyingPrice;

  public double getBundleNetNetBuyingPrice() {
    return bundleNetNetBuyingPrice;
  }

  @XmlElement(name = "NETNETBUYINGPRICEPERSALESBUNDLE")
  public void setBundleNetNetBuyingPrice(double bundleNetNetBuyingPrice) {
    this.bundleNetNetBuyingPrice = bundleNetNetBuyingPrice;
  }

  private Integer coreAssortmentIndicator;

  public Integer getCoreAssortmentIndicator() {
    return coreAssortmentIndicator;
  }

  @XmlElement(name = "COREASSORTMENTINDICATOR")
  public void setCoreAssortmentIndicator(Integer coreAssortmentIndicator) {
    this.coreAssortmentIndicator = coreAssortmentIndicator;
  }

  private BigDecimal minimumGrossSalesPricePerUnit;

  public BigDecimal getMinimumGrossSalesPricePerUnit() {
    return minimumGrossSalesPricePerUnit;
  }

  @XmlElement(name = "MINIMUMGROSSSALESPRICEPERUNIT")
  public void setMinimumGrossSalesPricePerUnit(BigDecimal minimumGrossSalesPricePerUnit) {
    this.minimumGrossSalesPricePerUnit = minimumGrossSalesPricePerUnit;
  }

  private Integer returnCode;

  public Integer getReturnCode() {
    return returnCode;
  }

  @XmlElement(name = "RETURN_CODE")
  public void setReturnCode(Integer returnCode) {
    this.returnCode = returnCode;
  }

  private Integer goodsEntryDetailCheck;

  public Integer getGoodsEntryDetailCheck() {
    return goodsEntryDetailCheck;
  }

  @XmlElement(name = "GOODS_ENTRY_DETAIL_CHECK")
  public void setGoodsEntryDetailCheck(Integer goodsEntryDetailCheck) {
    this.goodsEntryDetailCheck = goodsEntryDetailCheck;
  }

  private Integer store;

  public Integer getStore() {
    return store;
  }

  @XmlElement(name = "STORE")
  public void setStore(Integer store) {
    this.store = store;
  }

  @Override
  public String toString() {
    return "ArticleCacheDataGroup{" +
            "id=" + id +
            ", ownBrandId='" + ownBrandId + '\'' +
            ", vatPercent=" + vatPercent +
            ", weightArticles=" + weightArticles +
            ", foodNonfoodIndicator='" + foodNonfoodIndicator + '\'' +
            ", presentationMainMerchandiseGroup=" + presentationMainMerchandiseGroup +
            ", presentationMerchandiseGroup=" + presentationMerchandiseGroup +
            ", presentationSubMerchandiseGroup=" + presentationSubMerchandiseGroup +
            ", hasMinimumRetailPrice=" + hasMinimumRetailPrice +
            ", alcoholClass='" + alcoholClass + '\'' +
            ", singleItemSalesBarcode=" + singleItemSalesBarcode +
            ", generalSupplierName='" + generalSupplierName + '\'' +
            ", salesBundleIndicator=" + salesBundleIndicator +
            ", expiryDays=" + expiryDays +
            ", countryOfOrigin='" + countryOfOrigin + '\'' +
            ", packageFactor=" + packageFactor +
            ", articleNoEmptiesSales=" + articleNoEmptiesSales +
            ", sellingBundleContent=" + sellingBundleContent +
            ", priceCheckCodeMccSalesLine=" + priceCheckCodeMccSalesLine +
            ", assignedStoreSupplier=" + assignedStoreSupplier +
            ", assignedStoreSupplierType='" + assignedStoreSupplierType + '\'' +
            ", hierarchySequenceStore=" + hierarchySequenceStore +
            ", activationStatus=" + activationStatus +
            ", blockingCode=" + blockingCode +
            ", assortmentCode='" + assortmentCode + '\'' +
            ", isWeightArticle=" + isWeightArticle +
            ", storeNo=" + storeNo +
            ", pieceNetSellPrice=" + pieceNetSellPrice +
            ", bundleNetSellPrice=" + bundleNetSellPrice +
            ", promotionBundleNetPrice=" + promotionBundleNetPrice +
            ", minimumRetailPriceForVatCalculation=" + minimumRetailPriceForVatCalculation +
            ", hierarchySequenceStoreRef=" + hierarchySequenceStoreRef +
            ", procurementCd=" + procurementCd +
            ", promotionId='" + promotionId + '\'' +
            ", promotionEnd='" + promotionEnd + '\'' +
            ", articleClass='" + articleClass + '\'' +
            ", pieceNetBuyingPrice=" + pieceNetBuyingPrice +
            ", bundleNetBuyingPrice=" + bundleNetBuyingPrice +
            ", pieceNetNetBuyingPrice=" + pieceNetNetBuyingPrice +
            ", bundleNetNetBuyingPrice=" + bundleNetNetBuyingPrice +
            ", coreAssortmentIndicator=" + coreAssortmentIndicator +
            ", minimumGrossSalesPricePerUnit=" + minimumGrossSalesPricePerUnit +
            ", returnCode=" + returnCode +
            ", goodsEntryDetailCheck=" + goodsEntryDetailCheck +
            ", store=" + store +
            '}';
  }
}
