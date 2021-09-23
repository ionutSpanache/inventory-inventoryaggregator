package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.aricle_cache_isu_istem_store_merchandise;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "readerDigest")
public class ReaderDigestIsuItemStoreMerchandise implements Serializable {

    private int bundleNumber;
    private int variantNumber;

    public int getStoreNumber() {
        return storeNumber;
    }

    @XmlElement(name = "storeNumber")
    public void setStoreNumber(int storeNumber) {
        this.storeNumber = storeNumber;
    }

    private int articleNumber;
    private int storeNumber;


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
