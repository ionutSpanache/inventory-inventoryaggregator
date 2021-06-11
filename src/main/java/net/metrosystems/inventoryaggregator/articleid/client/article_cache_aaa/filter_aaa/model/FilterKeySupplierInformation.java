package net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterKeySupplierInformation {

  private String hardKeyDocument;
  private String country;
  private String salesLine;
  private Long supplierNumber;

  @JsonProperty("hard-key")
  public String getHardKey() {
    return "/"
        + country
        + "/"
        + hardKeyDocument
        + "?company="
        + salesLine
        + "&supno="
        + supplierNumber;
  }

  @JsonIgnore
  public String getHardKeyDocument() {
    return hardKeyDocument;
  }

  @JsonIgnore
  public String getCountry() {
    return country;
  }

  @JsonIgnore
  public String getSalesLine() {
    return salesLine;
  }

  @JsonIgnore
  public Long getSupplierNumber() {
    return supplierNumber;
  }
}
