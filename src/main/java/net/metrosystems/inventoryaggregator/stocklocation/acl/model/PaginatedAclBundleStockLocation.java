package net.metrosystems.inventoryaggregator.stocklocation.acl.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import net.metrosystems.inventoryaggregator.stocklocation.acl.model.AclBundleStockLocation;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedAclBundleStockLocation {

    List<AclBundleStockLocation> stockLocations;

}
