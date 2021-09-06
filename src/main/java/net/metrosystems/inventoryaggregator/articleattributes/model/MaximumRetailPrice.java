package net.metrosystems.inventoryaggregator.articleattributes.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaximumRetailPrice implements Serializable {

    private Long articleNumber;
    private Integer variantNumber;
    private Integer sellingBundleNumber;
    private String validFrom;
    private String validTo;
    private Integer mrpIndicator;
    private List<MaximumRetailPriceDetails> mrpDetails;

}
