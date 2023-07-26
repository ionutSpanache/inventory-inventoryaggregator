package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.aricle_cache_isu_istem_store_merchandise;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "storeInformationGroup")
public class StoreInformationGroup implements Serializable {

  private int groupingId;
  private Long originalSupplierNumber;
  private String channelDeliveryFlag;
  private StoreItemPhaseOutInformation storeItemPhaseOutInformation;

  public int getGroupingId() {
    return groupingId;
  }

  @XmlAttribute(name = "groupingId")
  public void setGroupingId(int groupingId) {
    this.groupingId = groupingId;
  }

  public Long getOriginalSupplierNumber() {
    return originalSupplierNumber;
  }

  @XmlElement(name = "originalSupplierNumber")
  public void setOriginalSupplierNumber(Long originalSupplierNumber) {
    this.originalSupplierNumber = originalSupplierNumber;
  }

  public String getChannelDeliveryFlag() {
    return channelDeliveryFlag;
  }

  @XmlElement(name = "ChannelDeliveryFlag")
  public void setChannelDeliveryFlag(String channelDeliveryFlag) {
    this.channelDeliveryFlag = channelDeliveryFlag;
  }

  public StoreItemPhaseOutInformation getStoreItemPhaseOutInformation() {
    return storeItemPhaseOutInformation;
  }

  @XmlElement(name = "storeItemPhaseOutInformation")
  public void setStoreItemPhaseOutInformation(
      StoreItemPhaseOutInformation storeItemPhaseOutInformation ) {
    this.storeItemPhaseOutInformation = storeItemPhaseOutInformation ;
  }
}
