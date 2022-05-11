package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_promotion_article;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DataGroup")
public class PromotionArticleDataGroup {

    private String id;
    private Double promotionNetSalesPricePerBundle;

    public String getPromotionActiveEndDate() {
        return promotionActiveEndDate;
    }

    @XmlElement(name = "PROMOTION_END")
    public void setPromotionActiveEndDate(String promotionActiveEndDate) {
        this.promotionActiveEndDate = promotionActiveEndDate;
    }

    private String promotionActiveEndDate;

    public String getPromotionId() {
        return promotionId;
    }

    @XmlElement(name = "PROMOTION_ID")
    public void setPromotionId(String promotionId) {
        this.promotionId = promotionId;
    }

    private String promotionId;

    public Double getPromotionNetSalesPricePerBundle() {
        return promotionNetSalesPricePerBundle;
    }

    @XmlElement(name = "PROMOTION_NET_SALES_PRICE_PER_BUNDLE")
    public void setPromotionNetSalesPricePerBundle(Double promotionNetSalesPricePerBundle) {
        this.promotionNetSalesPricePerBundle = promotionNetSalesPricePerBundle;
    }

    public String getId() {
        return id;
    }

    @XmlAttribute(name = "id")
    public void setId(String id) {
        this.id = id;
    }

}
