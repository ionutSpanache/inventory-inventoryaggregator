package inventory.aggregator.common.stock_location.acl.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedAclBundleStockLocation {

    List<AclBundleStockLocation> stockLocations;

}
