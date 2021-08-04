package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_isu_item_master;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "merchandiseBrand")
public class MerchandiseBrand {

    private int merchandiseBrandId;
    private Boolean isMerchandiseOwnBrand;

    public int getMerchandiseBrandId() {
        return merchandiseBrandId;
    }

    @XmlElement(name = "merchandiseBrandId")
    public void setMerchandiseBrandId(int merchandiseBrandId) {
        this.merchandiseBrandId = merchandiseBrandId;
    }

    public Boolean getMerchandiseOwnBrand() {
        return isMerchandiseOwnBrand;
    }

    @XmlElement(name = "isMerchandiseOwnBrand")
    public void setMerchandiseOwnBrand(Boolean merchandiseOwnBrand) {
        isMerchandiseOwnBrand = merchandiseOwnBrand;
    }
}
