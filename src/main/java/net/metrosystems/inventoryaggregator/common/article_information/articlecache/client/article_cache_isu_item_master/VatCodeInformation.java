package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_isu_item_master;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "vatCodeInformation")
public class VatCodeInformation implements Serializable {

    private String vatCode;

    public String getVatCode() {
        return vatCode;
    }

    @XmlElement(name = "vatCode")
    public void setVatCode(String vatCode) {
        this.vatCode = vatCode;
    }

    public String getDutyFeeTaxCategoryCodeVat() {
        return dutyFeeTaxCategoryCodeVat;
    }

    @XmlElement(name = "dutyFeeTaxCategoryCodeVat")
    public void setDutyFeeTaxCategoryCodeVat(String dutyFeeTaxCategoryCodeVat) {
        this.dutyFeeTaxCategoryCodeVat = dutyFeeTaxCategoryCodeVat;
    }

    private String dutyFeeTaxCategoryCodeVat;
}
