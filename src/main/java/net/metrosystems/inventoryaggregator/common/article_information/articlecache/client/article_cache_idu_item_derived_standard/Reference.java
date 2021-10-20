package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "reference")
public class Reference {

    private List<Label> labels;

    private String refKey;

    private String refName;

    public String getRefName() {
        return refName;
    }

    @XmlAttribute(name = "refName")
    public void setRefName(String refName) {
        this.refName = refName;
    }

    public String getRefKey() {
        return refKey;
    }

    @XmlAttribute(name = "refKey")
    public void setRefKey(String refKey) {
        this.refKey = refKey;
    }

    public List<Label> getLabels() {
        return labels;
    }

    @XmlElement(name = "label")
    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }
}
