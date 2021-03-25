package net.metrosystems.inventoryaggregator.common.stock_information.movie.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsigmentStock {

    private Float quantity;
    private String lastChange;
}
