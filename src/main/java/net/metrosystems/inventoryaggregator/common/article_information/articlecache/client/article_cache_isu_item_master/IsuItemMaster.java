package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_isu_item_master;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "ihdsUniversal")
public class IsuItemMaster implements Serializable {

    private Domain domain;
    private ReaderDigest readerDigest;
    private TechnicalMetaData technicalMetaData;
    private Content content;

    public Content getContent() {
        return content;
    }

    @XmlElement(name = "content")
    public void setContent(Content content) {
        this.content = content;
    }

    public Domain getDomain() {
        return domain;
    }

    @XmlElement(name = "domain")
    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public ReaderDigest getReaderDigest() {
        return readerDigest;
    }

    @XmlElement(name = "readerDigest")
    public void setReaderDigest(ReaderDigest readerDigest) {
        this.readerDigest = readerDigest;
    }

    public TechnicalMetaData getTechnicalMetaData() {
        return technicalMetaData;
    }

    @XmlElement(name = "technicalMetadata")
    public void setTechnicalMetaData(TechnicalMetaData technicalMetaData) {
        this.technicalMetaData = technicalMetaData;
    }


}
