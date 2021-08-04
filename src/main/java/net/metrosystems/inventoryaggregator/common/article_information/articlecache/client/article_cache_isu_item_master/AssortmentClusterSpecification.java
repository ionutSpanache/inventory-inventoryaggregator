package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_isu_item_master;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "assortmentClusterSpecification")
public class AssortmentClusterSpecification implements Serializable {
    private PresencePeriodV2 presencePeriodV2;
    private String assortmentCluster;

    public String getAssortmentCluster() {
        return assortmentCluster;
    }

    @XmlElement(name = "assortmentCluster")
    public void setAssortmentCluster(String assortmentCluster) {
        this.assortmentCluster = assortmentCluster;
    }

    public PresencePeriodV2 getPresencePeriodV2() {
        return presencePeriodV2;
    }

    @XmlElement(name = "presencePeriodV2")
    public void setPresencePeriodV2(PresencePeriodV2 presencePeriodV2) {
        this.presencePeriodV2 = presencePeriodV2;
    }
}
