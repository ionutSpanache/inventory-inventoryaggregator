package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.aricle_cache_isu_istem_store_merchandise;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "storeItemPhaseOutInformation")
public class StoreItemPhaseOutInformation implements Serializable {

  private Long returnPossibilityStoreSupplierNumber;

  public Long getReturnPossibilityStoreSupplierNumber() {
    return returnPossibilityStoreSupplierNumber;
  }

  @XmlElement(name = "returnPossibilityStoreSupplierNumber")
  public void setReturnPossibilityStoreSupplierNumber(Long returnPossibilityStoreSupplierNumber) {
    this.returnPossibilityStoreSupplierNumber = returnPossibilityStoreSupplierNumber;
  }
}
