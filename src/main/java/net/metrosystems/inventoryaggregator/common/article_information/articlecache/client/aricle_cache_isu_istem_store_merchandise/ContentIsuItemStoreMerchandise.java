package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.aricle_cache_isu_istem_store_merchandise;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

@XmlRootElement(name = "content")
public class ContentIsuItemStoreMerchandise implements Serializable {

    private List<StoreInformationGroup> storeInformationGroups;

    public List<StoreInformationGroup> getStoreInformationGroups() {
        return storeInformationGroups;
    }

    @XmlElement(name = "storeInformationGroup")
    public void setStoreInformationGroups(List<StoreInformationGroup> storeInformationGroups) {
        this.storeInformationGroups = storeInformationGroups;
    }
}
