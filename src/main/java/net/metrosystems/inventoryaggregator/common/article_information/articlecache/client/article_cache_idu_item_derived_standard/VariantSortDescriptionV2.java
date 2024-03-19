package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlValue;
import java.io.Serializable;
import java.util.Date;

@XmlRootElement(name = "variantSortDescriptionV2")
public class VariantSortDescriptionV2 implements Serializable {

    private String language;
    private String description;
    private Date validFrom;

    public Date getValidFrom() {
        return validFrom;
    }

    @XmlAttribute(name = "validFrom")
    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

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
