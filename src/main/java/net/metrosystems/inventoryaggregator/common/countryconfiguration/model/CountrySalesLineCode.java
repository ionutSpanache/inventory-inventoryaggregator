package net.metrosystems.inventoryaggregator.common.countryconfiguration.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountrySalesLineCode {
  private String countryCode;
  private String salesLine;
  private Integer salesLineCode;
}
