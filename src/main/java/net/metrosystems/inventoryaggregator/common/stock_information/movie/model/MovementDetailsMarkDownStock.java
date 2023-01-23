package net.metrosystems.inventoryaggregator.common.stock_information.movie.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovementDetailsMarkDownStock {

    @JsonProperty("LABEL")
    private Float LABEL;

    @JsonProperty("SALES")
    private Float SALES;
}
