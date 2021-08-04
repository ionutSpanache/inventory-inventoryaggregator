package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_isu_item_master;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "readerDigest")
public class ReaderDigest implements Serializable {

    private int bundleNumber;
    private int variantNumber;
    private int articleNumber;


    public int getBundleNumber() {
        return bundleNumber;
    }

    @XmlElement(name = "bundleNumber")
    public void setBundleNumber(int bundleNumber) {
        this.bundleNumber = bundleNumber;
    }

    public int getVariantNumber() {
        return variantNumber;
    }

    @XmlElement(name = "variantNumber")
    public void setVariantNumber(int variantNumber) {
        this.variantNumber = variantNumber;
    }

    public int getArticleNumber() {
        return articleNumber;
    }

    @XmlElement(name = "articleNumber")
    public void setArticleNumber(int articleNumber) {
        this.articleNumber = articleNumber;
    }
}
