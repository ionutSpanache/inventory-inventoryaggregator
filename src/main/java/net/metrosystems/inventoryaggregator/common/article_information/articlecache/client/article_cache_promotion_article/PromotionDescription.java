package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_promotion_article;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "PROMOTION_DESCRIPTION")
public class PromotionDescription implements Serializable {

  private String language;
  private String description;

  public String getLanguage() {
    return language;
  }

  @XmlAttribute(name = "languageCode")
  public void setLanguage(String language) {
    this.language = language;
  }

  public String getDescription() {
    return description;
  }

  @XmlValue
  public void setDescription(String description) {
    this.description = description;
  }
}
