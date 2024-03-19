package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_isu_item_master;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "importClassification")
public class ImportClassification implements Serializable {

    private String importClassificationTypeCode;
    private String importClassificationValue;

    public String getImportClassificationTypeCode() {
        return importClassificationTypeCode;
    }

    @XmlElement(name = "importClassificationTypeCode")
    public void setImportClassificationTypeCode(String importClassificationTypeCode) {
        this.importClassificationTypeCode = importClassificationTypeCode;
    }

    public String getImportClassificationValue() {
        return importClassificationValue;
    }

    @XmlElement(name = "importClassificationValue")
    public void setImportClassificationValue(String importClassificationValue) {
        this.importClassificationValue = importClassificationValue;
    }
}
