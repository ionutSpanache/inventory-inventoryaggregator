package net.metrosystems.inventoryaggregator.stocklocation.acl.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.metrosystems.inventoryaggregator.stocklocation.acl.model.AclBundleStockLocation;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties
public class AclArticleStockLocation {
  private Long articleNumber;
  private Integer variantNumber;
  private Integer bundleNumber;
  private List<AclBundleStockLocation> aisleBay;
}
