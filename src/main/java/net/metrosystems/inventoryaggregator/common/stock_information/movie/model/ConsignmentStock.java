package net.metrosystems.inventoryaggregator.common.stock_information.movie.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsignmentStock {

    private String country;
    private String salesLine;
    private Integer locationId;
    private Integer articleNo;
    private Integer variant;
    private Integer bundleNo;
    private String bundleContent;
    private Double consignmentStock;
    private String message;
    private Integer returnCode;
}
