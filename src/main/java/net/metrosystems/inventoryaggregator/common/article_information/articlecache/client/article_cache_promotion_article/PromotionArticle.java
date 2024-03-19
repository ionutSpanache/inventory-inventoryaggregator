package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_promotion_article;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="IhdsTree")
public class PromotionArticle {

  private PromotionArticleTransaction promotionArticleTransaction;

    public PromotionArticleTransaction getPromotionArticleTransaction() {
        return promotionArticleTransaction;
    }

    @XmlElement(name="Transaction")
    public void setPromotionArticleTransaction(PromotionArticleTransaction promotionArticleTransaction) {
        this.promotionArticleTransaction = promotionArticleTransaction;
    }
}
