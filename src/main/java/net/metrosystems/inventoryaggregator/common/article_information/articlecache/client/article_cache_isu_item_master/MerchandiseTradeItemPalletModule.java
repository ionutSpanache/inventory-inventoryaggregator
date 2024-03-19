package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_isu_item_master;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "merchandiseTradeitemPalletDataModule")
public class MerchandiseTradeItemPalletModule implements Serializable {

    private Long tradeItemsPerPallet;

    public Long getTradeItemsPerPallet() {
        return tradeItemsPerPallet;
    }

    @XmlElement(name = "merchandiseQuantityOfTradeItemsPerPallet")
    public void setTradeItemsPerPallet(Long tradeItemsPerPallet) {
        this.tradeItemsPerPallet = tradeItemsPerPallet;
    }

    public Long getTradeItemsPerLayer() {
        return tradeItemsPerLayer;
    }

    @XmlElement(name = "merchandiseQuantityOfTradeItemsPerPalletLayer")
    public void setTradeItemsPerLayer(Long tradeItemsPerLayer) {
        this.tradeItemsPerLayer = tradeItemsPerLayer;
    }

    private Long tradeItemsPerLayer;
}
