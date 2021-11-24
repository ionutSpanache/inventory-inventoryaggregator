package net.metrosystems.inventoryaggregator.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreId {

    private String countryCode;
    private String salesLine;
    private Integer storeNumber;
}
