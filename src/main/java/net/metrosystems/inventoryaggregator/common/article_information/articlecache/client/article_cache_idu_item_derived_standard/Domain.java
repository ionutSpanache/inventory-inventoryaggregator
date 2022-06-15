package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard;

import com.sun.xml.txw2.annotation.XmlElement;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "domain")
public class Domain implements Serializable {
    private Long articleNumber;
    private Long variantNumber;
    private Long bundleNumber;

    public Long getArticleNumber() {
        return articleNumber;
    }

    @XmlElement("articleNumber")
    public void setArticleNumber(Long articleNumber) {
        this.articleNumber = articleNumber;
    }

    public Long getVariantNumber() {
        return variantNumber;
    }

    @XmlElement("variantNumber")
    public void setVariantNumber(Long variantNumber) {
        this.variantNumber = variantNumber;
    }

    public Long getBundleNumber() {
        return bundleNumber;
    }

    @XmlElement("bundleNumber")
    public void setBundleNumber(Long bundleNumber) {
        this.bundleNumber = bundleNumber;
    }
}
