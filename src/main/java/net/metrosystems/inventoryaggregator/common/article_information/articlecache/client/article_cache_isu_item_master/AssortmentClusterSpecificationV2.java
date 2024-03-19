package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_isu_item_master;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "assortmentClusterSpecificationV2")
public class AssortmentClusterSpecificationV2 implements Serializable {
    private PresencePeriodV3 presencePeriodV3;
    private String assortmentClusterV2;

    public String getAssortmentClusterV2() {
        return assortmentClusterV2;
    }

    @XmlElement(name = "assortmentClusterV2")
    public void setAssortmentClusterV2(String assortmentClusterV2) {
        this.assortmentClusterV2 = assortmentClusterV2;
    }

    public PresencePeriodV3 getPresencePeriodV3() {
        return presencePeriodV3;
    }

    @XmlElement(name = "presencePeriodV3")
    public void setPresencePeriodV3(PresencePeriodV3 presencePeriodV3) {
        this.presencePeriodV3 = presencePeriodV3;
    }
}
