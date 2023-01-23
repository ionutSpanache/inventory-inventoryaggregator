package net.metrosystems.inventoryaggregator.common.stock_information.movie.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovementDetailsSystemStock {

    @JsonProperty("GR")
    private Float goodsReceivingQuantity;

    @JsonProperty("SALES")
    private Float saleQuantity;

    @JsonProperty("SC")
    private Float stockCorrectionQuantity;
}
