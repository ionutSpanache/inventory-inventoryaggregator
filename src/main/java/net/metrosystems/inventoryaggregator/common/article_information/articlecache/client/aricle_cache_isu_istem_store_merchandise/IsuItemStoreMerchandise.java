package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.aricle_cache_isu_istem_store_merchandise;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "ihdsUniversal")
public class IsuItemStoreMerchandise implements Serializable  {

    private DomainIsuItemStoreMerchandise domainIsuItemStoreMerchandise;
    private ReaderDigestIsuItemStoreMerchandise readerDigestIsuItemStoreMerchandise;
    private TechnicalMetaDataIsuItemStoreMerchandise technicalMetaDataIsuItemStoreMerchandise;
    private ContentIsuItemStoreMerchandise contentIsuItemStoreMerchandise;

    public DomainIsuItemStoreMerchandise getDomainIsuItemStoreMerchandise() {
        return domainIsuItemStoreMerchandise;
    }

    @XmlElement(name = "domain")
    public void setDomainIsuItemStoreMerchandise(DomainIsuItemStoreMerchandise domainIsuItemStoreMerchandise) {
        this.domainIsuItemStoreMerchandise = domainIsuItemStoreMerchandise;
    }

    public ReaderDigestIsuItemStoreMerchandise getReaderDigestIsuItemStoreMerchandise() {
        return readerDigestIsuItemStoreMerchandise;
    }

    @XmlElement(name = "readerDigest")
    public void setReaderDigestIsuItemStoreMerchandise(ReaderDigestIsuItemStoreMerchandise readerDigestIsuItemStoreMerchandise) {
        this.readerDigestIsuItemStoreMerchandise = readerDigestIsuItemStoreMerchandise;
    }

    public TechnicalMetaDataIsuItemStoreMerchandise getTechnicalMetaDataIsuItemStoreMerchandise() {
        return technicalMetaDataIsuItemStoreMerchandise;
    }

    @XmlElement(name = "technicalMetadata")
    public void setTechnicalMetaDataIsuItemStoreMerchandise(TechnicalMetaDataIsuItemStoreMerchandise technicalMetaDataIsuItemStoreMerchandise) {
        this.technicalMetaDataIsuItemStoreMerchandise = technicalMetaDataIsuItemStoreMerchandise;
    }

    public ContentIsuItemStoreMerchandise getContentIsuItemStoreMerchandise() {
        return contentIsuItemStoreMerchandise;
    }

    @XmlElement(name = "content")
    public void setContentIsuItemStoreMerchandise(ContentIsuItemStoreMerchandise contentIsuItemStoreMerchandise) {
        this.contentIsuItemStoreMerchandise = contentIsuItemStoreMerchandise;
    }
}
