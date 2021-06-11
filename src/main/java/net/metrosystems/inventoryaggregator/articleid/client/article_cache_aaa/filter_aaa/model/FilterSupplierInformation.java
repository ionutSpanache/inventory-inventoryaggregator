package net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterSupplierInformation {

  private List<FilterKeySupplierInformation> filterKeys;

  @JsonProperty("filter-keys")
  public List<FilterKeySupplierInformation> getFilterKeys() {
    return filterKeys;
  }
}
