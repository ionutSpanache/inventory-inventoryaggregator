package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard;

import com.sun.xml.txw2.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

@XmlRootElement(name = "content")
public class ContentIduItemDerivedStandard implements Serializable {


    private List<MerchandiseArticleDescription> merchandiseArticleDescription;

    private List<VariantSortDescriptionV2> variantSortDescriptionV2;

    private IsTradeItemExcludedFromDiscounts isTradeItemExcludedFromDiscounts;

    private GradeCodeReferenceFruitsVegetables gradeCodeReferenceFruitsVegetables;

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

    @XmlElement("gradeCodeReferenceFruitsVegetables")
    public void setGradeCodeReferenceFruitsVegetables(
        GradeCodeReferenceFruitsVegetables gradeCodeReferenceFruitsVegetables) {
        this.gradeCodeReferenceFruitsVegetables = gradeCodeReferenceFruitsVegetables;
    }

    @XmlElement("isTradeItemExcludedFromDiscounts")
    public void setIsTradeItemExcludedFromDiscounts(IsTradeItemExcludedFromDiscounts isTradeItemExcludedFromDiscounts) {
        this.isTradeItemExcludedFromDiscounts = isTradeItemExcludedFromDiscounts;
    }
}
