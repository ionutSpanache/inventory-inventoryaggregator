package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlValue;
import java.io.Serializable;

@XmlRootElement(name = "merchandiseArticleDescription")
public class MerchandiseArticleDescription implements Serializable {

    private static final long serialVersionUID = 213963540204750744L;

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
