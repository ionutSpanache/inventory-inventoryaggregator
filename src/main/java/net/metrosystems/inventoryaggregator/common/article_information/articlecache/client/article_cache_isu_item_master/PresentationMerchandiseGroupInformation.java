package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_isu_item_master;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "presentationMerchandiseGroupInformation")
public class PresentationMerchandiseGroupInformation implements Serializable {

 private Long presentationMerchandiseMainGroup;

    public Long getPresentationMerchandiseMainGroup() {
        return presentationMerchandiseMainGroup;
    }

    @XmlElement(name = "presentationMerchandiseMainGroup")
    public void setPresentationMerchandiseMainGroup(Long presentationMerchandiseMainGroup) {
        this.presentationMerchandiseMainGroup = presentationMerchandiseMainGroup;
    }

    public String getPresentationMerchandiseGroup() {
        return presentationMerchandiseGroup;
    }

    @XmlElement(name = "presentationMerchandiseGroup")
    public void setPresentationMerchandiseGroup(String presentationMerchandiseGroup) {
        this.presentationMerchandiseGroup = presentationMerchandiseGroup;
    }

    public String getPresentationMerchandiseSubGroup() {
        return presentationMerchandiseSubGroup;
    }

    @XmlElement(name = "presentationMerchandiseSubGroup")
    public void setPresentationMerchandiseSubGroup(String presentationMerchandiseSubGroup) {
        this.presentationMerchandiseSubGroup = presentationMerchandiseSubGroup;
    }

    private String presentationMerchandiseGroup;
 private String presentationMerchandiseSubGroup;

}
