package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_promotion_article;

import java.util.List;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;

@XmlRootElement(name = "DataGroup")
@Getter
public class PromotionArticleDataGroup {

  private String id;
  private Double promotionNetSalesPricePerBundle;
  private String promotionActiveEndDate;
  private String promotionId;
  private Double promotionNetSalesPricePerPiece;
  private String promotionActiveStartDate;
  private String promotionChannel;
  private Boolean validity;

  private List<PromotionDescription> descriptions;

  @XmlElement(name = "PROMOTION_END")
  public void setPromotionActiveEndDate(String promotionActiveEndDate) {
    this.promotionActiveEndDate = promotionActiveEndDate;
  }

  @XmlElement(name = "PROMOTION_ID")
  public void setPromotionId(String promotionId) {
    this.promotionId = promotionId;
  }

  @XmlElement(name = "PROMOTION_NET_SALES_PRICE_PER_BUNDLE")
  public void setPromotionNetSalesPricePerBundle(Double promotionNetSalesPricePerBundle) {
    this.promotionNetSalesPricePerBundle = promotionNetSalesPricePerBundle;
  }

  @XmlAttribute(name = "id")
  public void setId(String id) {
    this.id = id;
  }

  @XmlElement(name = "PROMOTION_NET_SALES_PRICE_PER_PIECE")
  public void setPromotionNetSalesPricePerPiece(Double promotionNetSalesPricePerPiece) {
    this.promotionNetSalesPricePerPiece = promotionNetSalesPricePerPiece;
  }

  @XmlElement(name = "PROMOTION_START")
  public void setPromotionActiveStartDate(String promotionActiveStartDate) {
    this.promotionActiveStartDate = promotionActiveStartDate;
  }

  @XmlElement(name = "VALIDITY")
  public void setValidity(Boolean validity) {
    this.validity = validity;
  }

  @XmlElement(name = "PROMOTION_DESCRIPTION")
  public void setDescriptions(List<PromotionDescription> descriptions) {
    this.descriptions = descriptions;
  }
  @XmlElement(name = "PROMO_CHANNEL")
  public void setPromotionChannel(String promotionChannel) {
    this.promotionChannel = promotionChannel;
  }
}
