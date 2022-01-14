package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard;

import java.util.Date;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "gradeCodeReferenceFruitsVegetables")
public class GradeCodeReferenceFruitsVegetables {

  private String value;
  private Date validFrom;

  public String getValue() {
    return value;
  }

  @XmlValue
  public void setValue(String value) {
    this.value = value;
  }

  public Date getValidFrom() {
    return validFrom;
  }

  @XmlAttribute(name = "validFrom")
  public void setValidFrom(Date validFrom) {
    this.validFrom = validFrom;
  }
}
