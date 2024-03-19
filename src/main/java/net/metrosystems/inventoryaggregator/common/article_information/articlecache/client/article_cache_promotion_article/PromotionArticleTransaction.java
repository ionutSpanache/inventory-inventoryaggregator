package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_promotion_article;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Transaction")
public class PromotionArticleTransaction {

    private PromotionArticleIncident promotionArticleIncident;

    public PromotionArticleIncident getPromotionArticleIncident() {
        return promotionArticleIncident;
    }

    @XmlElement(name="Incident")
    public void setPromotionArticleIncident(PromotionArticleIncident promotionArticleIncident) {
        this.promotionArticleIncident = promotionArticleIncident;
    }
}
