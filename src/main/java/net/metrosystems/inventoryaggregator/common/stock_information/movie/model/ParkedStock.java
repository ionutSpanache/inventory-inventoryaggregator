package net.metrosystems.inventoryaggregator.common.stock_information.movie.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParkedStock {
    private String grDate;
    private Double mrp;
    private Double quantity;
}
