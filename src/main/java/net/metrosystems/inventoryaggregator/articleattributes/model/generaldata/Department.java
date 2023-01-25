package net.metrosystems.inventoryaggregator.articleattributes.model.generaldata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Department {
  private Integer departmentNumber;
  private String departmentDescription;
}
