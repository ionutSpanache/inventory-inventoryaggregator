package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_isu_item_master;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

@XmlRootElement(name = "content")
public class Content implements Serializable {

    private Double grossWeight;
    private TradeItemMeasurement tradeItemMeasurement;
    private Long merchandisePalletLoadingHeight;
    private String referenceType;
    private Boolean isTradeItemAnOrderableUnitMerchandise;
    private Boolean isTradeItemASellableUnitMerchandise;
    private Double volume;
    private MerchandiseTradeItemPalletModule merchandiseTradeItemPalletModule;
    private List<String> variantSortDescription;
    private Double netWeight;
    private String countryOfOrigin;
    private List<String> variantSortDescriptionV2;
    private OrderQuantityForOnlineOrdering orderQuantityForOnlineOrdering;
    private VatCodeInformation vatCodeInformation;
    private MerchandiseGroupInformation merchandiseGroupInformation;
    private AssortmentClusterSpecification assortmentClusterSpecification;
    private MerchandiseBrand merchandiseBrand;
    private Boolean isReturnableAssetEmpty;
    private Boolean isTradeItemAVariableUnit;
    private Boolean isTradeItemTraceable;
    private Long reportGroupingSupplierNumber;
    private String articleType;
    private PurchaseAreaInformation purchaseAreaInformation;
    private List<String> compactArticleDescriptions;
    private PresentationMerchandiseGroupInformation presentationMerchandiseGroupInformation;
    private List<String> merchandiseArticleDescription;
    private Boolean isBaseAssortment;
    private CategoryManagementArea categoryManagementArea;
    private ImportClassification importClassification;
    private Boolean ifStackable;
    private Boolean isFoodTradeItem;
    private int stackingFactor;
    private int minimumTradeItemLifespanFromTimeOfArrival;

    public ImportClassification getImportClassification() {
        return importClassification;
    }

    @XmlElement(name = "importClassification")
    public void setImportClassification(ImportClassification importClassification) {
        this.importClassification = importClassification;
    }

    public int getMinimumTradeItemLifespanFromTimeOfArrival() {
        return minimumTradeItemLifespanFromTimeOfArrival;
    }

    @XmlElement(name = "minimumTradeItemLifespanFromTimeOfArrival")
    public void setMinimumTradeItemLifespanFromTimeOfArrival(int minimumTradeItemLifespanFromTimeOfArrival) {
        this.minimumTradeItemLifespanFromTimeOfArrival = minimumTradeItemLifespanFromTimeOfArrival;
    }

    public int getStackingFactor() {
        return stackingFactor;
    }

    @XmlElement(name = "stackingFactor")
    public void setStackingFactor(int stackingFactor) {
        this.stackingFactor = stackingFactor;
    }

    public Boolean getIsFoodTradeItem() {
        return isFoodTradeItem;
    }

    @XmlElement(name = "isFoodTradeItem")
    public void setIsFoodTradeItem(Boolean isFoodTradeItem) {
        this.isFoodTradeItem = isFoodTradeItem;
    }

    public CategoryManagementArea getCategoryManagementArea() {
        return categoryManagementArea;
    }

    @XmlElement(name = "categoryManagementArea")
    public void setCategoryManagementArea(CategoryManagementArea categoryManagementArea) {
        this.categoryManagementArea = categoryManagementArea;
    }

    public Boolean getIfStackable() {
        return ifStackable;
    }

    @XmlElement(name = "ifStackable")
    public void setIfStackable(Boolean ifStackable) {
        this.ifStackable = ifStackable;
    }

    public Boolean getBaseAssortment() {
        return isBaseAssortment;
    }

    @XmlElement(name = "isBaseAssortment")
    public void setBaseAssortment(Boolean baseAssortment) {
        isBaseAssortment = baseAssortment;
    }

    public PresentationMerchandiseGroupInformation getPresentationMerchandiseGroupInformation() {
        return presentationMerchandiseGroupInformation;
    }

