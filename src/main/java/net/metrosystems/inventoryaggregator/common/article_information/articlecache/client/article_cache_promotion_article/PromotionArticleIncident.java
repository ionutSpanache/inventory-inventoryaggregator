package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_promotion_article;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "Incident")
public class PromotionArticleIncident {

    private List<PromotionArticleDataGroup> promotionArticleDataGroups;

    public List<PromotionArticleDataGroup> getPromotionArticleDataGroups() {
        return promotionArticleDataGroups;
    }

    @XmlElement(name = "DataGroup")
    public void setPromotionArticleDataGroups(List<PromotionArticleDataGroup> promotionArticleDataGroups) {
        this.promotionArticleDataGroups = promotionArticleDataGroups;
    }

}
