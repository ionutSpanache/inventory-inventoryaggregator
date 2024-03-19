package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_isu_item_master;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "purchaseAreaInformation")
public class PurchaseAreaInformation implements Serializable {
    public Long getPurchaseAreaNumber() {
        return purchaseAreaNumber;
    }

    @XmlElement(name = "purchaseAreaNumber")
    public void setPurchaseAreaNumber(Long purchaseAreaNumber) {
        this.purchaseAreaNumber = purchaseAreaNumber;
    }

    private Long purchaseAreaNumber;
}
