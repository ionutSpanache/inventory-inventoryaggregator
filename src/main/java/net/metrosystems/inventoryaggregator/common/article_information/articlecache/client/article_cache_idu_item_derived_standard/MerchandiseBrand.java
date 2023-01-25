package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

@XmlRootElement(name = "merchandiseBrand")
public class MerchandiseBrand implements Serializable {

    private Boolean isMerchandiseOwnBrand;
    private Date validFrom;

    public Boolean getIsMerchandiseOwnBrand() {
        return isMerchandiseOwnBrand;
    }

    @XmlElement(name = "isMerchandiseOwnBrand")
    public void setIsMerchandiseOwnBrand(Boolean isMerchandiseOwnBrand) {
        this.isMerchandiseOwnBrand = isMerchandiseOwnBrand;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    @XmlAttribute(name = "validFrom")
    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }
}