    @XmlElement(name = "presentationMerchandiseGroupInformation")
    public void setPresentationMerchandiseGroupInformation(PresentationMerchandiseGroupInformation presentationMerchandiseGroupInformation) {
        this.presentationMerchandiseGroupInformation = presentationMerchandiseGroupInformation;
    }

    public List<String> getCompactArticleDescriptions() {
        return compactArticleDescriptions;
    }

    @XmlElement(name = "compactArticleDescription")
    public void setCompactArticleDescriptions(List<String> compactArticleDescriptions) {
        this.compactArticleDescriptions = compactArticleDescriptions;
    }

    public List<String> getMerchandiseArticleDescription() {
        return merchandiseArticleDescription;
    }

    @XmlElement(name = "merchandiseArticleDescription")
    public void setMerchandiseArticleDescription(List<String> merchandiseArticleDescription) {
        this.merchandiseArticleDescription = merchandiseArticleDescription;
    }

    public String getArticleType() {
        return articleType;
    }

    @XmlElement(name = "articleType")
    public void setArticleType(String articleType) {
        this.articleType = articleType;
    }

    public PurchaseAreaInformation getPurchaseAreaInformation() {
        return purchaseAreaInformation;
    }

    @XmlElement(name = "purchaseAreaInformation")
    public void setPurchaseAreaInformation(PurchaseAreaInformation purchaseAreaInformation) {
        this.purchaseAreaInformation = purchaseAreaInformation;
    }

    public Long getReportGroupingSupplierNumber() {
        return reportGroupingSupplierNumber;
    }

    @XmlElement(name = "reportGroupingSupplierNumber")
    public void setReportGroupingSupplierNumber(Long reportGroupingSupplierNumber) {
        this.reportGroupingSupplierNumber = reportGroupingSupplierNumber;
    }

    public Boolean getTradeItemTraceable() {
        return isTradeItemTraceable;
    }

    @XmlElement(name = "isTradeItemTraceable")
    public void setTradeItemTraceable(Boolean tradeItemTraceable) {
        isTradeItemTraceable = tradeItemTraceable;
    }

    public Boolean getReturnableAssetEmpty() {
        return isReturnableAssetEmpty;
    }

    @XmlElement(name = "isReturnableAssetEmpty")
    public void setReturnableAssetEmpty(Boolean returnableAssetEmpty) {
        isReturnableAssetEmpty = returnableAssetEmpty;
    }

    public Boolean getTradeItemAVariableUnit() {
        return isTradeItemAVariableUnit;
    }

    @XmlElement(name = "isTradeItemAVariableUnit")
    public void setTradeItemAVariableUnit(Boolean tradeItemAVariableUnit) {
        isTradeItemAVariableUnit = tradeItemAVariableUnit;
    }

    public VatCodeInformation getVatCodeInformation() {
        return vatCodeInformation;
    }

    @XmlElement(name = "vatCodeInformation")
    public void setVatCodeInformation(VatCodeInformation vatCodeInformation) {
        this.vatCodeInformation = vatCodeInformation;
    }

    public MerchandiseGroupInformation getMerchandiseGroupInformation() {
        return merchandiseGroupInformation;
    }

    @XmlElement(name = "merchandiseGroupInformation")
    public void setMerchandiseGroupInformation(MerchandiseGroupInformation merchandiseGroupInformation) {
        this.merchandiseGroupInformation = merchandiseGroupInformation;
    }

    public MerchandiseBrand getMerchandiseBrand() {
        return merchandiseBrand;
    }

    @XmlElement(name = "merchandiseBrand")
    public void setMerchandiseBrand(MerchandiseBrand merchandiseBrand) {
        this.merchandiseBrand = merchandiseBrand;
    }

    public AssortmentClusterSpecification getAssortmentClusterSpecification() {
        return assortmentClusterSpecification;
    }

