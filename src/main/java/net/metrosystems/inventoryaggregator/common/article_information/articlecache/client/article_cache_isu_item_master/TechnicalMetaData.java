package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_isu_item_master;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "technicalMetadata")
public class TechnicalMetaData implements Serializable {

    private String timeCreationAi;
    private String targetKey;
    private Double versionOfAttributeSet;
    private String startDateOfValidity;

    public String getTimeCreationAi() {
        return timeCreationAi;
    }

    @XmlElement(name = "timeCreationAi")
    public void setTimeCreationAi(String timeCreationAi) {
        this.timeCreationAi = timeCreationAi;
    }

    public String getTargetKey() {
        return targetKey;
    }

    @XmlElement(name = "targetKey")
    public void setTargetKey(String targetKey) {
        this.targetKey = targetKey;
    }

    public Double getVersionOfAttributeSet() {
        return versionOfAttributeSet;
    }

    @XmlElement(name = "versionOfAttributeSet")
    public void setVersionOfAttributeSet(Double versionOfAttributeSet) {
        this.versionOfAttributeSet = versionOfAttributeSet;
    }

    public String getStartDateOfValidity() {
        return startDateOfValidity;
    }

    @XmlElement(name = "startDateOfValidity")
    public void setStartDateOfValidity(String startDateOfValidity) {
        this.startDateOfValidity = startDateOfValidity;
    }
}
