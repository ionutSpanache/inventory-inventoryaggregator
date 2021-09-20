package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.aricle_cache_isu_istem_store_merchandise;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "domain")
public class DomainIsuItemStoreMerchandise implements Serializable {

    private String company;
    private String country;
    private String scopeName;
    private Double amountOfBaseItems;
    private int bundleNumber;
    private int variantNumber;
    private int articleNumber;
    private String environment;
    private String key;
    private String dataSource;
    private String connector;
    private int storeNumber;

    public int getStoreNumber() {
        return storeNumber;
    }

    @XmlElement(name = "storeNumber")
    public void setStoreNumber(int storeNumber) {
        this.storeNumber = storeNumber;
    }

    public String getCompany() {
        return company;
    }

    @XmlElement(name = "company")
    public void setCompany(String company) {
        this.company = company;
    }

    public String getCountry() {
        return country;
    }

    @XmlElement(name = "country")
    public void setCountry(String country) {
        this.country = country;
    }

    public String getScopeName() {
        return scopeName;
    }

    @XmlElement(name = "scopeName")
    public void setScopeName(String scopeName) {
        this.scopeName = scopeName;
    }

    public Double getAmountOfBaseItems() {
        return amountOfBaseItems;
    }

    @XmlElement(name = "amountOfBaseItems")
    public void setAmountOfBaseItems(Double amountOfBaseItems) {
        this.amountOfBaseItems = amountOfBaseItems;
    }

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

    public String getEnvironment() {
        return environment;
    }

    @XmlElement(name = "environment")
    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getKey() {
        return key;
    }

    @XmlElement(name = "key")
    public void setKey(String key) {
        this.key = key;
    }

    public String getDataSource() {
        return dataSource;
    }

    @XmlElement(name = "dataSource")
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getConnector() {
        return connector;
    }

    @XmlElement(name = "connector")
    public void setConnector(String connector) {
        this.connector = connector;
    }
}