    @XmlElement(name = "assortmentClusterSpecification")
    public void setAssortmentClusterSpecification(AssortmentClusterSpecification assortmentClusterSpecification) {
        this.assortmentClusterSpecification = assortmentClusterSpecification;
    }

    public OrderQuantityForOnlineOrdering getOrderQuantityForOnlineOrdering() {
        return orderQuantityForOnlineOrdering;
    }

    @XmlElement(name = "orderQuantityForOnlineOrdering")
    public void setOrderQuantityForOnlineOrdering(OrderQuantityForOnlineOrdering orderQuantityForOnlineOrdering) {
        this.orderQuantityForOnlineOrdering = orderQuantityForOnlineOrdering;
    }

    public Double getNetWeight() {
        return netWeight;
    }

    @XmlElement(name = "netWeight")
    public void setNetWeight(Double netWeight) {
        this.netWeight = netWeight;
    }

    public List<String> getVariantSortDescriptionV2() {
        return variantSortDescriptionV2;
    }

    @XmlElement(name = "variantSortDescriptionV2")
    public void setVariantSortDescriptionV2(List<String> variantSortDescriptionV2) {
        this.variantSortDescriptionV2 = variantSortDescriptionV2;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    @XmlElement(name = "countryOfOrigin")
    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    public List<String> getVariantSortDescription() {
        return variantSortDescription;
    }

    @XmlElement(name = "variantSortDescription")
    public void setVariantSortDescription(List<String> variantSortDescription) {
        this.variantSortDescription = variantSortDescription;
    }

    public MerchandiseTradeItemPalletModule getMerchandiseTradeItemPalletModule() {
        return merchandiseTradeItemPalletModule;
    }

    @XmlElement(name = "merchandiseTradeitemPalletDataModule")
    public void setMerchandiseTradeItemPalletModule(MerchandiseTradeItemPalletModule merchandiseTradeItemPalletModule) {
        this.merchandiseTradeItemPalletModule = merchandiseTradeItemPalletModule;
    }

    public Double getVolume() {
        return volume;
    }

    @XmlElement(name = "volume")
    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Boolean getTradeItemAnOrderableUnitMerchandise() {
        return isTradeItemAnOrderableUnitMerchandise;
    }

    @XmlElement(name = "isTradeItemAnOrderableUnitMerchandise")
    public void setTradeItemAnOrderableUnitMerchandise(Boolean tradeItemAnOrderableUnitMerchandise) {
        isTradeItemAnOrderableUnitMerchandise = tradeItemAnOrderableUnitMerchandise;
    }

    public Boolean getTradeItemASellableUnitMerchandise() {
        return isTradeItemASellableUnitMerchandise;
    }

    @XmlElement(name = "isTradeItemASellableUnitMerchandise")
    public void setTradeItemASellableUnitMerchandise(Boolean tradeItemASellableUnitMerchandise) {
        isTradeItemASellableUnitMerchandise = tradeItemASellableUnitMerchandise;
    }

    public String getReferenceType() {
        return referenceType;
    }

    @XmlElement(name = "referenceType")
    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public Long getMerchandisePalletLoadingHeight() {
        return merchandisePalletLoadingHeight;
    }

    @XmlElement(name = "merchandisePalletLoadingHeight")
    public void setMerchandisePalletLoadingHeight(Long merchandisePalletLoadingHeight) {
        this.merchandisePalletLoadingHeight = merchandisePalletLoadingHeight;
    }

    public TradeItemMeasurement getTradeItemMeasurement() {
        return tradeItemMeasurement;
    }

    @XmlElement(name = "tradeItemMeasurements")
    public void setTradeItemMeasurement(TradeItemMeasurement tradeItemMeasurement) {
        this.tradeItemMeasurement = tradeItemMeasurement;
    }

    public Double getGrossWeight() {
        return grossWeight;
    }

    @XmlElement(name = "grossWeight")
    public void setGrossWeight(Double grossWeight) {
        this.grossWeight = grossWeight;
    }


}
