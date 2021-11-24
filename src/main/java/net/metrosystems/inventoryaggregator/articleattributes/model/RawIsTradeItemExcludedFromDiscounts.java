package net.metrosystems.inventoryaggregator.articleattributes.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class RawIsTradeItemExcludedFromDiscounts {
    private Boolean value;
    private Date validFrom;
    private Long articleNumber;
    private Long bundleNumber;
    private Long variantNumber;
}
