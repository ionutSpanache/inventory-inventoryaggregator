package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "merchandiseArticleDescription")
public class MerchandiseArticleDescription {

    private String language;
    private String description;

    public String getLanguage() {
        return language;
    }

    @XmlAttribute(name = "lang")
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
