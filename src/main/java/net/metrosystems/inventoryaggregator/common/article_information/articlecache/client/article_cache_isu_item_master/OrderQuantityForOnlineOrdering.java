package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_isu_item_master;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "orderQuantityForOnlineOrdering")
public class OrderQuantityForOnlineOrdering implements Serializable {

    private Boolean isOrderableOnlyInMultipleOfMinimumOrderQuantityForOnlineOrdering;

    public Boolean getOrderableOnlyInMultipleOfMinimumOrderQuantityForOnlineOrdering() {
        return isOrderableOnlyInMultipleOfMinimumOrderQuantityForOnlineOrdering;
    }

    @XmlElement(name = "isOrderableOnlyInMultipleOfMinimumOrderQuantityForOnlineOrdering")
    public void setOrderableOnlyInMultipleOfMinimumOrderQuantityForOnlineOrdering(Boolean orderableOnlyInMultipleOfMinimumOrderQuantityForOnlineOrdering) {
        isOrderableOnlyInMultipleOfMinimumOrderQuantityForOnlineOrdering = orderableOnlyInMultipleOfMinimumOrderQuantityForOnlineOrdering;
    }
}
