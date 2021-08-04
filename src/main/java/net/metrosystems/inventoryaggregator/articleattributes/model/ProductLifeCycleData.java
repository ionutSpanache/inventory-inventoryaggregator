package net.metrosystems.inventoryaggregator.articleattributes.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductLifeCycleData implements Serializable {

    private Long presencePeriodEndYear;
    private Long presencePeriodEndWeek;
    private Long presencePeriodStartYear;
    private Long presencePeriodStartWeek;
    private String assortmentCluster;

}
