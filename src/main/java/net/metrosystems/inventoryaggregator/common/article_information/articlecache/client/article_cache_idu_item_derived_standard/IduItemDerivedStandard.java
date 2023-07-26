package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "ihdsUniversal")
public class IduItemDerivedStandard {
  private ContentIduItemDerivedStandard contentIduItemDerivedStandard;
  private References references;
  private Domain domain;

  public ContentIduItemDerivedStandard getContentIduItemDerivedStandard() {
    return contentIduItemDerivedStandard;
  }

  @XmlElement(name = "content")
  public void setContentIduItemDerivedStandard(
      ContentIduItemDerivedStandard contentIduItemDerivedStandard) {
    this.contentIduItemDerivedStandard = contentIduItemDerivedStandard;
  }

  @XmlElement(name = "references")
  public void setReferences(References references) {
    this.references = references;
  }

  public References getReferences() {
    return references;
  }

  public Domain getDomain() {
    return domain;
  }

  @XmlElement(name = "domain")
  public void setDomain(Domain domain) {
    this.domain = domain;
  }
}
