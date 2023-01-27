package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard;

import com.sun.xml.txw2.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_isu_item_master.AssortmentClusterSpecificationV2;

@XmlRootElement(name = "content")
public class ContentIduItemDerivedStandard implements Serializable {


    private List<MerchandiseArticleDescription> merchandiseArticleDescription;

    private List<VariantSortDescriptionV2> variantSortDescriptionV2;

    private IsTradeItemExcludedFromDiscounts isTradeItemExcludedFromDiscounts;

    private GradeCodeReferenceFruitsVegetables gradeCodeReferenceFruitsVegetables;

    private IsTradeItemInSellOut isTradeItemInSellOut;

    private IsTradeItemSellableInPFSShop isTradeItemSellableInPFSShop;

    private IsTradeItemDiscountMachine isTradeItemDiscountMachine;

    private MerchandiseBrand merchandiseBrand;

    private AssortmentClusterSpecificationV2 assortmentClusterSpecificationV2;

    private Boolean isTradeItemAVariableUnit;

    public List<MerchandiseArticleDescription> getMerchandiseArticleDescription() {
        return merchandiseArticleDescription;
    }

    @XmlElement("merchandiseArticleDescription")
    public void setMerchandiseArticleDescription(List<MerchandiseArticleDescription> merchandiseArticleDescription) {
        this.merchandiseArticleDescription = merchandiseArticleDescription;
    }

    public List<VariantSortDescriptionV2> getVariantSortDescriptionV2() {
        return variantSortDescriptionV2;
    }

    @XmlElement("variantSortDescriptionV2")
    public void setVariantSortDescriptionV2(List<VariantSortDescriptionV2> variantSortDescriptionV2) {
        this.variantSortDescriptionV2 = variantSortDescriptionV2;
    }

    public IsTradeItemExcludedFromDiscounts getIsTradeItemExcludedFromDiscounts() {
        return isTradeItemExcludedFromDiscounts;
    }

    public GradeCodeReferenceFruitsVegetables getGradeCodeReferenceFruitsVegetables() {
        return gradeCodeReferenceFruitsVegetables;
    }

    public IsTradeItemInSellOut getIsTradeItemInSellOut() {
        return isTradeItemInSellOut;
    }

    public IsTradeItemSellableInPFSShop getIsTradeItemSellableInPFSShop() {
        return isTradeItemSellableInPFSShop;
    }

    public IsTradeItemDiscountMachine getIsTradeItemDiscountMachine() {
        return isTradeItemDiscountMachine;
    }

    public MerchandiseBrand getMerchandiseBrand() {
        return merchandiseBrand;
    }

    @XmlElement("gradeCodeReferenceFruitsVegetables")
    public void setGradeCodeReferenceFruitsVegetables(
        GradeCodeReferenceFruitsVegetables gradeCodeReferenceFruitsVegetables) {
        this.gradeCodeReferenceFruitsVegetables = gradeCodeReferenceFruitsVegetables;
    }

    @XmlElement("isTradeItemInSellOut")
    public void setIsTradeItemInSellOut(
            IsTradeItemInSellOut isTradeItemInSellOut) {
        this.isTradeItemInSellOut = isTradeItemInSellOut;
    }

    @XmlElement("isTradeItemSellableInPFSShop")
    public void setIsTradeItemSellableInPFSShop(
            IsTradeItemSellableInPFSShop isTradeItemSellableInPFSShop) {
        this.isTradeItemSellableInPFSShop = isTradeItemSellableInPFSShop;
    }

    @XmlElement("isTradeItemDiscountMachine")
    public void setIsTradeItemDiscountMachine(
            IsTradeItemDiscountMachine isTradeItemDiscountMachine) {
        this.isTradeItemDiscountMachine = isTradeItemDiscountMachine;
    }

    @XmlElement("isTradeItemExcludedFromDiscounts")
    public void setIsTradeItemExcludedFromDiscounts(IsTradeItemExcludedFromDiscounts isTradeItemExcludedFromDiscounts) {
        this.isTradeItemExcludedFromDiscounts = isTradeItemExcludedFromDiscounts;
    }

    @XmlElement("merchandiseBrand")
    public void setMerchandiseBrand(MerchandiseBrand merchandiseBrand) {
        this.merchandiseBrand = merchandiseBrand;
    }

    public AssortmentClusterSpecificationV2 getAssortmentClusterSpecificationV2() {
        return assortmentClusterSpecificationV2;
    }

    @javax.xml.bind.annotation.XmlElement(name = "assortmentClusterSpecificationV2")
    public void setAssortmentClusterSpecificationV2(AssortmentClusterSpecificationV2 assortmentClusterSpecificationV2) {
        this.assortmentClusterSpecificationV2 = assortmentClusterSpecificationV2;
    }

    @XmlElement("isTradeItemAVariableUnit")
    public void setIsTradeItemAVariableUnit(Boolean isTradeItemAVariableUnit){
        this.isTradeItemAVariableUnit = isTradeItemAVariableUnit;
    }

    public double getWeightArticle(){
        if(Boolean.TRUE.equals(isTradeItemAVariableUnit)){
            return 1;
        }
        return 0;
    }

}
