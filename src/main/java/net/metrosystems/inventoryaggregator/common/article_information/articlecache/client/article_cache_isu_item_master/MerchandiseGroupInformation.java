package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_isu_item_master;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "merchandiseGroupInformation")
public class MerchandiseGroupInformation implements Serializable {

    private Long merchandiseMainGroup;
    private String merchandiseGroup;
    private String merchandiseSubGroup;

    public Long getMerchandiseMainGroup() {
        return merchandiseMainGroup;
    }

    @XmlElement(name = "merchandiseMainGroup")
    public void setMerchandiseMainGroup(Long merchandiseMainGroup) {
        this.merchandiseMainGroup = merchandiseMainGroup;
    }

    public String getMerchandiseGroup() {
        return merchandiseGroup;
    }

    @XmlElement(name = "merchandiseGroup")
    public void setMerchandiseGroup(String merchandiseGroup) {
        this.merchandiseGroup = merchandiseGroup;
    }

    public String getMerchandiseSubGroup() {
        return merchandiseSubGroup;
    }

    @XmlElement(name = "merchandiseSubGroup")
    public void setMerchandiseSubGroup(String merchandiseSubGroup) {
        this.merchandiseSubGroup = merchandiseSubGroup;
    }
}
