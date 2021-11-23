package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import java.io.Serializable;
import java.util.Date;

@XmlRootElement(name = "isTradeItemExcludedFromDiscounts")
public class IsTradeItemExcludedFromDiscounts implements Serializable {

    private Boolean value;
    private Date validFrom;

    public Boolean getValue() {
        return value;
    }

    @XmlValue
    public void setValue(Boolean value) {
        this.value = value;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    @XmlAttribute(name = "validFrom")
    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }
}
