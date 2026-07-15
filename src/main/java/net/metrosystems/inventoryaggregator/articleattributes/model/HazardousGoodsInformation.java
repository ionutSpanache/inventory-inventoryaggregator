package net.metrosystems.inventoryaggregator.articleattributes.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HazardousGoodsInformation implements Serializable {
  private String technicalUnId;
  private String hazardousGoodsId;
  private String chemicalDescription;

  private String hazardousLabel;
  private String packagingGroup;
  private String multiplicator;
}
