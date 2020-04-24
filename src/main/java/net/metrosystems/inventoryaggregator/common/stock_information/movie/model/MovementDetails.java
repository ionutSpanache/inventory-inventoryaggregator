package net.metrosystems.inventoryaggregator.common.stock_information.movie.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovementDetails {

    private MovementDetailsSystemStock systemStock;
    private MovementDetailsMarkDownStock markDownStock;

}
