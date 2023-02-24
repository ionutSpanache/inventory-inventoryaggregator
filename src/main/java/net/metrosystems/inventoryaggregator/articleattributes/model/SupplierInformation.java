package net.metrosystems.inventoryaggregator.articleattributes.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupplierInformation {

  private String generalSupplierName;
  private String originalSupplierName;
  private Integer returnCode;
}
